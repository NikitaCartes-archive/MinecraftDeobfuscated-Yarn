package net.minecraft.client.font;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

/**
 * A utility class for visiting the characters of strings, handling surrogate
 * code points and formatting codes.
 */
@Environment(EnvType.CLIENT)
public class TextVisitFactory {
	private static final Optional<Object> VISIT_TERMINATED = Optional.of(Unit.INSTANCE);

	private static boolean visitRegularCharacter(Style style, CharacterVisitor visitor, int index, char c) {
		return Character.isSurrogate(c) ? visitor.accept(index, style, 65533) : visitor.accept(index, style, c);
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
	public static boolean visitForwards(String text, Style style, CharacterVisitor visitor) {
		int i = text.length();

		for (int j = 0; j < i; j++) {
			char c = text.charAt(j);
			if (Character.isHighSurrogate(c)) {
				if (j + 1 >= i) {
					if (!visitor.accept(j, style, 65533)) {
						return false;
					}
					break;
				}

				char d = text.charAt(j + 1);
				if (Character.isLowSurrogate(d)) {
					if (!visitor.accept(j, style, Character.toCodePoint(c, d))) {
						return false;
					}

					j++;
				} else if (!visitor.accept(j, style, 65533)) {
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
	public static boolean visitBackwards(String text, Style style, CharacterVisitor visitor) {
		int i = text.length();

		for (int j = i - 1; j >= 0; j--) {
			char c = text.charAt(j);
			if (Character.isLowSurrogate(c)) {
				if (j - 1 < 0) {
					if (!visitor.accept(0, style, 65533)) {
						return false;
					}
					break;
				}

				char d = text.charAt(j - 1);
				if (Character.isHighSurrogate(d)) {
					if (!visitor.accept(--j, style, Character.toCodePoint(d, c))) {
						return false;
					}
				} else if (!visitor.accept(j, style, 65533)) {
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
	public static boolean visitFormatted(String text, Style style, CharacterVisitor visitor) {
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
	public static boolean visitFormatted(String text, int startIndex, Style style, CharacterVisitor visitor) {
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
	public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
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
					if (!visitor.accept(j, style, 65533)) {
						return false;
					}
					break;
				}

				char d = text.charAt(j + 1);
				if (Character.isLowSurrogate(d)) {
					if (!visitor.accept(j, style, Character.toCodePoint(c, d))) {
						return false;
					}

					j++;
				} else if (!visitor.accept(j, style, 65533)) {
					return false;
				}
			} else if (!visitRegularCharacter(style, visitor, j, c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Visits the code points for every literal string and the formatting codes
	 * supplied by the renderable.
	 * 
	 * <p>The visit is in forward direction.</p>
	 * 
	 * @return {@code true} if the full string was visited, or {@code false} indicating
	 * the {@code visitor} terminated half-way
	 * @see StringRenderable#visit(StringRenderable.StyledVisitor, Style)
	 */
	public static boolean visitFormatted(StringVisitable text, Style style, CharacterVisitor visitor) {
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

	public static String method_31402(StringVisitable stringVisitable) {
		StringBuilder stringBuilder = new StringBuilder();
		visitFormatted(stringVisitable, Style.EMPTY, (i, style, j) -> {
			stringBuilder.appendCodePoint(j);
			return true;
		});
		return stringBuilder.toString();
	}
}
