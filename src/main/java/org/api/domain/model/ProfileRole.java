package org.api.domain.model;


public enum ProfileRole {

    USER("USER"),
    ADMIN("ADMIN");

    private String role;

    ProfileRole(String role) {this.role = role;}

    public String getRole(){return role;}


}
