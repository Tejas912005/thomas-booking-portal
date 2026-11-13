package com.thomascook.hotel.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelBookingRequest {

    @NotBlank(message = "Email is required")
    private String email;

    public CancelBookingRequest() {}

    public CancelBookingRequest(String email) {
        this.email = email;
    }

    public static CancelBookingRequestBuilder builder() {
        return new CancelBookingRequestBuilder();
    }

    public static class CancelBookingRequestBuilder {
        private String email;
        public CancelBookingRequestBuilder email(String email) { this.email = email; return this; }
        public CancelBookingRequest build() {
            return new CancelBookingRequest(email);
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
