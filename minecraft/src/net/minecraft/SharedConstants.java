package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.TextComponentBuiltInExceptionProvider;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class SharedConstants {
	public static final Level RESOURCE_LEAK_DETECTOR_DISABLED = Level.DISABLED;
	public static boolean isDevelopment;
	public static final char[] INVALID_CHARS_LEVEL_NAME = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
	private static GameVersion version;

	public static boolean isValidChar(char c) {
		return c != 167 && c >= ' ' && c != 127;
	}

	public static String stripInvalidChars(String string) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : string.toCharArray()) {
			if (isValidChar(c)) {
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

	public static GameVersion getGameVersion() {
		if (version == null) {
			version = MinecraftVersion.create();
		}

		return version;
	}

	static {
		ResourceLeakDetector.setLevel(RESOURCE_LEAK_DETECTOR_DISABLED);
		CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
		CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TextComponentBuiltInExceptionProvider();
		NamespacedStringType.ENSURE_NAMESPACE = SchemaIdentifierNormalize::normalize;
	}
}
