package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    private static final int REPUTATION = 975;

    @Mock
    GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;

    private FetchReputationUseCaseSync SUT;

    @Before
    public void setUp() {
        SUT = new FetchReputationUseCaseSync(getReputationHttpEndpointSyncMock);
        success();
    }

    @Test
    public void fetchReputationSync_success_correctReputationReturn() {
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void fetchReputationSync_success_statusSuccessReturn() {
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseStatus.SUCCESS));
    }

    @Test
    public void fetchReputationSync_generalError_reputation0Return() {
        generalError();
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        assertThat(result.getReputation(), is(0));
    }

    @Test
    public void fetchReputationSync_generalError_statusFailureReturn() {
        generalError();
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseStatus.FAILURE));
    }

    @Test
    public void fetchReputationSync_networkError_reputation0Return() {
        networkError();
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        assertThat(result.getReputation(), is(0));
    }

    @Test
    public void fetchReputationSync_networkError_statusFailureReturn() {
        networkError();
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseStatus.FAILURE));
    }

    private void success() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(
                        GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, REPUTATION));
    }

    private void generalError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(
                        GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, 0));
    }

    private void networkError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(
                        GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR, 0));
    }

}