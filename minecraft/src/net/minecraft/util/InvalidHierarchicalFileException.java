package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

/**
 * An exception that tracks the names of the invalid files and the path to
 * the invalid element in a hierarchical tree structure (such as JSON).
 */
public class InvalidHierarchicalFileException extends IOException {
	private final List<InvalidHierarchicalFileException.File> invalidFiles = Lists.<InvalidHierarchicalFileException.File>newArrayList();
	private final String message;

	public InvalidHierarchicalFileException(String message) {
		this.invalidFiles.add(new InvalidHierarchicalFileException.File());
		this.message = message;
	}

	public InvalidHierarchicalFileException(String message, Throwable cause) {
		super(cause);
		this.invalidFiles.add(new InvalidHierarchicalFileException.File());
		this.message = message;
	}

	public void addInvalidKey(String key) {
		((InvalidHierarchicalFileException.File)this.invalidFiles.get(0)).addKey(key);
	}

	public void addInvalidFile(String fileName) {
		((InvalidHierarchicalFileException.File)this.invalidFiles.get(0)).name = fileName;
		this.invalidFiles.add(0, new InvalidHierarchicalFileException.File());
	}

	public String getMessage() {
		return "Invalid " + this.invalidFiles.get(this.invalidFiles.size() - 1) + ": " + this.message;
	}

	public static InvalidHierarchicalFileException wrap(Exception cause) {
		if (cause instanceof InvalidHierarchicalFileException) {
			return (InvalidHierarchicalFileException)cause;
		} else {
			String string = cause.getMessage();
			if (cause instanceof FileNotFoundException) {
				string = "File not found";
			}

			return new InvalidHierarchicalFileException(string, cause);
		}
	}

	public static class File {
		@Nullable
		String name;
		private final List<String> keys = Lists.<String>newArrayList();

		File() {
		}

		void addKey(String key) {
			this.keys.add(0, key);
		}

		@Nullable
		public String getName() {
			return this.name;
		}

		public String joinKeys() {
			return StringUtils.join(this.keys, "->");
		}

		public String toString() {
			if (this.name != null) {
				return this.keys.isEmpty() ? this.name : this.name + " " + this.joinKeys();
			} else {
				return this.keys.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.joinKeys();
			}
		}
	}
}
