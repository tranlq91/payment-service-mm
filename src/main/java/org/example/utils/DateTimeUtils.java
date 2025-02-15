package org.example.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static String localTimeToStringFormat() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(dateFormatter);
    }
}
