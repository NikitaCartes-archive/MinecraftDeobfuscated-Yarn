package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resource.fs.ResourceFileSystem;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class FileResourcePackProvider implements ResourcePackProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Path packsDir;
	private final ResourceType type;
	private final ResourcePackSource source;

	public FileResourcePackProvider(Path packsDir, ResourceType type, ResourcePackSource source) {
		this.packsDir = packsDir;
		this.type = type;
		this.source = source;
	}

	private static String getFileName(Path path) {
		return path.getFileName().toString();
	}

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder) {
		try {
			Files.createDirectories(this.packsDir);
			forEachProfile(
				this.packsDir,
				false,
				(path, packFactory) -> {
					String string = getFileName(path);
					ResourcePackProfile resourcePackProfile = ResourcePackProfile.create(
						"file/" + string, Text.literal(string), false, packFactory, this.type, ResourcePackProfile.InsertionPosition.TOP, this.source
					);
					if (resourcePackProfile != null) {
						profileAdder.accept(resourcePackProfile);
					}
				}
			);
		} catch (IOException var3) {
			LOGGER.warn("Failed to list packs in {}", this.packsDir, var3);
		}
	}

	public static void forEachProfile(Path packsDir, boolean alwaysStable, BiConsumer<Path, ResourcePackProfile.PackFactory> consumer) throws IOException {
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(packsDir);

		try {
			for (Path path : directoryStream) {
				ResourcePackProfile.PackFactory packFactory = getFactory(path, alwaysStable);
				if (packFactory != null) {
					consumer.accept(path, packFactory);
				}
			}
		} catch (Throwable var8) {
			if (directoryStream != null) {
				try {
					directoryStream.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}
			}

			throw var8;
		}

		if (directoryStream != null) {
			directoryStream.close();
		}
	}

	@Nullable
	public static ResourcePackProfile.PackFactory getFactory(Path path, boolean alwaysStable) {
		BasicFileAttributes basicFileAttributes;
		try {
			basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
		} catch (NoSuchFileException var5) {
			return null;
		} catch (IOException var6) {
			LOGGER.warn("Failed to read properties of '{}', ignoring", path, var6);
			return null;
		}

		if (basicFileAttributes.isDirectory() && Files.isRegularFile(path.resolve("pack.mcmeta"), new LinkOption[0])) {
			return name -> new DirectoryResourcePack(name, path, alwaysStable);
		} else {
			if (basicFileAttributes.isRegularFile() && path.getFileName().toString().endsWith(".zip")) {
				FileSystem fileSystem = path.getFileSystem();
				if (fileSystem == FileSystems.getDefault() || fileSystem instanceof ResourceFileSystem) {
					File file = path.toFile();
					return name -> new ZipResourcePack(name, file, alwaysStable);
				}
			}

			LOGGER.info("Found non-pack entry '{}', ignoring", path);
			return null;
		}
	}
}
