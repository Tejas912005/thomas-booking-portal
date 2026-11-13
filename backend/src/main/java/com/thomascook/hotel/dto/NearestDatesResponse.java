package com.thomascook.hotel.dto;

import java.util.List;

public class NearestDatesResponse {
    private List<AlternativeWindow> alternatives;

    public NearestDatesResponse() {}

    public NearestDatesResponse(List<AlternativeWindow> alternatives) {
        this.alternatives = alternatives;
    }

    public static NearestDatesResponseBuilder builder() {
        return new NearestDatesResponseBuilder();
    }

    public static class NearestDatesResponseBuilder {
        private List<AlternativeWindow> alternatives;
        public NearestDatesResponseBuilder alternatives(List<AlternativeWindow> alternatives) {
            this.alternatives = alternatives;
            return this;
        }
        public NearestDatesResponse build() {
            return new NearestDatesResponse(alternatives);
        }
    }

    public List<AlternativeWindow> getAlternatives() { return alternatives; }
    public void setAlternatives(List<AlternativeWindow> alternatives) { this.alternatives = alternatives; }
}
