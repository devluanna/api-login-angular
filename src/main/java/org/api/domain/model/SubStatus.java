package org.api.domain.model;

public enum SubStatus {

    LOCKED("LOCKED, password expired"),
    // BLOCKED, PASSWORD EXPIRED AFTER 3 MONTHS, RECOVER A NEW PASSWORD
    ON_ALERT("ON ALERT, the password is about to expire"),
    // WHEN THE PASSWORD IS ABOUT TO EXPIRE EITHER IN THE CASE OF UPDATE PASSWORD
    // MANUAL HOW TO AFTER 3 MONTHS OF USING THE SAME PASSWORD
    IN_NON_COMPLIANCE("IN NON-COMPLIANCE, you haven't changed your password yet, the deadline is about to expire."),
    // NOT COMPLIANCE, YOU NEED
    // CHANGE THE PASSWORD MANUALLY BEFORE 7 DAYS
    BLOCKED("BLOCKED, user did not update password after first access"),
    // BLOCKED, USER DID NOT MANUALLY UPDATE PASSWORD AFTER 7 DAYS
    UNLOCKED("UNLOCKED, user compliant");
    // UNLOCKED AND COMPLIANT, PASSWORD AND USERNAME OK


    private String substatus_description;

    private SubStatus(String substatus_description) {this.substatus_description = substatus_description;}

    public String getSubStatus_description() {
        return substatus_description;
    }

}
