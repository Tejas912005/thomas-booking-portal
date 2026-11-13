package com.thomascook.hotel.dto;

public class FieldError {
    private String field;
    private String message;

    public FieldError() {}

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public static FieldErrorBuilder builder() {
        return new FieldErrorBuilder();
    }

    public static class FieldErrorBuilder {
        private String field;
        private String message;

        public FieldErrorBuilder field(String field) { this.field = field; return this; }
        public FieldErrorBuilder message(String message) { this.message = message; return this; }
        public FieldError build() {
            return new FieldError(field, message);
        }
    }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
