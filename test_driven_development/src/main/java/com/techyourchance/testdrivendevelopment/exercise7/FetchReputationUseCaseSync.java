package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    private GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public enum UseCaseStatus {
        FAILURE,
        SUCCESS
    }

    class UseCaseResult {

        int reputation;
        UseCaseStatus status;

        UseCaseResult(int reputation, UseCaseStatus status) {
            this.reputation = reputation;
            this.status = status;
        }

        int getReputation() {
            return reputation;
        }

        UseCaseStatus getStatus() {
            return status;
        }
    }

    UseCaseResult fetchReputationSync() {
        GetReputationHttpEndpointSync.EndpointResult reputationSync = getReputationHttpEndpointSync.getReputationSync();
        if (reputationSync.getStatus() == GetReputationHttpEndpointSync.EndpointStatus.SUCCESS) {
            return new UseCaseResult(reputationSync.getReputation(), UseCaseStatus.SUCCESS);
        } else {
            return new UseCaseResult(0, UseCaseStatus.FAILURE);
        }
    }
}