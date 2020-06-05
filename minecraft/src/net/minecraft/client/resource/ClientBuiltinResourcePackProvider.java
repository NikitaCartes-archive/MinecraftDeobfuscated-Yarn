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
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
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
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern ALPHANUMERAL = Pattern.compile("^[a-fA-F0-9]{40}$");
	private final DefaultResourcePack pack;
	private final File serverPacksRoot;
	private final ReentrantLock lock = new ReentrantLock();
	private final ResourceIndex index;
	@Nullable
	private CompletableFuture<?> downloadTask;
	@Nullable
	private ClientResourcePackProfile serverContainer;

	public ClientBuiltinResourcePackProvider(File serverPacksRoot, ResourceIndex index) {
		this.serverPacksRoot = serverPacksRoot;
		this.index = index;
		this.pack = new DefaultClientResourcePack(index);
	}

	@Override
	public <T extends ResourcePackProfile> void register(Consumer<T> consumer, ResourcePackProfile.Factory<T> factory) {
		T resourcePackProfile = ResourcePackProfile.of(
			"vanilla", true, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN
		);
		if (resourcePackProfile != null) {
			consumer.accept(resourcePackProfile);
		}

		if (this.serverContainer != null) {
			consumer.accept(this.serverContainer);
		}

		T resourcePackProfile2 = this.method_25454(factory);
		if (resourcePackProfile2 != null) {
			consumer.accept(resourcePackProfile2);
		}
	}

	public DefaultResourcePack getPack() {
		return this.pack;
	}

	public static Map<String, String> getDownloadHeaders() {
		Map<String, String> map = Maps.<String, String>newHashMap();
		map.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
		map.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
		map.put("X-Minecraft-Version", SharedConstants.getGameVersion().getName());
		map.put("X-Minecraft-Version-ID", SharedConstants.getGameVersion().getId());
		map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getGameVersion().getPackVersion()));
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
				completableFuture = NetworkUtils.download(file, string, map, 104857600, progressScreen, minecraftClient.getNetworkProxy());
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

	private boolean verifyFile(String expectedSha1, File rfile) {
		try {
			FileInputStream fileInputStream = new FileInputStream(rfile);
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
				LOGGER.info("Found file {} without verification hash", rfile);
				return true;
			}

			if (string.toLowerCase(Locale.ROOT).equals(expectedSha1.toLowerCase(Locale.ROOT))) {
				LOGGER.info("Found file {} matching requested hash {}", rfile, expectedSha1);
				return true;
			}

			LOGGER.warn("File {} had wrong hash (expected {}, found {}).", rfile, expectedSha1, string);
		} catch (IOException var17) {
			LOGGER.warn("File {} couldn't be hashed.", rfile, var17);
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

	public CompletableFuture<Void> loadServerPack(File packZip, ResourcePackSource resourcePackSource) {
		PackResourceMetadata packResourceMetadata;
		NativeImage nativeImage;
		try (ZipResourcePack zipResourcePack = new ZipResourcePack(packZip)) {
			packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);
			nativeImage = ClientResourcePackProfile.method_29713(zipResourcePack);
		} catch (IOException var18) {
			return Util.completeExceptionally(new IOException(String.format("Invalid resourcepack at %s", packZip), var18));
		}

		LOGGER.info("Applying server pack {}", packZip);
		this.serverContainer = new ClientResourcePackProfile(
			"server",
			true,
			() -> new ZipResourcePack(packZip),
			new TranslatableText("resourcePack.server.name"),
			packResourceMetadata.getDescription(),
			ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()),
			ResourcePackProfile.InsertionPosition.TOP,
			true,
			resourcePackSource,
			nativeImage
		);
		return MinecraftClient.getInstance().reloadResourcesConcurrently();
	}

	@Nullable
	private <T extends ResourcePackProfile> T method_25454(ResourcePackProfile.Factory<T> factory) {
		T resourcePackProfile = null;
		File file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"));
		if (file != null && file.isFile()) {
			resourcePackProfile = method_25453(factory, () -> method_16048(file));
		}

		if (resourcePackProfile == null && SharedConstants.isDevelopment) {
			File file2 = this.index.findFile("../resourcepacks/programmer_art");
			if (file2 != null && file2.isDirectory()) {
				resourcePackProfile = method_25453(factory, () -> method_25455(file2));
			}
		}

		return resourcePackProfile;
	}

	@Nullable
	private static <T extends ResourcePackProfile> T method_25453(ResourcePackProfile.Factory<T> factory, Supplier<ResourcePack> supplier) {
		return ResourcePackProfile.of("programer_art", false, supplier, factory, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN);
	}

	private static DirectoryResourcePack method_25455(File file) {
		return new DirectoryResourcePack(file) {
			@Override
			public String getName() {
				return "Programmer Art";
			}
		};
	}

	private static ResourcePack method_16048(File file) {
		return new ZipResourcePack(file) {
			@Override
			public String getName() {
				return "Programmer Art";
			}
		};
	}
}
