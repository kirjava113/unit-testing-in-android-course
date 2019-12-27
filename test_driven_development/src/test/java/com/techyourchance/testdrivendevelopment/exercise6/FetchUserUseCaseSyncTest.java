package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncTest {

    // region constants ----------------------------------------------------------------------------

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final User USER = new User(USER_ID, USERNAME);

    // endregion constants -------------------------------------------------------------------------


    // region helper fields ------------------------------------------------------------------------
    @Mock
    private UsersCache usersCacheMock;
    private FetchUserHttpEndpointSyncImpl fetchUserHttpEndpointSync;

    // endregion helper fields ---------------------------------------------------------------------

    private FetchUserUseCaseSync SUT;

    @Before
    public void setup() throws Exception {
        fetchUserHttpEndpointSync = new FetchUserHttpEndpointSyncImpl();

         SUT = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSync, usersCacheMock);

        userNotInCache();
        endpointSuccess();
    }

    @Test
    public void fetchUser_success_successReturned() throws NetworkErrorException {
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUser_success_userInCache() throws NetworkErrorException {
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUser_successWhenUserNotInCache_userInCache() throws NetworkErrorException {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.fetchUserSync(USER_ID);
        verify(usersCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue(), is(USER));
    }

    @Test
    public void fetchUser_successWhenUserNotInCache_userPassedToEndpoint() throws NetworkErrorException {
        SUT.fetchUserSync(USER_ID);
        assertThat(fetchUserHttpEndpointSync.userId, is(USER_ID));
    }

    @Test
    public void fetchUser_successUserInCache_successReturned() throws NetworkErrorException {
        userInCache();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUser_successUserInCache_fetchInEndpointNotCalled() throws NetworkErrorException {
        userInCache();
        SUT.fetchUserSync(USER_ID);
        assertThat(fetchUserHttpEndpointSync.mRequestCount, is(0));
    }

    @Test
    public void fetchUser_successUserInCache_goodUserReturned() throws NetworkErrorException {
        userInCache();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), Is.is(USER));
    }

    @Test
    public void fetchUser_failureNetwork_networkErrorReturned() throws NetworkErrorException {
        endpointNetworkError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUser_failureAuth_failureReturned() throws NetworkErrorException {
        endpointAuthError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUser_failureServer_failureReturned() throws NetworkErrorException {
        endpointServerError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUser_failureNetwork_userNotInCache() throws NetworkErrorException {
        endpointNetworkError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUser_failureAuth_userNotInCache() throws NetworkErrorException {
        endpointAuthError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUser_failureServer_userNotInCache() throws NetworkErrorException {
        endpointServerError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUser_failureNetwork_nullUserReturned() throws NetworkErrorException {
        endpointNetworkError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), nullValue());
    }

    @Test
    public void fetchUser_failureAuth_nullUserReturned() throws NetworkErrorException {
        endpointAuthError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), nullValue());
    }

    @Test
    public void fetchUser_failureServer_nullUserReturned() throws NetworkErrorException {
        endpointServerError();
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), nullValue());
    }

    // region helper methods -----------------------------------------------------------------------

    private void userNotInCache() {
        when(usersCacheMock.getUser(anyString())).thenReturn(null);
    }

    private void userInCache() {
        when(usersCacheMock.getUser(anyString())).thenReturn(USER);
    }

    private void endpointSuccess() {
        fetchUserHttpEndpointSync.userId = USER_ID;
    }

    private void endpointAuthError() {
        fetchUserHttpEndpointSync.mAuthError = true;
    }

    private void endpointServerError() {
        fetchUserHttpEndpointSync.mServerError = true;
    }

    private void endpointNetworkError() {
        fetchUserHttpEndpointSync.mNetworkError = true;
    }

    // endregion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------

    public class FetchUserHttpEndpointSyncImpl implements FetchUserHttpEndpointSync {

        private int mRequestCount = 0;
        private String userId = "";

        public boolean mAuthError;
        public boolean mServerError;
        public boolean mNetworkError;

        @Override
        public EndpointResult fetchUserSync(String userId) throws NetworkErrorException {
            mRequestCount ++;
            this.userId = userId;

            if (mAuthError) {
                return new EndpointResult(EndpointStatus.AUTH_ERROR, "", "");
            } else if (mServerError) {
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, "", "");
            } else if (mNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USERNAME);
            }
        }
    }


}