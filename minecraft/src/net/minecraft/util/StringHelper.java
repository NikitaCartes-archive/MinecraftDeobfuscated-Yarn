package net.minecraft.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains string-related helper methods.
 */
public class StringHelper {
	private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
	private static final Pattern LINE_BREAK = Pattern.compile("\\r\\n|\\v");
	private static final Pattern ENDS_WITH_LINE_BREAK = Pattern.compile("(?:\\r\\n|\\v)$");

	/**
	 * {@return the length of the {@code tick} in the MM:SS format, where
	 * the MM is the minutes and SS is the seconds (optionally zero-padded)}
	 */
	public static String formatTicks(int ticks, float tickRate) {
		int i = MathHelper.floor((float)ticks / tickRate);
		int j = i / 60;
		i %= 60;
		int k = j / 60;
		j %= 60;
		return k > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", k, j, i) : String.format(Locale.ROOT, "%02d:%02d", j, i);
	}

	/**
	 * {@return the {@code text} with all formatting codes removed}
	 * 
	 * <p>A formatting code is the character {@code §} followed by
	 * a numeric character or a letter A to F, K to O, or R.
	 * 
	 * @see Formatting#strip
	 */
	public static String stripTextFormat(String text) {
		return FORMATTING_CODE.matcher(text).replaceAll("");
	}

	/**
	 * {@return true if {@code text} is {@code null} or empty, false otherwise}
	 */
	public static boolean isEmpty(@Nullable String text) {
		return StringUtils.isEmpty(text);
	}

	/**
	 * {@return {@code text} truncated to at most {@code maxLength} characters,
	 * optionally with ellipsis}
	 */
	public static String truncate(String text, int maxLength, boolean addEllipsis) {
		if (text.length() <= maxLength) {
			return text;
		} else {
			return addEllipsis && maxLength > 3 ? text.substring(0, maxLength - 3) + "..." : text.substring(0, maxLength);
		}
	}

	/**
	 * {@return the number of linebreaks in {@code text}}
	 * 
	 * <p>A linebreak is either a CRLF sequence or a vertical tab (U+000B).
	 */
	public static int countLines(String text) {
		if (text.isEmpty()) {
			return 0;
		} else {
			Matcher matcher = LINE_BREAK.matcher(text);
			int i = 1;

			while (matcher.find()) {
				i++;
			}

			return i;
		}
	}

	/**
	 * {@return true if {@code text} ends with a linebreak, false otherwise}
	 * 
	 * <p>A linebreak is either a CRLF sequence or a vertical tab (U+000B).
	 */
	public static boolean endsWithLineBreak(String text) {
		return ENDS_WITH_LINE_BREAK.matcher(text).find();
	}

	/**
	 * {@return {@code text} truncated to at most 256 characters without ellipsis}
	 * 
	 * @apiNote This is used when sending chat messages.
	 */
	public static String truncateChat(String text) {
		return truncate(text, 256, false);
	}

	/**
	 * {@return whether {@code c} is a valid character}
	 * 
	 * <p>Characters are valid if they are not an ASCII control code or {@code §}.
	 */
	public static boolean isValidChar(char c) {
		return c != 167 && c >= ' ' && c != 127;
	}

	public static boolean isValidPlayerName(String name) {
		return name.length() > 16 ? false : name.chars().filter(c -> c <= 32 || c >= 127).findAny().isEmpty();
	}

	/**
	 * {@return {@code string} with all {@linkplain #isValidChar invalid characters},
	 * including linebreak ({@code \n}), removed}
	 */
	public static String stripInvalidChars(String string) {
		return stripInvalidChars(string, false);
	}

	/**
	 * {@return {@code string} with all {@linkplain #isValidChar invalid characters}
	 * removed}
	 */
	public static String stripInvalidChars(String string, boolean allowLinebreak) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : string.toCharArray()) {
			if (isValidChar(c)) {
				stringBuilder.append(c);
			} else if (allowLinebreak && c == '\n') {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	public static boolean isWhitespace(int c) {
		return Character.isWhitespace(c) || Character.isSpaceChar(c);
	}

	public static boolean isBlank(@Nullable String string) {
		return string != null && !string.isEmpty() ? string.chars().allMatch(StringHelper::isWhitespace) : true;
	}
}
