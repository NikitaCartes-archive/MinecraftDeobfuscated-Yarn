/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum class_4357 {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    private static final Map<Character, class_4357> field_19634;
    private static final Map<String, class_4357> field_19635;
    private static final Pattern field_19636;
    private final char field_19637;
    private final boolean field_19609;
    private final String field_19610;

    private class_4357(char c) {
        this(c, false);
    }

    private class_4357(char c, boolean bl) {
        this.field_19637 = c;
        this.field_19609 = bl;
        this.field_19610 = "\u00a7" + c;
    }

    public char method_21061() {
        return this.field_19637;
    }

    public String method_21063() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.field_19610;
    }

    static {
        field_19634 = Arrays.stream(class_4357.values()).collect(Collectors.toMap(class_4357::method_21061, arg -> arg));
        field_19635 = Arrays.stream(class_4357.values()).collect(Collectors.toMap(class_4357::method_21063, arg -> arg));
        field_19636 = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

