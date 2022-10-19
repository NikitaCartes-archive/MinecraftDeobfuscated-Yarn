/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import java.util.Optional;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

/**
 * A utility class for visiting the characters of strings, handling surrogate
 * code points and formatting codes.
 */
public class TextVisitFactory {
    private static final char REPLACEMENT_CHARACTER = '\ufffd';
    private static final Optional<Object> VISIT_TERMINATED = Optional.of(Unit.INSTANCE);

    private static boolean visitRegularCharacter(Style style, CharacterVisitor visitor, int index, char c) {
        if (Character.isSurrogate(c)) {
            return visitor.accept(index, style, 65533);
        }
        return visitor.accept(index, style, c);
    }

    /**
     * Visits the code points of a string in forward (left to right) direction.
     * 
     * @return {@code true} if the full string was visited, or {@code false} indicating
     * the {@code visitor} terminated half-way
     * 
     * @param style the style of the string
     * @param text the string
     * @param visitor the visitor of characters
     */
    public static boolean visitForwards(String text, Style style, CharacterVisitor visitor) {
        int i = text.length();
        for (int j = 0; j < i; ++j) {
            char c = text.charAt(j);
            if (Character.isHighSurrogate(c)) {
                if (j + 1 >= i) {
                    if (visitor.accept(j, style, 65533)) break;
                    return false;
                }
                char d = text.charAt(j + 1);
                if (Character.isLowSurrogate(d)) {
                    if (!visitor.accept(j, style, Character.toCodePoint(c, d))) {
                        return false;
                    }
                    ++j;
                    continue;
                }
                if (visitor.accept(j, style, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    /**
     * Visits the code points of a string in backward (right to left) direction.
     * 
     * @return {@code true} if the full string was visited, or {@code false} indicating
     * the {@code visitor} terminated half-way
     * 
     * @param style the style of the string
     * @param text the string
     * @param visitor the visitor
     */
    public static boolean visitBackwards(String text, Style style, CharacterVisitor visitor) {
        int i = text.length();
        for (int j = i - 1; j >= 0; --j) {
            char c = text.charAt(j);
            if (Character.isLowSurrogate(c)) {
                if (j - 1 < 0) {
                    if (visitor.accept(0, style, 65533)) break;
                    return false;
                }
                char d = text.charAt(j - 1);
                if (!(Character.isHighSurrogate(d) ? !visitor.accept(--j, style, Character.toCodePoint(d, c)) : !visitor.accept(j, style, 65533))) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    /**
     * Visits the code points of a string, applying the formatting codes within.
     * 
     * <p>The visit is in forward direction.
     * 
     * @return {@code true} if the full string was visited, or {@code false} indicating
     * the {@code visitor} terminated half-way
     * 
     * @param text the string visited
     * @param visitor the visitor
     * @param style the style of the string
     */
    public static boolean visitFormatted(String text, Style style, CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(text, 0, style, visitor);
    }

    /**
     * Visits the code points of a string, applying the formatting codes within.
     * 
     * <p>The visit is in forward direction.
     * 
     * @return {@code true} if the full string was visited, or {@code false} indicating
     * the {@code visitor} terminated half-way
     * 
     * @param style the style of the string
     * @param startIndex the starting index of the visit
     * @param text the string visited
     */
    public static boolean visitFormatted(String text, int startIndex, Style style, CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(text, startIndex, style, style, visitor);
    }

    /**
     * Visits the code points of a string, applying the formatting codes within.
     * 
     * <p>The visit is in forward direction.
     * 
     * @return {@code true} if the full string was visited, or {@code false} indicating
     * the {@code visitor} terminated half-way
     * 
     * @param text the string visited
     * @param startIndex the starting index of the visit
     * @param startingStyle the style of the string when the visit starts
     * @param resetStyle the style to reset to when a {@code \u00a7r} formatting code is encountered
     * @param visitor the visitor
     */
    public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
        int i = text.length();
        Style style = startingStyle;
        for (int j = startIndex; j < i; ++j) {
            char d;
            char c = text.charAt(j);
            if (c == '\u00a7') {
                if (j + 1 >= i) break;
                d = text.charAt(j + 1);
                Formatting formatting = Formatting.byCode(d);
                if (formatting != null) {
                    style = formatting == Formatting.RESET ? resetStyle : style.withExclusiveFormatting(formatting);
                }
                ++j;
                continue;
            }
            if (Character.isHighSurrogate(c)) {
                if (j + 1 >= i) {
                    if (visitor.accept(j, style, 65533)) break;
                    return false;
                }
                d = text.charAt(j + 1);
                if (Character.isLowSurrogate(d)) {
                    if (!visitor.accept(j, style, Character.toCodePoint(c, d))) {
                        return false;
                    }
                    ++j;
                    continue;
                }
                if (visitor.accept(j, style, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    /**
     * Visits the code points for every literal string and the formatting codes
     * supplied by the renderable.
     * 
     * <p>The visit is in forward direction.
     * 
     * @return {@code true} if the full string was visited, or {@code false} indicating
     * the {@code visitor} terminated half-way
     * @see StringVisitable#visit(StringVisitable.StyledVisitor, Style)
     */
    public static boolean visitFormatted(StringVisitable text, Style style2, CharacterVisitor visitor) {
        return !text.visit((style, string) -> TextVisitFactory.visitFormatted(string, 0, style, visitor) ? Optional.empty() : VISIT_TERMINATED, style2).isPresent();
    }

    /**
     * {@return a new string that has all surrogate characters within}
     * The characters are validated from an original string {@code text}.
     * 
     * @param text the original string
     */
    public static String validateSurrogates(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        TextVisitFactory.visitForwards(text, Style.EMPTY, (index, style, codePoint) -> {
            stringBuilder.appendCodePoint(codePoint);
            return true;
        });
        return stringBuilder.toString();
    }

    public static String removeFormattingCodes(StringVisitable text) {
        StringBuilder stringBuilder = new StringBuilder();
        TextVisitFactory.visitFormatted(text, Style.EMPTY, (int index, Style style, int codePoint) -> {
            stringBuilder.appendCodePoint(codePoint);
            return true;
        });
        return stringBuilder.toString();
    }
}

