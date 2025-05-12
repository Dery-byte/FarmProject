package com.alibou.book.Entity;

public enum GenderStatus {
    MALE(1),
    FEMALE(2);

    private final int value;

    // Constructor to assign the value
    GenderStatus(int value) {
        this.value = value;
    }

    // Getter to retrieve the stored value (1 or 2)
    public int getValue() {
        return value;
    }

    // (Optional) Convert an integer back to GenderStatus
    public static GenderStatus fromValue(int value) {
        for (GenderStatus status : GenderStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid GenderStatus value: " + value);
    }
}