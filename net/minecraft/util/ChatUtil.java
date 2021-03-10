/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ChatUtil {
    private static final Pattern PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern field_29204 = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");

    @Environment(value=EnvType.CLIENT)
    public static String ticksToString(int ticks) {
        int i = ticks / 20;
        int j = i / 60;
        if ((i %= 60) < 10) {
            return j + ":0" + i;
        }
        return j + ":" + i;
    }

    @Environment(value=EnvType.CLIENT)
    public static String stripTextFormat(String text) {
        return PATTERN.matcher(text).replaceAll("");
    }

    public static boolean isEmpty(@Nullable String string) {
        return StringUtils.isEmpty(string);
    }

    @Environment(value=EnvType.CLIENT)
    public static int method_34238(String string) {
        if (string.isEmpty()) {
            return 0;
        }
        Matcher matcher = field_29204.matcher(string);
        int i = 1;
        while (matcher.find()) {
            ++i;
        }
        return i;
    }
}

