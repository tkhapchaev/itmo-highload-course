package ru.tkhapchaev.electionservice.entity;

import java.util.Arrays;

public enum ElectionStatusCode {
    OPEN(0),
    ACTIVE(1),
    CLOSED(2),
    CANCELED(3);

    private final int id;

    ElectionStatusCode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ElectionStatusCode fromId(int id) {
        return Arrays.stream(values())
                .filter(code -> code.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown election status id: " + id));
    }
}
