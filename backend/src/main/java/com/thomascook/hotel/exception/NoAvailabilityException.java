package com.thomascook.hotel.exception;

import com.thomascook.hotel.dto.AlternativeWindow;

import java.util.List;

public class NoAvailabilityException extends RuntimeException {

    private final List<AlternativeWindow> alternatives;

    public NoAvailabilityException(List<AlternativeWindow> alternatives) {
        super("No rooms available for the selected dates");
        this.alternatives = alternatives;
    }

    public List<AlternativeWindow> getAlternatives() {
        return alternatives;
    }
}
