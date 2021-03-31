package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientBuiltinResourcePackProvider implements ResourcePackProvider {
	private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(
		new TranslatableText("resourcePack.vanilla.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion())
	);
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern ALPHANUMERAL = Pattern.compile("^[a-fA-F0-9]{40}$");
	private static final int field_32958 = 104857600;
	private static final int field_32959 = 10;
	private static final String field_32960 = "vanilla";
	private static final String field_32961 = "server";
	private static final String field_32962 = "programer_art";
	private static final String field_32963 = "Programmer Art";
	private final DefaultResourcePack pack;
	private final File serverPacksRoot;
	private final ReentrantLock lock = new ReentrantLock();
	private final ResourceIndex index;
	@Nullable
	private CompletableFuture<?> downloadTask;
	@Nullable
	private ResourcePackProfile serverContainer;

	public ClientBuiltinResourcePackProvider(File serverPacksRoot, ResourceIndex index) {
		this.serverPacksRoot = serverPacksRoot;
		this.index = index;
		this.pack = new DefaultClientResourcePack(DEFAULT_PACK_METADATA, index);
	}

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
		ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(
			"vanilla", true, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN
		);
		if (resourcePackProfile != null) {
			profileAdder.accept(resourcePackProfile);
		}

		if (this.serverContainer != null) {
			profileAdder.accept(this.serverContainer);
		}

		ResourcePackProfile resourcePackProfile2 = this.getProgrammerArtResourcePackProfile(factory);
		if (resourcePackProfile2 != null) {
			profileAdder.accept(resourcePackProfile2);
		}
	}

	public DefaultResourcePack getPack() {
		return this.pack;
	}

	private static Map<String, String> getDownloadHeaders() {
		Map<String, String> map = Maps.<String, String>newHashMap();
		map.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
		map.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
		map.put("X-Minecraft-Version", SharedConstants.getGameVersion().getName());
		map.put("X-Minecraft-Version-ID", SharedConstants.getGameVersion().getId());
		map.put("X-Minecraft-Pack-Format", String.valueOf(ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion())));
		map.put("User-Agent", "Minecraft Java/" + SharedConstants.getGameVersion().getName());
		return map;
	}

	public CompletableFuture<?> download(String string, String string2) {
		String string3 = DigestUtils.sha1Hex(string);
		String string4 = ALPHANUMERAL.matcher(string2).matches() ? string2 : "";
		this.lock.lock();

		CompletableFuture var13;
		try {
			this.clear();
			this.deleteOldServerPack();
			File file = new File(this.serverPacksRoot, string3);
			CompletableFuture<?> completableFuture;
			if (file.exists()) {
				completableFuture = CompletableFuture.completedFuture("");
			} else {
				ProgressScreen progressScreen = new ProgressScreen();
				Map<String, String> map = getDownloadHeaders();
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				minecraftClient.submitAndJoin(() -> minecraftClient.openScreen(progressScreen));
				completableFuture = NetworkUtils.downloadResourcePack(file, string, map, 104857600, progressScreen, minecraftClient.getNetworkProxy());
			}

			this.downloadTask = completableFuture.thenCompose(
					object -> !this.verifyFile(string4, file)
							? Util.completeExceptionally(new RuntimeException("Hash check failure for file " + file + ", see log"))
							: this.loadServerPack(file, ResourcePackSource.PACK_SOURCE_SERVER)
				)
				.whenComplete((void_, throwable) -> {
					if (throwable != null) {
						LOGGER.warn("Pack application failed: {}, deleting file {}", throwable.getMessage(), file);
						delete(file);
					}
				});
			var13 = this.downloadTask;
		} finally {
			this.lock.unlock();
		}

		return var13;
	}

	private static void delete(File file) {
		try {
			Files.delete(file.toPath());
		} catch (IOException var2) {
			LOGGER.warn("Failed to delete file {}: {}", file, var2.getMessage());
		}
	}

	public void clear() {
		this.lock.lock();

		try {
			if (this.downloadTask != null) {
				this.downloadTask.cancel(true);
			}

			this.downloadTask = null;
			if (this.serverContainer != null) {
				this.serverContainer = null;
				MinecraftClient.getInstance().reloadResourcesConcurrently();
			}
		} finally {
			this.lock.unlock();
		}
	}

	private boolean verifyFile(String expectedSha1, File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			Throwable var5 = null;

			String string;
			try {
				string = DigestUtils.sha1Hex(fileInputStream);
			} catch (Throwable var15) {
				var5 = var15;
				throw var15;
			} finally {
				if (fileInputStream != null) {
					if (var5 != null) {
						try {
							fileInputStream.close();
						} catch (Throwable var14) {
							var5.addSuppressed(var14);
						}
					} else {
						fileInputStream.close();
					}
				}
			}

			if (expectedSha1.isEmpty()) {
				LOGGER.info("Found file {} without verification hash", file);
				return true;
			}

			if (string.toLowerCase(Locale.ROOT).equals(expectedSha1.toLowerCase(Locale.ROOT))) {
				LOGGER.info("Found file {} matching requested hash {}", file, expectedSha1);
				return true;
			}

			LOGGER.warn("File {} had wrong hash (expected {}, found {}).", file, expectedSha1, string);
		} catch (IOException var17) {
			LOGGER.warn("File {} couldn't be hashed.", file, var17);
		}

		return false;
	}

	private void deleteOldServerPack() {
		try {
			List<File> list = Lists.<File>newArrayList(FileUtils.listFiles(this.serverPacksRoot, TrueFileFilter.TRUE, null));
			list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			int i = 0;

			for (File file : list) {
				if (i++ >= 10) {
					LOGGER.info("Deleting old server resource pack {}", file.getName());
					FileUtils.deleteQuietly(file);
				}
			}
		} catch (IllegalArgumentException var5) {
			LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
		}
	}

	public CompletableFuture<Void> loadServerPack(File packZip, ResourcePackSource packSource) {
		PackResourceMetadata packResourceMetadata;
		try (ZipResourcePack zipResourcePack = new ZipResourcePack(packZip)) {
			packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);
		} catch (IOException var17) {
			return Util.completeExceptionally(new IOException(String.format("Invalid resourcepack at %s", packZip), var17));
		}

		LOGGER.info("Applying server pack {}", packZip);
		this.serverContainer = new ResourcePackProfile(
			"server",
			true,
			() -> new ZipResourcePack(packZip),
			new TranslatableText("resourcePack.server.name"),
			packResourceMetadata.getDescription(),
			ResourcePackCompatibility.from(packResourceMetadata, ResourceType.CLIENT_RESOURCES),
			ResourcePackProfile.InsertionPosition.TOP,
			true,
			packSource
		);
		return MinecraftClient.getInstance().reloadResourcesConcurrently();
	}

	@Nullable
	private ResourcePackProfile getProgrammerArtResourcePackProfile(ResourcePackProfile.Factory factory) {
		ResourcePackProfile resourcePackProfile = null;
		File file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"));
		if (file != null && file.isFile()) {
			resourcePackProfile = getProgrammerArtResourcePackProfile(factory, () -> getProgrammerArtResourcePackFromZipFile(file));
		}

		if (resourcePackProfile == null && SharedConstants.isDevelopment) {
			File file2 = this.index.findFile("../resourcepacks/programmer_art");
			if (file2 != null && file2.isDirectory()) {
				resourcePackProfile = getProgrammerArtResourcePackProfile(factory, () -> getProgrammerArtResourcePackFromDirectory(file2));
			}
		}

		return resourcePackProfile;
	}

	@Nullable
	private static ResourcePackProfile getProgrammerArtResourcePackProfile(ResourcePackProfile.Factory factory, Supplier<ResourcePack> packSupplier) {
		return ResourcePackProfile.of(
			"programer_art", false, packSupplier, factory, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN
		);
	}

	private static DirectoryResourcePack getProgrammerArtResourcePackFromDirectory(File packDirectory) {
		return new DirectoryResourcePack(packDirectory) {
			@Override
			public String getName() {
				return "Programmer Art";
			}
		};
	}

	private static ResourcePack getProgrammerArtResourcePackFromZipFile(File zipFile) {
		return new ZipResourcePack(zipFile) {
			@Override
			public String getName() {
				return "Programmer Art";
			}
		};
	}
}
