/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

public enum Formatting {
    BLACK("BLACK", '0', 0, 0),
    DARK_BLUE("DARK_BLUE", '1', 1, 170),
    DARK_GREEN("DARK_GREEN", '2', 2, 43520),
    DARK_AQUA("DARK_AQUA", '3', 3, 43690),
    DARK_RED("DARK_RED", '4', 4, 0xAA0000),
    DARK_PURPLE("DARK_PURPLE", '5', 5, 0xAA00AA),
    GOLD("GOLD", '6', 6, 0xFFAA00),
    GRAY("GRAY", '7', 7, 0xAAAAAA),
    DARK_GRAY("DARK_GRAY", '8', 8, 0x555555),
    BLUE("BLUE", '9', 9, 0x5555FF),
    GREEN("GREEN", 'a', 10, 0x55FF55),
    AQUA("AQUA", 'b', 11, 0x55FFFF),
    RED("RED", 'c', 12, 0xFF5555),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 0xFF55FF),
    YELLOW("YELLOW", 'e', 14, 0xFFFF55),
    WHITE("WHITE", 'f', 15, 0xFFFFFF),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1, null);

    private static final Map<String, Formatting> BY_NAME;
    private static final Pattern FORMATTING_CODE_PATTERN;
    private final String name;
    private final char code;
    private final boolean modifier;
    private final String stringValue;
    private final int colorIndex;
    @Nullable
    private final Integer colorValue;

    private static String sanitize(String string) {
        return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private Formatting(String string2, @Nullable char c, int j, Integer integer) {
        this(string2, c, false, j, integer);
    }

    private Formatting(String string2, char c, boolean bl) {
        this(string2, c, bl, -1, null);
    }

    private Formatting(String string2, char c, @Nullable boolean bl, int j, Integer integer) {
        this.name = string2;
        this.code = c;
        this.modifier = bl;
        this.colorIndex = j;
        this.colorValue = integer;
        this.stringValue = "\u00a7" + c;
    }

    @Environment(value=EnvType.CLIENT)
    public static String getFormatAtEnd(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = -1;
        int j = string.length();
        while ((i = string.indexOf(167, i + 1)) != -1) {
            Formatting formatting;
            if (i >= j - 1 || (formatting = Formatting.byCode(string.charAt(i + 1))) == null) continue;
            if (formatting.affectsGlyphWidth()) {
                stringBuilder.setLength(0);
            }
            if (formatting == RESET) continue;
            stringBuilder.append((Object)formatting);
        }
        return stringBuilder.toString();
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public boolean isModifier() {
        return this.modifier;
    }

    public boolean isColor() {
        return !this.modifier && this != RESET;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Integer getColorValue() {
        return this.colorValue;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean affectsGlyphWidth() {
        return !this.modifier;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.stringValue;
    }

    @Nullable
    public static String strip(@Nullable String string) {
        return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
    }

    @Nullable
    public static Formatting byName(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return BY_NAME.get(Formatting.sanitize(string));
    }

    @Nullable
    public static Formatting byColorIndex(int i) {
        if (i < 0) {
            return RESET;
        }
        for (Formatting formatting : Formatting.values()) {
            if (formatting.getColorIndex() != i) continue;
            return formatting;
        }
        return null;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Formatting byCode(char c) {
        char d = Character.toString(c).toLowerCase(Locale.ROOT).charAt(0);
        for (Formatting formatting : Formatting.values()) {
            if (formatting.code != d) continue;
            return formatting;
        }
        return null;
    }

    public static Collection<String> getNames(boolean bl, boolean bl2) {
        ArrayList<String> list = Lists.newArrayList();
        for (Formatting formatting : Formatting.values()) {
            if (formatting.isColor() && !bl || formatting.isModifier() && !bl2) continue;
            list.add(formatting.getName());
        }
        return list;
    }

    static {
        BY_NAME = Arrays.stream(Formatting.values()).collect(Collectors.toMap(formatting -> Formatting.sanitize(formatting.name), formatting -> formatting));
        FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

