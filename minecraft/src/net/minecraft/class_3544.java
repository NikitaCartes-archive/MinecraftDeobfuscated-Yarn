package net.minecraft;

import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

public class class_3544 {
	private static final Pattern field_15771 = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	@Environment(EnvType.CLIENT)
	public static String method_15439(int i) {
		int j = i / 20;
		int k = j / 60;
		j %= 60;
		return j < 10 ? k + ":0" + j : k + ":" + j;
	}

	@Environment(EnvType.CLIENT)
	public static String method_15440(String string) {
		return field_15771.matcher(string).replaceAll("");
	}

	public static boolean method_15438(@Nullable String string) {
		return StringUtils.isEmpty(string);
	}
}
