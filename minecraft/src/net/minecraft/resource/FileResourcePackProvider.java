package net.minecraft.resource;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_5352;

public class FileResourcePackProvider implements ResourcePackProvider {
	private static final FileFilter POSSIBLE_PACK = file -> {
		boolean bl = file.isFile() && file.getName().endsWith(".zip");
		boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
		return bl || bl2;
	};
	private final File packsFolder;
	private final class_5352 field_25345;

	public FileResourcePackProvider(File packsFolder, class_5352 arg) {
		this.packsFolder = packsFolder;
		this.field_25345 = arg;
	}

	@Override
	public <T extends ResourcePackProfile> void register(Consumer<T> consumer, ResourcePackProfile.class_5351<T> factory) {
		if (!this.packsFolder.isDirectory()) {
			this.packsFolder.mkdirs();
		}

		File[] files = this.packsFolder.listFiles(POSSIBLE_PACK);
		if (files != null) {
			for (File file : files) {
				String string = "file/" + file.getName();
				T resourcePackProfile = ResourcePackProfile.of(
					string, false, this.createResourcePack(file), factory, ResourcePackProfile.InsertionPosition.TOP, this.field_25345
				);
				if (resourcePackProfile != null) {
					consumer.accept(resourcePackProfile);
				}
			}
		}
	}

	private Supplier<ResourcePack> createResourcePack(File file) {
		return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
	}
}
