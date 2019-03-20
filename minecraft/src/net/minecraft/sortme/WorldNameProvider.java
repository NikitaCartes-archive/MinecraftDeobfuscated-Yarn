package net.minecraft.sortme;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;

@Environment(EnvType.CLIENT)
public class WorldNameProvider {
	private static final Set<String> RESERVED_WINDOWS_NAMES = ImmutableSet.of(
		"CON",
		"COM",
		"PRN",
		"AUX",
		"CLOCK$",
		"NUL",
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
		"COM7",
		"COM8",
		"COM9",
		"LPT1",
		"LPT2",
		"LPT3",
		"LPT4",
		"LPT5",
		"LPT6",
		"LPT7",
		"LPT8",
		"LPT9"
	);
	private static final Pattern field_18956 = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);

	public static String transformWorldName(Path path, String string) throws IOException {
		for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
			string = string.replace(c, '_');
		}

		string = string.replaceAll("[./\"]", "_");
		if (RESERVED_WINDOWS_NAMES.contains(string.toUpperCase())) {
			string = "_" + string + "_";
		}

		Matcher matcher = field_18956.matcher(string);
		int i = 0;
		if (matcher.matches()) {
			string = matcher.group("name");
			i = Integer.parseInt(matcher.group("count"));
		}

		if (string.length() > 255) {
			string = string.substring(0, 255);
		}

		while (true) {
			String string2 = string;
			if (i != 0) {
				String string3 = " (" + i + ")";
				int j = 255 - string3.length();
				if (string.length() > j) {
					string2 = string.substring(0, j);
				}

				string2 = string2 + string3;
			}

			Path path2 = path.resolve(string2);

			try {
				Path path3 = Files.createDirectory(path2);
				Files.deleteIfExists(path3);
				return path.relativize(path3).toString();
			} catch (FileAlreadyExistsException var7) {
				i++;
			}
		}
	}
}
