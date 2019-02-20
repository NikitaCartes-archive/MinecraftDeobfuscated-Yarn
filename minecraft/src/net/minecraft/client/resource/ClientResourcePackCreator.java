package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.WorkingScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientResourcePackCreator implements ResourcePackCreator {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern ALPHANUMERAL = Pattern.compile("^[a-fA-F0-9]{40}$");
	private final DefaultResourcePack pack;
	private final File serverPacksRoot;
	private final ReentrantLock lock = new ReentrantLock();
	private final ResourceIndex index;
	@Nullable
	private CompletableFuture<?> downloadTask;
	@Nullable
	private ClientResourcePackContainer serverContainer;

	public ClientResourcePackCreator(File file, ResourceIndex resourceIndex) {
		this.serverPacksRoot = file;
		this.index = resourceIndex;
		this.pack = new DefaultClientResourcePack(resourceIndex);
	}

	@Override
	public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
		T resourcePackContainer = ResourcePackContainer.of("vanilla", true, () -> this.pack, factory, ResourcePackContainer.SortingDirection.field_14281);
		if (resourcePackContainer != null) {
			map.put("vanilla", resourcePackContainer);
		}

		if (this.serverContainer != null) {
			map.put("server", this.serverContainer);
		}

		File file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"));
		if (file != null && file.isFile()) {
			T resourcePackContainer2 = ResourcePackContainer.of("programer_art", false, () -> new ZipResourcePack(file) {
					@Override
					public String getName() {
						return "Programmer Art";
					}
				}, factory, ResourcePackContainer.SortingDirection.field_14280);
			if (resourcePackContainer2 != null) {
				map.put("programer_art", resourcePackContainer2);
			}
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
		map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getGameVersion().getPackVersion()));
		map.put("User-Agent", "Minecraft Java/" + SharedConstants.getGameVersion().getName());
		return map;
	}

	public CompletableFuture<?> download(String string, String string2) {
		String string3 = DigestUtils.sha1Hex(string);
		String string4 = ALPHANUMERAL.matcher(string2).matches() ? string2 : "";
		File file = new File(this.serverPacksRoot, string3);
		this.lock.lock();

		try {
			this.clear();
			if (file.exists()) {
				if (this.verifyFile(string4, file)) {
					return this.loadServerPack(file);
				}

				LOGGER.warn("Deleting file {}", file);
				FileUtils.deleteQuietly(file);
			}

			this.deleteOldServerPack();
			WorkingScreen workingScreen = new WorkingScreen();
			Map<String, String> map = getDownloadHeaders();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.executeFuture(() -> minecraftClient.openScreen(workingScreen)).join();
			this.downloadTask = NetworkUtils.download(file, string, map, 52428800, workingScreen, minecraftClient.getNetworkProxy())
				.whenComplete((object, throwable) -> {
					if (throwable == null) {
						if (this.verifyFile(string4, file)) {
							this.loadServerPack(file);
						} else {
							LOGGER.warn("Deleting file {}", file);
							FileUtils.deleteQuietly(file);
						}
					} else {
						FileUtils.deleteQuietly(file);
					}
				});
			return this.downloadTask;
		} finally {
			this.lock.unlock();
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

	private boolean verifyFile(String string, File file) {
		try {
			String string2 = DigestUtils.sha1Hex(new FileInputStream(file));
			if (string.isEmpty()) {
				LOGGER.info("Found file {} without verification hash", file);
				return true;
			}

			if (string2.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
				LOGGER.info("Found file {} matching requested hash {}", file, string);
				return true;
			}

			LOGGER.warn("File {} had wrong hash (expected {}, found {}).", file, string, string2);
		} catch (IOException var4) {
			LOGGER.warn("File {} couldn't be hashed.", file, var4);
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

	public CompletableFuture<Void> loadServerPack(File file) {
		PackResourceMetadata packResourceMetadata = null;
		NativeImage nativeImage = null;

		try {
			ZipResourcePack zipResourcePack = new ZipResourcePack(file);
			Throwable var5 = null;

			try {
				packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);

				try {
					InputStream inputStream = zipResourcePack.openRoot("pack.png");
					Throwable var7 = null;

					try {
						nativeImage = NativeImage.fromInputStream(inputStream);
					} catch (Throwable var34) {
						var7 = var34;
						throw var34;
					} finally {
						if (inputStream != null) {
							if (var7 != null) {
								try {
									inputStream.close();
								} catch (Throwable var33) {
									var7.addSuppressed(var33);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (IllegalArgumentException | IOException var36) {
				}
			} catch (Throwable var37) {
				var5 = var37;
				throw var37;
			} finally {
				if (zipResourcePack != null) {
					if (var5 != null) {
						try {
							zipResourcePack.close();
						} catch (Throwable var32) {
							var5.addSuppressed(var32);
						}
					} else {
						zipResourcePack.close();
					}
				}
			}
		} catch (IOException var39) {
		}

		if (packResourceMetadata == null) {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			completableFuture.completeExceptionally(new RuntimeException("Invalid resourcepack"));
			return completableFuture;
		} else {
			this.serverContainer = new ClientResourcePackContainer(
				"server",
				true,
				() -> new ZipResourcePack(file),
				new TranslatableTextComponent("resourcePack.server.name"),
				packResourceMetadata.getDescription(),
				ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()),
				ResourcePackContainer.SortingDirection.field_14280,
				true,
				nativeImage
			);
			return MinecraftClient.getInstance().reloadResourcesConcurrently();
		}
	}
}
