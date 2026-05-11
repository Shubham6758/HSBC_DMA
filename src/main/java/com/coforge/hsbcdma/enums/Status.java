package com.coforge.hsbcdma.enums;

/**
 * Created by pratish.b
 */
public enum Status {

    OPEN_POSITIONS("Open Positions"),
    CLOSED_POSITIONS("Closed Positions"),
    ON_HOLD("On Hold"),
    ABANDONED("Abandoned"),
    FULFILLED("Fulfilled"),
    ON_BOARDING_INPROGRESS("On Boarding InProgress"),
    PROFILE_SHARED("Profile Shared"),
    REJECTED("Rejected"),
    SOFT_SELECT("Soft Select"),
    CANDIDATE_RESIGNED("Candidate Resigned"),
    DUPLICATE_DEMAND("Duplicate Demand");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
