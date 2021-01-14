package net.minecraft.util;

import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

public class ChatUtil {
	private static final Pattern PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	@Environment(EnvType.CLIENT)
	public static String ticksToString(int ticks) {
		int i = ticks / 20;
		int j = i / 60;
		i %= 60;
		return i < 10 ? j + ":0" + i : j + ":" + i;
	}

	@Environment(EnvType.CLIENT)
	public static String stripTextFormat(String text) {
		return PATTERN.matcher(text).replaceAll("");
	}

	public static boolean isEmpty(@Nullable String text) {
		return StringUtils.isEmpty(text);
	}
}
