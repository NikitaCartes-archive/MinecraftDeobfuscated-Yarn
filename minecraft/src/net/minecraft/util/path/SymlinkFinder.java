package net.minecraft.util.path;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SymlinkFinder {
	private final PathMatcher matcher;

	public SymlinkFinder(PathMatcher matcher) {
		this.matcher = matcher;
	}

	public void validate(Path path, List<SymlinkEntry> results) throws IOException {
		Path path2 = Files.readSymbolicLink(path);
		if (!this.matcher.matches(path2)) {
			results.add(new SymlinkEntry(path, path2));
		}
	}

	public List<SymlinkEntry> validate(Path path) throws IOException {
		List<SymlinkEntry> list = new ArrayList();
		this.validate(path, list);
		return list;
	}

	public List<SymlinkEntry> collect(Path path, boolean resolveSymlink) throws IOException {
		List<SymlinkEntry> list = new ArrayList();

		BasicFileAttributes basicFileAttributes;
		try {
			basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
		} catch (NoSuchFileException var6) {
			return list;
		}

		if (basicFileAttributes.isRegularFile()) {
			throw new IOException("Path " + path + " is not a directory");
		} else {
			if (basicFileAttributes.isSymbolicLink()) {
				if (!resolveSymlink) {
					this.validate(path, list);
					return list;
				}

				path = Files.readSymbolicLink(path);
			}

			this.validateRecursively(path, list);
			return list;
		}
	}

	public void validateRecursively(Path path, List<SymlinkEntry> results) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			private void validate(Path path, BasicFileAttributes attributes) throws IOException {
				if (attributes.isSymbolicLink()) {
					SymlinkFinder.this.validate(path, results);
				}
			}

			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
				this.validate(path, basicFileAttributes);
				return super.preVisitDirectory(path, basicFileAttributes);
			}

			public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
				this.validate(path, basicFileAttributes);
				return super.visitFile(path, basicFileAttributes);
			}
		});
	}
}
