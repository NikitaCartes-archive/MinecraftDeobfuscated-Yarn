/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ChatUtil {
    private static final Pattern PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    @Environment(value=EnvType.CLIENT)
    public static String ticksToString(int i) {
        int j = i / 20;
        int k = j / 60;
        if ((j %= 60) < 10) {
            return k + ":0" + j;
        }
        return k + ":" + j;
    }

    @Environment(value=EnvType.CLIENT)
    public static String stripTextFormat(String text) {
        return PATTERN.matcher(text).replaceAll("");
    }

    public static boolean isEmpty(@Nullable String string) {
        return StringUtils.isEmpty(string);
    }
}

