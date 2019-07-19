package net.minecraft.util;

import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

public class ChatUtil {
	private static final Pattern PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	@Environment(EnvType.CLIENT)
	public static String ticksToString(int i) {
		int j = i / 20;
		int k = j / 60;
		j %= 60;
		return j < 10 ? k + ":0" + j : k + ":" + j;
	}

	@Environment(EnvType.CLIENT)
	public static String stripTextFormat(String text) {
		return PATTERN.matcher(text).replaceAll("");
	}

	public static boolean isEmpty(@Nullable String string) {
		return StringUtils.isEmpty(string);
	}
}
