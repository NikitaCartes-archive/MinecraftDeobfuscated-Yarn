/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import org.apache.commons.io.FilenameUtils;

/**
 * A class holding file name-related utility methods.
 */
public class FileNameUtil {
    private static final Pattern FILE_NAME_WITH_COUNT = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final int MAX_NAME_LENGTH = 255;
    private static final Pattern RESERVED_WINDOWS_NAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

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
        for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
            name = ((String)name).replace(c, '_');
        }
        if (RESERVED_WINDOWS_NAMES.matcher((CharSequence)(name = ((String)name).replaceAll("[./\"]", "_"))).matches()) {
            name = "_" + (String)name + "_";
        }
        Matcher matcher = FILE_NAME_WITH_COUNT.matcher((CharSequence)name);
        int i = 0;
        if (matcher.matches()) {
            name = matcher.group("name");
            i = Integer.parseInt(matcher.group("count"));
        }
        if (((String)name).length() > 255 - extension.length()) {
            name = ((String)name).substring(0, 255 - extension.length());
        }
        while (true) {
            Object string = name;
            if (i != 0) {
                String string2 = " (" + i + ")";
                int j = 255 - string2.length();
                if (((String)string).length() > j) {
                    string = ((String)string).substring(0, j);
                }
                string = (String)string + string2;
            }
            string = (String)string + extension;
            Path path2 = path.resolve((String)string);
            try {
                Path path3 = Files.createDirectory(path2, new FileAttribute[0]);
                Files.deleteIfExists(path3);
                return path.relativize(path3).toString();
            } catch (FileAlreadyExistsException fileAlreadyExistsException) {
                ++i;
                continue;
            }
            break;
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
            if (!RESERVED_WINDOWS_NAMES.matcher(path2.toString()).matches()) continue;
            return false;
        }
        return true;
    }

    public static Path getResourcePath(Path path, String resourceName, String extension) {
        String string = resourceName + extension;
        Path path2 = Paths.get(string, new String[0]);
        if (path2.endsWith(extension)) {
            throw new InvalidPathException(string, "empty resource name");
        }
        return path.resolve(path2);
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
}

