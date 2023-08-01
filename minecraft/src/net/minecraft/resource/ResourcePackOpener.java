package net.minecraft.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.path.SymlinkEntry;
import net.minecraft.util.path.SymlinkFinder;

public abstract class ResourcePackOpener<T> {
	private final SymlinkFinder symlinkFinder;

	protected ResourcePackOpener(SymlinkFinder symlinkFinder) {
		this.symlinkFinder = symlinkFinder;
	}

	@Nullable
	public T open(Path path, List<SymlinkEntry> foundSymlinks) throws IOException {
		Path path2 = path;

		BasicFileAttributes basicFileAttributes;
		try {
			basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
		} catch (NoSuchFileException var6) {
			return null;
		}

		if (basicFileAttributes.isSymbolicLink()) {
			this.symlinkFinder.validate(path, foundSymlinks);
			if (!foundSymlinks.isEmpty()) {
				return null;
			}

			path2 = Files.readSymbolicLink(path);
			basicFileAttributes = Files.readAttributes(path2, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
		}

		if (basicFileAttributes.isDirectory()) {
			this.symlinkFinder.validateRecursively(path2, foundSymlinks);
			if (!foundSymlinks.isEmpty()) {
				return null;
			} else {
				return !Files.isRegularFile(path2.resolve("pack.mcmeta"), new LinkOption[0]) ? null : this.openDirectory(path2);
			}
		} else {
			return basicFileAttributes.isRegularFile() && path2.getFileName().toString().endsWith(".zip") ? this.openZip(path2) : null;
		}
	}

	@Nullable
	protected abstract T openZip(Path path) throws IOException;

	@Nullable
	protected abstract T openDirectory(Path path) throws IOException;
}
