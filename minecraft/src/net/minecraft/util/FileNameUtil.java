package net.minecraft.util;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;

public class FileNameUtil {
	private static final Pattern field_18956 = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
	private static final Pattern RESERVED_WINDOWS_NAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

	@Environment(EnvType.CLIENT)
	public static String getNextUniqueName(Path path, String name, String extension) throws IOException {
		for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
			name = name.replace(c, '_');
		}

		name = name.replaceAll("[./\"]", "_");
		if (RESERVED_WINDOWS_NAMES.matcher(name).matches()) {
			name = "_" + name + "_";
		}

		Matcher matcher = field_18956.matcher(name);
		int i = 0;
		if (matcher.matches()) {
			name = matcher.group("name");
			i = Integer.parseInt(matcher.group("count"));
		}

		if (name.length() > 255 - extension.length()) {
			name = name.substring(0, 255 - extension.length());
		}

		while (true) {
			String string = name;
			if (i != 0) {
				String string2 = " (" + i + ")";
				int j = 255 - string2.length();
				if (name.length() > j) {
					string = name.substring(0, j);
				}

				string = string + string2;
			}

			string = string + extension;
			Path path2 = path.resolve(string);

			try {
				Path path3 = Files.createDirectory(path2);
				Files.deleteIfExists(path3);
				return path.relativize(path3).toString();
			} catch (FileAlreadyExistsException var8) {
				i++;
			}
		}
	}

	public static boolean isNormal(Path path) {
		Path path2 = path.normalize();
		return path2.equals(path);
	}

	public static boolean isAllowedName(Path path) {
		for (Path path2 : path) {
			if (RESERVED_WINDOWS_NAMES.matcher(path2.toString()).matches()) {
				return false;
			}
		}

		return true;
	}

	public static Path method_20202(Path path, String string, String string2) {
		String string3 = string + string2;
		Path path2 = Paths.get(string3);
		if (path2.endsWith(string2)) {
			throw new InvalidPathException(string3, "empty resource name");
		} else {
			return path.resolve(path2);
		}
	}
}
