package net.minecraft.util;

import com.mojang.serialization.DataResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import org.apache.commons.io.FilenameUtils;

/**
 * A class holding path-related utility methods.
 */
public class PathUtil {
	private static final Pattern FILE_NAME_WITH_COUNT = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
	private static final int MAX_NAME_LENGTH = 255;
	private static final Pattern RESERVED_WINDOWS_NAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);
	private static final Pattern VALID_FILE_NAME = Pattern.compile("[-._a-z0-9]+");

	public static String replaceInvalidChars(String fileName) {
		for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
			fileName = fileName.replace(c, '_');
		}

		return fileName.replaceAll("[./\"]", "_");
	}

	/**
	 * {@return a filename, prefixed with {@code name}, that does not currently
	 * exist inside {@code path}}
	 * 
	 * @implNote This strips any illegal characters from {@code name}, then
	 * attempts to make a directory with the name and the extension. If this succeeds,
	 * the directory is deleted and the name with the extension is returned. If not, it
	 * appends {@code (1)} to the name and tries again until it succeeds.
	 * 
	 * @throws IOException if creating the temporary directory fails, e.g. due to {@code path}
	 * not being a directory
	 */
	public static String getNextUniqueName(Path path, String name, String extension) throws IOException {
		name = replaceInvalidChars(name);
		if (RESERVED_WINDOWS_NAMES.matcher(name).matches()) {
			name = "_" + name + "_";
		}

		Matcher matcher = FILE_NAME_WITH_COUNT.matcher(name);
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

	/**
	 * {@return whether {@code path} is already normalized}
	 */
	public static boolean isNormal(Path path) {
		Path path2 = path.normalize();
		return path2.equals(path);
	}

	/**
	 * {@return whether {@code path} does not contain reserved Windows file names}
	 * 
	 * @apiNote This returns {@code false} for reserved names regardless of whether the platform
	 * the game is running is actually Windows. Note that this does not check for
	 * illegal characters or file permissions.
	 */
	public static boolean isAllowedName(Path path) {
		for (Path path2 : path) {
			if (RESERVED_WINDOWS_NAMES.matcher(path2.toString()).matches()) {
				return false;
			}
		}

		return true;
	}

	public static Path getResourcePath(Path path, String resourceName, String extension) {
		String string = resourceName + extension;
		Path path2 = Paths.get(string);
		if (path2.endsWith(extension)) {
			throw new InvalidPathException(string, "empty resource name");
		} else {
			return path.resolve(path2);
		}
	}

	/**
	 * {@return the full path of {@code path} with directory separator normalized
	 * to {@code /}}
	 */
	public static String getPosixFullPath(String path) {
		return FilenameUtils.getFullPath(path).replace(File.separator, "/");
	}

	/**
	 * {@return the normalized path of {@code path} with directory separator normalized
	 * to {@code /}}
	 */
	public static String normalizeToPosix(String path) {
		return FilenameUtils.normalize(path).replace(File.separator, "/");
	}

	/**
	 * {@return {@code path} split by {@code /}, or an error result if the path is invalid}
	 * 
	 * <p>All path segments must be a {@linkplain #isFileNameValid valid file name}. Additionally,
	 * {@code .} and {@code ..} are forbidden.
	 */
	public static DataResult<List<String>> split(String path) {
		int i = path.indexOf(47);
		if (i == -1) {
			return switch (path) {
				case "", ".", ".." -> DataResult.error(() -> "Invalid path '" + path + "'");
				default -> !isFileNameValid(path) ? DataResult.error(() -> "Invalid path '" + path + "'") : DataResult.success(List.of(path));
			};
		} else {
			List<String> list = new ArrayList();
			int j = 0;
			boolean bl = false;

			while (true) {
				String string = path.substring(j, i);
				switch (string) {
					case "":
					case ".":
					case "..":
						return DataResult.error(() -> "Invalid segment '" + string + "' in path '" + path + "'");
				}

				if (!isFileNameValid(string)) {
					return DataResult.error(() -> "Invalid segment '" + string + "' in path '" + path + "'");
				}

				list.add(string);
				if (bl) {
					return DataResult.success(list);
				}

				j = i + 1;
				i = path.indexOf(47, j);
				if (i == -1) {
					i = path.length();
					bl = true;
				}
			}
		}
	}

	/**
	 * {@return {@code paths} resolved as a path from {@code root}}
	 * 
	 * <p>If {@code paths} is empty, this returns {@code root}.
	 */
	public static Path getPath(Path root, List<String> paths) {
		int i = paths.size();

		return switch (i) {
			case 0 -> root;
			case 1 -> root.resolve((String)paths.get(0));
			default -> {
				String[] strings = new String[i - 1];

				for (int j = 1; j < i; j++) {
					strings[j - 1] = (String)paths.get(j);
				}

				yield root.resolve(root.getFileSystem().getPath((String)paths.get(0), strings));
			}
		};
	}

	/**
	 * {@return whether {@code name} is a valid file name}
	 * 
	 * @apiNote A valid file name contains only ASCII lowercase alphabets, ASCII digits,
	 * a dot, or an underscore. Unlike {@link Identifier} paths, hyphens are not allowed.
	 */
	public static boolean isFileNameValid(String name) {
		return VALID_FILE_NAME.matcher(name).matches();
	}

	/**
	 * Validates that {@code paths} is not empty and does not contain invalid segments
	 * (such as {@code .}, {@code ..}, or otherwise {@linkplain #isFileNameValid invalid names}).
	 * 
	 * @throws IllegalArgumentException when the {@code paths} are invalid
	 */
	public static void validatePath(String... paths) {
		if (paths.length == 0) {
			throw new IllegalArgumentException("Path must have at least one element");
		} else {
			for (String string : paths) {
				if (string.equals("..") || string.equals(".") || !isFileNameValid(string)) {
					throw new IllegalArgumentException("Illegal segment " + string + " in path " + Arrays.toString(paths));
				}
			}
		}
	}

	/**
	 * A symbolic-link safe version of {@link java.nio.file.Files#createDirectories}.
	 */
	public static void createDirectories(Path path) throws IOException {
		Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
	}
}
