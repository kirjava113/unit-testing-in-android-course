package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private UsersCache usersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        User result = usersCache.getUser(userId);

        if (result == null) {
            try {
                FetchUserHttpEndpointSync.EndpointResult endpointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);
                if (endpointResult.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.SUCCESS) {
                    result = new User(endpointResult.getUserId(), endpointResult.getUsername());
                    usersCache.cacheUser(result);
                } else if (endpointResult.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR ||
                        endpointResult.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR) {
                    return new UseCaseResult(Status.FAILURE, null);
                }
            } catch (NetworkErrorException e) {
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }
        }
        return new UseCaseResult(Status.SUCCESS, result);
    }
}
