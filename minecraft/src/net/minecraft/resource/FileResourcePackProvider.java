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
import java.util.Optional;
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
	private static final ResourcePackPosition POSITION = new ResourcePackPosition(false, ResourcePackProfile.InsertionPosition.TOP, false);
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
			forEachProfile(this.packsDir, this.symlinkFinder, (path, packFactory) -> {
				ResourcePackInfo resourcePackInfo = this.createPackInfo(path);
				ResourcePackProfile resourcePackProfile = ResourcePackProfile.create(resourcePackInfo, packFactory, this.type, POSITION);
				if (resourcePackProfile != null) {
					profileAdder.accept(resourcePackProfile);
				}
			});
		} catch (IOException var3) {
			LOGGER.warn("Failed to list packs in {}", this.packsDir, var3);
		}
	}

	private ResourcePackInfo createPackInfo(Path path) {
		String string = getFileName(path);
		return new ResourcePackInfo("file/" + string, Text.literal(string), this.source, Optional.empty());
	}

	public static void forEachProfile(Path path, SymlinkFinder symlinkFinder, BiConsumer<Path, ResourcePackProfile.PackFactory> callback) throws IOException {
		FileResourcePackProvider.PackOpenerImpl packOpenerImpl = new FileResourcePackProvider.PackOpenerImpl(symlinkFinder);
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);

		try {
			for (Path path2 : directoryStream) {
				try {
					List<SymlinkEntry> list = new ArrayList();
					ResourcePackProfile.PackFactory packFactory = packOpenerImpl.open(path2, list);
					if (!list.isEmpty()) {
						LOGGER.warn("Ignoring potential pack entry: {}", SymlinkValidationException.getMessage(path2, list));
					} else if (packFactory != null) {
						callback.accept(path2, packFactory);
					} else {
						LOGGER.info("Found non-pack entry '{}', ignoring", path2);
					}
				} catch (IOException var10) {
					LOGGER.warn("Failed to read properties of '{}', ignoring", path2, var10);
				}
			}
		} catch (Throwable var11) {
			if (directoryStream != null) {
				try {
					directoryStream.close();
				} catch (Throwable var9) {
					var11.addSuppressed(var9);
				}
			}

			throw var11;
		}

		if (directoryStream != null) {
			directoryStream.close();
		}
	}

	static class PackOpenerImpl extends ResourcePackOpener<ResourcePackProfile.PackFactory> {
		protected PackOpenerImpl(SymlinkFinder symlinkFinder) {
			super(symlinkFinder);
		}

		@Nullable
		protected ResourcePackProfile.PackFactory openZip(Path path) {
			FileSystem fileSystem = path.getFileSystem();
			if (fileSystem != FileSystems.getDefault() && !(fileSystem instanceof ResourceFileSystem)) {
				FileResourcePackProvider.LOGGER.info("Can't open pack archive at {}", path);
				return null;
			} else {
				return new ZipResourcePack.ZipBackedFactory(path);
			}
		}

		protected ResourcePackProfile.PackFactory openDirectory(Path path) {
			return new DirectoryResourcePack.DirectoryBackedFactory(path);
		}
	}
}
