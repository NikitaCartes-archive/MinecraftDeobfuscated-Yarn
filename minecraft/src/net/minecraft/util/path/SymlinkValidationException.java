package net.minecraft.util.path;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class SymlinkValidationException extends Exception {
	private final Path path;
	private final List<SymlinkEntry> symlinks;

	public SymlinkValidationException(Path path, List<SymlinkEntry> symlinks) {
		this.path = path;
		this.symlinks = symlinks;
	}

	public String getMessage() {
		return getMessage(this.path, this.symlinks);
	}

	public static String getMessage(Path path, List<SymlinkEntry> symlinks) {
		return "Failed to validate '"
			+ path
			+ "'. Found forbidden symlinks: "
			+ (String)symlinks.stream().map(symlink -> symlink.link() + "->" + symlink.target()).collect(Collectors.joining(", "));
	}
}
