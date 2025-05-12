package com.alibou.book.common;

import com.alibou.book.Entity.GenderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // Automatically applies to all GenderStatus fields
public class GenderStatusConverter implements AttributeConverter<GenderStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(GenderStatus genderStatus) {
        if (genderStatus == null) {
            return null;
        }
        return genderStatus.getValue(); // Returns 1 (MALE) or 2 (FEMALE)
    }

    @Override
    public GenderStatus convertToEntityAttribute(Integer dbValue) {
        if (dbValue == null) {
            return null;
        }
        return GenderStatus.fromValue(dbValue); // Converts 1 → MALE, 2 → FEMALE
    }
}