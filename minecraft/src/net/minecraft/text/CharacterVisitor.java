package net.minecraft.text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A visitor for single characters in a string.
 */
@FunctionalInterface
public interface CharacterVisitor {
	/**
	 * Visits a single character.
	 * 
	 * <p>Multiple surrogate characters are converted into one single {@code
	 * codePoint} when passed into this method.</p>
	 * 
	 * @return {@code true} to continue visiting other characters, or {@code false} to terminate the visit
	 * 
	 * @param index the current index of the character
	 * @param style the style of the character, containing formatting and font information
	 * @param codePoint the code point of the character
	 */
	@Environment(EnvType.CLIENT)
	boolean accept(int index, Style style, int codePoint);
}
