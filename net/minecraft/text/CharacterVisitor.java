/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import net.minecraft.text.Style;

/**
 * A visitor for single characters in a string.
 */
@FunctionalInterface
public interface CharacterVisitor {
    /**
     * Visits a single character.
     * 
     * <p>Multiple surrogate characters are converted into one single {@code
     * codePoint} when passed into this method.
     * 
     * @return {@code true} to continue visiting other characters, or {@code false} to terminate the visit
     * 
     * @param index the current index of the character
     * @param style the style of the character, containing formatting and font information
     * @param codePoint the code point of the character
     */
    public boolean accept(int var1, Style var2, int var3);
}

