package net.minecraft.resource;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FileResourcePackProvider implements ResourcePackProvider {
	private static final FileFilter POSSIBLE_PACK = file -> {
		boolean bl = file.isFile() && file.getName().endsWith(".zip");
		boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
		return bl || bl2;
	};
	private final File packsFolder;
	private final ResourcePackSource field_25345;

	public FileResourcePackProvider(File packsFolder, ResourcePackSource source) {
		this.packsFolder = packsFolder;
		this.field_25345 = source;
	}

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
		if (!this.packsFolder.isDirectory()) {
			this.packsFolder.mkdirs();
		}

		File[] files = this.packsFolder.listFiles(POSSIBLE_PACK);
		if (files != null) {
			for (File file : files) {
				String string = "file/" + file.getName();
				ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(
					string, false, this.createResourcePack(file), factory, ResourcePackProfile.InsertionPosition.TOP, this.field_25345
				);
				if (resourcePackProfile != null) {
					profileAdder.accept(resourcePackProfile);
				}
			}
		}
	}

	private Supplier<ResourcePack> createResourcePack(File file) {
		return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
	}
}
