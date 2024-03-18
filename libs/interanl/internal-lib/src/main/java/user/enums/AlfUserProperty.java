package user.enums;

import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum AlfUserProperty {
    NAME("name"),
    EMAIL("email"),
    PERMISSIONS("permissions"),
    PASSWORD("password");

    private final String name;

    AlfUserProperty(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name.toLowerCase();
    }

    @JsonCreator
    public static AlfUserProperty fromString(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.toString().equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid user property"));
    }
}
