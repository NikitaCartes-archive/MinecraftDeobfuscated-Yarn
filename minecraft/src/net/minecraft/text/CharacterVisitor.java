package net.minecraft.text;

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
	 * @param style the style of the character, containing formatting and font information
	 * @param codePoint the code point of the character
	 * @param index the current index of the character
	 */
	boolean accept(int index, Style style, int codePoint);
}
