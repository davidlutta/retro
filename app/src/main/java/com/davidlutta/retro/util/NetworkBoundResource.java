package com.davidlutta.retro.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.davidlutta.retro.Api.ApiResponse;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private static final String TAG = "NetworkBoundResource";

    private MediatorLiveData<Resource<ResultType>> results = new MediatorLiveData<Resource<ResultType>>();
    private AppExecutors appExecutors;

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init() {
        results.setValue((Resource<ResultType>) Resource.loading(null));

        //Observe LiveData from Source from db
        final LiveData<ResultType> dbSource = loadFromDb();

        results.addSource(dbSource, resultType -> {
            // Remove observer from local db. Need to decide if read local db or network
            results.removeSource(dbSource);

            // get data from network if conditions in shouldFetch(boolean) are true
            if (shouldFetch(resultType)) {
                fetchFromNetwork(dbSource);
            } else { // Otherwise read data from local db
                results.addSource(dbSource, resultType1 -> {
                    setValue(Resource.success(resultType1));
                });
            }
        });
    }


    /**
     * 1) observe local db
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     *
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        results.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        results.addSource(apiResponse, response -> {
            results.removeSource(apiResponse);
            results.removeSource(dbSource);
            Log.d(TAG, "run: attempting to refresh data from network...");

                /*
                    3 Cases:
                        1) ApiSuccessResponse
                        2) ApiErrorResponse
                        3) ApiEmptyResponse
                */

            if (response instanceof ApiResponse.ApiSuccessResponse) {
                Log.d(TAG, "fetchFromNetwork: ApiSuccessResponse");
                appExecutors.diskIO().execute(() -> {
                    // save response to local db
                    saveCallResult((RequestType) processResponse((ApiResponse.ApiSuccessResponse) response));
                    appExecutors.mainThread().execute(() -> {
                        results.addSource(loadFromDb(), newData -> {
                            setValue(Resource.success(newData));
                        });
                    });
                });
            } else if (response instanceof ApiResponse.ApiEmptyResponse) { // Empty Response
                Log.d(TAG, "fetchFromNetwork: ApiEmptyResponse");
                appExecutors.mainThread().execute(() -> {
                    results.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));
                });
            } else if (response instanceof ApiResponse.ApiErrorResponse) { // error Response
                Log.d(TAG, "fetchFromNetwork: ApiErrorResponse");
                onFetchFailed();
                results.addSource(dbSource, resultType -> setValue(Resource.error(((ApiResponse.ApiErrorResponse) response).getErrorMessage(), resultType)));
            }
        });
    }

    /**
     * Setting new value to LiveData
     * Must be done on MainThread
     *
     * @param newValue
     */
    private void setValue(Resource<ResultType> newValue) {
        if (results.getValue() != newValue) {
            results.setValue(newValue);
        }
    }

    private ResultType processResponse(ApiResponse.ApiSuccessResponse response) {
        return (ResultType) response.getBody();
    }

    // Called to save the result of the API response into the database.
    public abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    public abstract boolean shouldFetch(@Nullable ResultType data);


    // Called to get the cached data from the database.
    @NonNull
    public abstract LiveData<ResultType> loadFromDb();


    // Called to create the API call.
    @NonNull
    public abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    public abstract void onFetchFailed();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return results;
    }

}
