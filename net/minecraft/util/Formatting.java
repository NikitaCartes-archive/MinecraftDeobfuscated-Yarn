/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

/**
 * An enum holding formattings.
 * 
 * <p>There are two types of formattings, color and modifier. Color formattings
 * are associated with a specific color, while modifier formattings modify the
 * style, such as by bolding the text. {@link #RESET} is a special formatting
 * and is not classified as either of these two.
 */
public enum Formatting implements StringIdentifiable
{
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

    public static final Codec<Formatting> CODEC;
    public static final char FORMATTING_CODE_PREFIX = '\u00a7';
    private static final Map<String, Formatting> BY_NAME;
    private static final Pattern FORMATTING_CODE_PATTERN;
    private final String name;
    private final char code;
    private final boolean modifier;
    private final String stringValue;
    private final int colorIndex;
    @Nullable
    private final Integer colorValue;

    private static String sanitize(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private Formatting(String name, @Nullable char code, int colorIndex, Integer colorValue) {
        this(name, code, false, colorIndex, colorValue);
    }

    private Formatting(String name, char code, boolean modifier) {
        this(name, code, modifier, -1, null);
    }

    private Formatting(String name, char code, @Nullable boolean modifier, int colorIndex, Integer colorValue) {
        this.name = name;
        this.code = code;
        this.modifier = modifier;
        this.colorIndex = colorIndex;
        this.colorValue = colorValue;
        this.stringValue = "\u00a7" + code;
    }

    /**
     * {@return the code to be placed after the {@value FORMATTING_CODE_PREFIX} when this format is converted to a string}
     */
    public char getCode() {
        return this.code;
    }

    /**
     * {@return the color index for the formatting, or {@code -1} to indicate no color}
     * 
     * @apiNote This is also used to calculate scoreboard team display slot IDs.
     */
    public int getColorIndex() {
        return this.colorIndex;
    }

    /**
     * {@return true if the formatting is a modifier, false otherwise}
     */
    public boolean isModifier() {
        return this.modifier;
    }

    /**
     * {@return true if the formatting is associated with a color, false otherwise}
     */
    public boolean isColor() {
        return !this.modifier && this != RESET;
    }

    /**
     * {@return the color of the formatted text, or {@code null} if the formatting
     * has no associated color}
     */
    @Nullable
    public Integer getColorValue() {
        return this.colorValue;
    }

    /**
     * {@return the name of the formatting}
     */
    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.stringValue;
    }

    /**
     * {@return the {@code text} with all formatting codes removed}
     * 
     * @see StringHelper#stripTextFormat
     */
    @Nullable
    public static String strip(@Nullable String string) {
        return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
    }

    /**
     * {@return the formatting with the name {@code name}, or {@code null} if there is none}
     */
    @Nullable
    public static Formatting byName(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return BY_NAME.get(Formatting.sanitize(name));
    }

    /**
     * {@return the formatting with the color index {@code colorIndex},
     * or {@code null} if there is none}
     */
    @Nullable
    public static Formatting byColorIndex(int colorIndex) {
        if (colorIndex < 0) {
            return RESET;
        }
        for (Formatting formatting : Formatting.values()) {
            if (formatting.getColorIndex() != colorIndex) continue;
            return formatting;
        }
        return null;
    }

    /**
     * {@return the formatting with the code {@code code}, or {@code null} if there is none}
     */
    @Nullable
    public static Formatting byCode(char code) {
        char c = Character.toString(code).toLowerCase(Locale.ROOT).charAt(0);
        for (Formatting formatting : Formatting.values()) {
            if (formatting.code != c) continue;
            return formatting;
        }
        return null;
    }

    /**
     * {@return the list of formattings matching the given condition}
     * 
     * @param colors whether or not to include color formattings
     * @param modifiers whether or not to include modifier formattings
     */
    public static Collection<String> getNames(boolean colors, boolean modifiers) {
        ArrayList<String> list = Lists.newArrayList();
        for (Formatting formatting : Formatting.values()) {
            if (formatting.isColor() && !colors || formatting.isModifier() && !modifiers) continue;
            list.add(formatting.getName());
        }
        return list;
    }

    @Override
    public String asString() {
        return this.getName();
    }

    static {
        CODEC = StringIdentifiable.createCodec(Formatting::values);
        BY_NAME = Arrays.stream(Formatting.values()).collect(Collectors.toMap(f -> Formatting.sanitize(f.name), f -> f));
        FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

