/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ChatUtil {
    private static final Pattern PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern LINE_BREAK = Pattern.compile("\\r\\n|\\v");
    private static final Pattern ENDS_WITH_LINE_BREAK = Pattern.compile("(?:\\r\\n|\\v)$");

    public static String ticksToString(int ticks) {
        int i = ticks / 20;
        int j = i / 60;
        if ((i %= 60) < 10) {
            return j + ":0" + i;
        }
        return j + ":" + i;
    }

    public static String stripTextFormat(String text) {
        return PATTERN.matcher(text).replaceAll("");
    }

    public static boolean isEmpty(@Nullable String text) {
        return StringUtils.isEmpty(text);
    }

    public static String truncate(String text, int maxLength, boolean addEllipsis) {
        if (text.length() <= maxLength) {
            return text;
        }
        if (addEllipsis && maxLength > 3) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text.substring(0, maxLength);
    }

    public static int countLines(String text) {
        if (text.isEmpty()) {
            return 0;
        }
        Matcher matcher = LINE_BREAK.matcher(text);
        int i = 1;
        while (matcher.find()) {
            ++i;
        }
        return i;
    }

    public static boolean endsWithLineBreak(String text) {
        return ENDS_WITH_LINE_BREAK.matcher(text).find();
    }
}

