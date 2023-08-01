package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resource.fs.ResourceFileSystem;
import net.minecraft.text.Text;
import net.minecraft.util.PathUtil;
import net.minecraft.util.path.SymlinkEntry;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.util.path.SymlinkValidationException;
import org.slf4j.Logger;

public class FileResourcePackProvider implements ResourcePackProvider {
	static final Logger LOGGER = LogUtils.getLogger();
	private final Path packsDir;
	private final ResourceType type;
	private final ResourcePackSource source;
	private final SymlinkFinder symlinkFinder;

	public FileResourcePackProvider(Path packsDir, ResourceType type, ResourcePackSource source, SymlinkFinder symlinkFinder) {
		this.packsDir = packsDir;
		this.type = type;
		this.source = source;
		this.symlinkFinder = symlinkFinder;
	}

	private static String getFileName(Path path) {
		return path.getFileName().toString();
	}

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder) {
		try {
			PathUtil.createDirectories(this.packsDir);
			forEachProfile(
				this.packsDir,
				this.symlinkFinder,
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

	public static void forEachProfile(Path path, SymlinkFinder symlinkFinder, boolean alwaysStable, BiConsumer<Path, ResourcePackProfile.PackFactory> consumer) throws IOException {
		FileResourcePackProvider.PackOpenerImpl packOpenerImpl = new FileResourcePackProvider.PackOpenerImpl(symlinkFinder, alwaysStable);
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);

		try {
			for (Path path2 : directoryStream) {
				try {
					List<SymlinkEntry> list = new ArrayList();
					ResourcePackProfile.PackFactory packFactory = packOpenerImpl.open(path2, list);
					if (!list.isEmpty()) {
						LOGGER.warn("Ignoring potential pack entry: {}", SymlinkValidationException.getMessage(path2, list));
					} else if (packFactory != null) {
						consumer.accept(path2, packFactory);
					} else {
						LOGGER.info("Found non-pack entry '{}', ignoring", path2);
					}
				} catch (IOException var11) {
					LOGGER.warn("Failed to read properties of '{}', ignoring", path2, var11);
				}
			}
		} catch (Throwable var12) {
			if (directoryStream != null) {
				try {
					directoryStream.close();
				} catch (Throwable var10) {
					var12.addSuppressed(var10);
				}
			}

			throw var12;
		}

		if (directoryStream != null) {
			directoryStream.close();
		}
	}

	static class PackOpenerImpl extends ResourcePackOpener<ResourcePackProfile.PackFactory> {
		private final boolean alwaysStable;

		protected PackOpenerImpl(SymlinkFinder symlinkFinder, boolean alwaysStable) {
			super(symlinkFinder);
			this.alwaysStable = alwaysStable;
		}

		@Nullable
		protected ResourcePackProfile.PackFactory openZip(Path path) {
			FileSystem fileSystem = path.getFileSystem();
			if (fileSystem != FileSystems.getDefault() && !(fileSystem instanceof ResourceFileSystem)) {
				FileResourcePackProvider.LOGGER.info("Can't open pack archive at {}", path);
				return null;
			} else {
				return new ZipResourcePack.ZipBackedFactory(path, this.alwaysStable);
			}
		}

		protected ResourcePackProfile.PackFactory openDirectory(Path path) {
			return new DirectoryResourcePack.DirectoryBackedFactory(path, this.alwaysStable);
		}
	}
}
