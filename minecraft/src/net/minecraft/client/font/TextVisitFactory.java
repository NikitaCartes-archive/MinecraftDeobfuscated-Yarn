package net.minecraft.client.font;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

/**
 * A utility class for visiting the characters of strings, handling surrogate
 * code points and formatting codes.
 */
@Environment(EnvType.CLIENT)
public class TextVisitFactory {
	private static final Optional<Object> VISIT_TERMINATED = Optional.of(Unit.INSTANCE);

	private static boolean visitRegularCharacter(Style style, TextVisitFactory.CharacterVisitor visitor, int index, char c) {
		return Character.isSurrogate(c) ? visitor.onChar(index, style, 65533) : visitor.onChar(index, style, c);
	}

	/**
	 * Visits the code points of a string in forward (left to right) direction.
	 * 
	 * @return {@code true} if the full string was visited, or {@code false} indicating
	 * the {@code visitor} terminated half-way
	 * 
	 * @param text the string
	 * @param style the style of the string
	 * @param visitor the visitor of characters
	 */
	public static boolean visitForwards(String text, Style style, TextVisitFactory.CharacterVisitor visitor) {
		int i = text.length();

		for (int j = 0; j < i; j++) {
			char c = text.charAt(j);
			if (Character.isHighSurrogate(c)) {
				if (j + 1 >= i) {
					if (!visitor.onChar(j, style, 65533)) {
						return false;
					}
					break;
				}

				char d = text.charAt(j + 1);
				if (Character.isLowSurrogate(d)) {
					if (!visitor.onChar(j, style, Character.toCodePoint(c, d))) {
						return false;
					}

					j++;
				} else if (!visitor.onChar(j, style, 65533)) {
					return false;
				}
			} else if (!visitRegularCharacter(style, visitor, j, c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Visits the code points of a string in backward (right to left) direction.
	 * 
	 * @return {@code true} if the full string was visited, or {@code false} indicating
	 * the {@code visitor} terminated half-way
	 * 
	 * @param text the string
	 * @param style the style of the string
	 * @param visitor the visitor
	 */
	public static boolean visitBackwards(String text, Style style, TextVisitFactory.CharacterVisitor visitor) {
		int i = text.length();

		for (int j = i - 1; j >= 0; j--) {
			char c = text.charAt(j);
			if (Character.isLowSurrogate(c)) {
				if (j - 1 < 0) {
					if (!visitor.onChar(0, style, 65533)) {
						return false;
					}
					break;
				}

				char d = text.charAt(j - 1);
				if (Character.isHighSurrogate(d)) {
					if (!visitor.onChar(--j, style, Character.toCodePoint(d, c))) {
						return false;
					}
				} else if (!visitor.onChar(j, style, 65533)) {
					return false;
				}
			} else if (!visitRegularCharacter(style, visitor, j, c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Visits the code points of a string, applying the formatting codes within.
	 * 
	 * <p>The visit is in forward direction.</p>
	 * 
	 * @return {@code true} if the full string was visited, or {@code false} indicating
	 * the {@code visitor} terminated half-way
	 * 
	 * @param text the string visited
	 * @param style the style of the string
	 * @param visitor the visitor
	 */
	public static boolean visitFormatted(String text, Style style, TextVisitFactory.CharacterVisitor visitor) {
		return visitFormatted(text, 0, style, visitor);
	}

	/**
	 * Visits the code points of a string, applying the formatting codes within.
	 * 
	 * <p>The visit is in forward direction.</p>
	 * 
	 * @return {@code true} if the full string was visited, or {@code false} indicating
	 * the {@code visitor} terminated half-way
	 * 
	 * @param text the string visited
	 * @param startIndex the starting index of the visit
	 * @param style the style of the string
	 */
	public static boolean visitFormatted(String text, int startIndex, Style style, TextVisitFactory.CharacterVisitor visitor) {
		return visitFormatted(text, startIndex, style, style, visitor);
	}

	/**
	 * Visits the code points of a string, applying the formatting codes within.
	 * 
	 * <p>The visit is in forward direction.</p>
	 * 
	 * @return {@code true} if the full string was visited, or {@code false} indicating
	 * the {@code visitor} terminated half-way
	 * 
	 * @param text the string visited
	 * @param startIndex the starting index of the visit
	 * @param startingStyle the style of the string when the visit starts
	 * @param resetStyle the style to reset to when a {@code §r} formatting code is encountered
	 * @param visitor the visitor
	 */
	public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, TextVisitFactory.CharacterVisitor visitor) {
		int i = text.length();
		Style style = startingStyle;

		for (int j = startIndex; j < i; j++) {
			char c = text.charAt(j);
			if (c == 167) {
				if (j + 1 >= i) {
					break;
				}

				char d = text.charAt(j + 1);
				Formatting formatting = Formatting.byCode(d);
				if (formatting != null) {
					style = formatting == Formatting.RESET ? resetStyle : style.withExclusiveFormatting(formatting);
				}

				j++;
			} else if (Character.isHighSurrogate(c)) {
				if (j + 1 >= i) {
					if (!visitor.onChar(j, style, 65533)) {
						return false;
					}
					break;
				}

				char d = text.charAt(j + 1);
				if (Character.isLowSurrogate(d)) {
					if (!visitor.onChar(j, style, Character.toCodePoint(c, d))) {
						return false;
					}

					j++;
				} else if (!visitor.onChar(j, style, 65533)) {
					return false;
				}
			} else if (!visitRegularCharacter(style, visitor, j, c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Visits the code points for every {@link Text#asString() partial string}
	 * of the texts in {@code text} and its siblings, applying the formatting
	 * codes within.
	 * 
	 * <p>The visit is in forward direction.</p>
	 * 
	 * @return {@code true} if the full string was visited, or {@code false} indicating
	 * the {@code visitor} terminated half-way
	 * @see Text#visit(Text.StyledVisitor, Style)
	 */
	public static boolean visitFormatted(Text text, Style style, TextVisitFactory.CharacterVisitor visitor) {
		return !text.visit((stylex, string) -> visitFormatted(string, 0, stylex, visitor) ? Optional.empty() : VISIT_TERMINATED, style).isPresent();
	}

	/**
	 * Returns a new string that has all surrogate characters within validated
	 * from an original string.
	 * 
	 * @param text the original string
	 */
	public static String validateSurrogates(String text) {
		StringBuilder stringBuilder = new StringBuilder();
		visitForwards(text, Style.EMPTY, (i, style, j) -> {
			stringBuilder.appendCodePoint(j);
			return true;
		});
		return stringBuilder.toString();
	}

	/**
	 * A visitor for single characters in a string.
	 */
	@FunctionalInterface
	@Environment(EnvType.CLIENT)
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
		boolean onChar(int index, Style style, int codePoint);
	}
}
