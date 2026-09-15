package ru.sbrf.platformmanagement.ufs.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlagType {
    USER("user"),
    GROUP("group"),
    DEFAULT("default");

    private final String wireValue;

    FlagType(String wireValue) {
        this.wireValue = wireValue;
    }

    @JsonValue
    public String wireValue() {
        return wireValue;
    }

    @JsonCreator
    public static FlagType fromWireValue(String value) {
        for (FlagType type : values()) {
            if (type.wireValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown flag type: " + value);
    }
}
