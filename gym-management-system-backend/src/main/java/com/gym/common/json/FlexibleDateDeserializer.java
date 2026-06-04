package com.gym.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FlexibleDateDeserializer extends JsonDeserializer<Date> {
    private static final List<String> PATTERNS = List.of(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    );

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        try {
            return Date.from(OffsetDateTime.parse(normalized).toInstant());
        } catch (DateTimeParseException ignored) {
            // Fall through to local browser/backend date formats.
        }

        for (String pattern : PATTERNS) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                format.setLenient(false);
                return format.parse(normalized);
            } catch (ParseException ignored) {
                // Try the next browser/backend date format.
            }
        }

        return (Date) context.handleWeirdStringValue(Date.class, normalized, "Expected yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss, or yyyy-MM-dd");
    }
}
