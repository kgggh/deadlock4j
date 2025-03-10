package com.gnnny.deadlock4j.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter ISO_FORMATTER =
        DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.systemDefault());

    public static String format(long timestamp) {
        return DEFAULT_FORMATTER.format(Instant.ofEpochMilli(timestamp));
    }

    public static String formatIso(long timestamp) {
        return ISO_FORMATTER.format(Instant.ofEpochMilli(timestamp));
    }

    public static String formatCustom(long timestamp, String pattern) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
        return customFormatter.format(Instant.ofEpochMilli(timestamp));
    }
}
