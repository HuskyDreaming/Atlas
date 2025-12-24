package com.huskydreaming.claims.model.flags;

public record ClaimFlag(String id) {
    public ClaimFlag {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Flag id must not be blank");
        }
    }
}