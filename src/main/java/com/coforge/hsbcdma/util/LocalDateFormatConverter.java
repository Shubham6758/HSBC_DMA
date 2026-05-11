package com.coforge.hsbcdma.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Converter(autoApply = false)
public class LocalDateFormatConverter implements AttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MMM-yy");

    @Override
    public String convertToDatabaseColumn(LocalDate date) {
        return (date == null) ? null : date.format(FMT);
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return LocalDate.parse(dbData, FMT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format (expected dd-MMM-yy): " + dbData, ex);
        }
    }
}
