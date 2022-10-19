package net.minecraft.resource.fs;

import java.nio.file.Path;
import java.util.Map;

interface ResourceFile {
	ResourceFile EMPTY = new ResourceFile() {
		public String toString() {
			return "empty";
		}
	};
	ResourceFile RELATIVE = new ResourceFile() {
		public String toString() {
			return "relative";
		}
	};

	public static record Directory(Map<String, ResourcePath> children) implements ResourceFile {
	}

	public static record File(Path contents) implements ResourceFile {
	}
}
