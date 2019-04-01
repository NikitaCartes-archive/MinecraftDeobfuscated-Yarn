package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_155 {
	public static final Level field_1124 = Level.DISABLED;
	public static boolean field_1125;
	public static final char[] field_1126 = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
	private static GameVersion field_16742;

	public static boolean method_643(char c) {
		return c != 167 && c >= ' ' && c != 127;
	}

	public static String method_644(String string) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : string.toCharArray()) {
			if (method_643(c)) {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	@Environment(EnvType.CLIENT)
	public static String method_16885(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = 0;

		while (i < string.length()) {
			int j = string.codePointAt(i);
			if (!Character.isSupplementaryCodePoint(j)) {
				stringBuilder.appendCodePoint(j);
			} else {
				stringBuilder.append('�');
			}

			i = string.offsetByCodePoints(i, 1);
		}

		return stringBuilder.toString();
	}

	public static GameVersion method_16673() {
		if (field_16742 == null) {
			field_16742 = class_3797.method_16672();
		}

		return field_16742;
	}

	static {
		ResourceLeakDetector.setLevel(field_1124);
		CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
		CommandSyntaxException.BUILT_IN_EXCEPTIONS = new class_2156();
		NamespacedStringType.ENSURE_NAMESPACE = class_1220::method_5193;
	}
}
