/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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

public enum ChatFormat {
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

    private static final Map<String, ChatFormat> field_1052;
    private static final Pattern FORMAT_PATTERN;
    private final String field_1057;
    private final char sectionSignCode;
    private final boolean modifier;
    private final String code;
    private final int id;
    @Nullable
    private final Integer color;

    private static String sanitizeName(String string) {
        return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private ChatFormat(String string2, @Nullable char c, int j, Integer integer) {
        this(string2, c, false, j, integer);
    }

    private ChatFormat(String string2, char c, boolean bl) {
        this(string2, c, bl, -1, null);
    }

    private ChatFormat(String string2, char c, @Nullable boolean bl, int j, Integer integer) {
        this.field_1057 = string2;
        this.sectionSignCode = c;
        this.modifier = bl;
        this.id = j;
        this.color = integer;
        this.code = "\u00a7" + c;
    }

    @Environment(value=EnvType.CLIENT)
    public static String getFormatAtEnd(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = -1;
        int j = string.length();
        while ((i = string.indexOf(167, i + 1)) != -1) {
            ChatFormat chatFormat;
            if (i >= j - 1 || (chatFormat = ChatFormat.bySectionSignCode(string.charAt(i + 1))) == null) continue;
            if (chatFormat.affectsGlyphWidth()) {
                stringBuilder.setLength(0);
            }
            if (chatFormat == RESET) continue;
            stringBuilder.append((Object)chatFormat);
        }
        return stringBuilder.toString();
    }

    public int getId() {
        return this.id;
    }

    public boolean isModifier() {
        return this.modifier;
    }

    public boolean isColor() {
        return !this.modifier && this != RESET;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Integer getColor() {
        return this.color;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean affectsGlyphWidth() {
        return !this.modifier;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.code;
    }

    @Nullable
    public static String stripFormatting(@Nullable String string) {
        return string == null ? null : FORMAT_PATTERN.matcher(string).replaceAll("");
    }

    @Nullable
    public static ChatFormat getFormatByName(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return field_1052.get(ChatFormat.sanitizeName(string));
    }

    @Nullable
    public static ChatFormat byId(int i) {
        if (i < 0) {
            return RESET;
        }
        for (ChatFormat chatFormat : ChatFormat.values()) {
            if (chatFormat.getId() != i) continue;
            return chatFormat;
        }
        return null;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static ChatFormat bySectionSignCode(char c) {
        char d = Character.toString(c).toLowerCase(Locale.ROOT).charAt(0);
        for (ChatFormat chatFormat : ChatFormat.values()) {
            if (chatFormat.sectionSignCode != d) continue;
            return chatFormat;
        }
        return null;
    }

    public static Collection<String> getNames(boolean bl, boolean bl2) {
        ArrayList<String> list = Lists.newArrayList();
        for (ChatFormat chatFormat : ChatFormat.values()) {
            if (chatFormat.isColor() && !bl || chatFormat.isModifier() && !bl2) continue;
            list.add(chatFormat.getName());
        }
        return list;
    }

    static {
        field_1052 = Arrays.stream(ChatFormat.values()).collect(Collectors.toMap(chatFormat -> ChatFormat.sanitizeName(chatFormat.field_1057), chatFormat -> chatFormat));
        FORMAT_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

