package net.minecraft.resource;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;

public class FileResourcePackCreator implements ResourcePackCreator {
	private static final FileFilter POSSIBLE_PACK = file -> {
		boolean bl = file.isFile() && file.getName().endsWith(".zip");
		boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
		return bl || bl2;
	};
	private final File packsFolder;

	public FileResourcePackCreator(File file) {
		this.packsFolder = file;
	}

	@Override
	public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
		if (!this.packsFolder.isDirectory()) {
			this.packsFolder.mkdirs();
		}

		File[] files = this.packsFolder.listFiles(POSSIBLE_PACK);
		if (files != null) {
			for (File file : files) {
				String string = "file/" + file.getName();
				T resourcePackContainer = ResourcePackContainer.of(
					string, false, this.createResourcePack(file), factory, ResourcePackContainer.InsertionPosition.field_14280
				);
				if (resourcePackContainer != null) {
					map.put(string, resourcePackContainer);
				}
			}
		}
	}

	private Supplier<ResourcePack> createResourcePack(File file) {
		return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
	}
}
