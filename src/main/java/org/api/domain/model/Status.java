package org.api.domain.model;

public enum Status {
    UNAVAILABLE("UNAVAILABLE"),
    AVAILABLE("AVAILABLE");

    private String status_description;

    private Status(String status_description) {this.status_description = status_description;}

    public String getStatus_description() {
        return status_description;
    }
}
