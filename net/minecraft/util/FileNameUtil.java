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

public class FileNameUtil {
    private static final Pattern FILE_NAME_WITH_COUNT = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final int field_33384 = 255;
    private static final Pattern RESERVED_WINDOWS_NAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

    public static String getNextUniqueName(Path path, String name, String extension) throws IOException {
        for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
            name = name.replace(c, '_');
        }
        if (RESERVED_WINDOWS_NAMES.matcher(name = name.replaceAll("[./\"]", "_")).matches()) {
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
                if (string.length() > j) {
                    string = string.substring(0, j);
                }
                string = string + string2;
            }
            string = string + extension;
            Path path2 = path.resolve(string);
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

    public static boolean isNormal(Path path) {
        Path path2 = path.normalize();
        return path2.equals(path);
    }

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

    public static String getPosixFullPath(String path) {
        return FilenameUtils.getFullPath(path).replace(File.separator, "/");
    }

    public static String normalizeToPosix(String path) {
        return FilenameUtils.normalize(path).replace(File.separator, "/");
    }
}

