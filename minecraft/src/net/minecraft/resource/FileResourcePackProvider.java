package net.minecraft.resource;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;

public class FileResourcePackProvider implements ResourcePackProvider {
	private static final FileFilter POSSIBLE_PACK = file -> {
		boolean bl = file.isFile() && file.getName().endsWith(".zip");
		boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
		return bl || bl2;
	};
	private final File packsFolder;

	public FileResourcePackProvider(File file) {
		this.packsFolder = file;
	}

	@Override
	public <T extends ResourcePackProfile> void register(Map<String, T> map, ResourcePackProfile.Factory<T> factory) {
		if (!this.packsFolder.isDirectory()) {
			this.packsFolder.mkdirs();
		}

		File[] files = this.packsFolder.listFiles(POSSIBLE_PACK);
		if (files != null) {
			for (File file : files) {
				String string = "file/" + file.getName();
				T resourcePackProfile = ResourcePackProfile.of(string, false, this.createResourcePack(file), factory, ResourcePackProfile.InsertionPosition.TOP);
				if (resourcePackProfile != null) {
					map.put(string, resourcePackProfile);
				}
			}
		}
	}

	private Supplier<ResourcePack> createResourcePack(File file) {
		return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
	}
}
