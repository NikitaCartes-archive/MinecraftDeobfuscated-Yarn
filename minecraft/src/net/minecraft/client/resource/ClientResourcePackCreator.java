package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
import net.minecraft.util.SystemUtil;
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
				WorkingScreen workingScreen = new WorkingScreen();
				Map<String, String> map = getDownloadHeaders();
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				minecraftClient.method_19537(() -> minecraftClient.openScreen(workingScreen));
				completableFuture = NetworkUtils.download(file, string, map, 52428800, workingScreen, minecraftClient.getNetworkProxy());
			}

			this.downloadTask = completableFuture.thenCompose(
					object -> !this.verifyFile(string4, file)
							? SystemUtil.completeExceptionally(new RuntimeException("Hash check failure for file " + file + ", see log"))
							: this.loadServerPack(file)
				)
				.whenComplete((void_, throwable) -> {
					if (throwable != null) {
						LOGGER.warn("Pack application failed: {}, deleting file {}", throwable.getMessage(), file);
						method_19437(file);
					}
				});
			var13 = this.downloadTask;
		} finally {
			this.lock.unlock();
		}

		return var13;
	}

	private static void method_19437(File file) {
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

	private boolean verifyFile(String string, File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			Throwable var5 = null;

			String string2;
			try {
				string2 = DigestUtils.sha1Hex(fileInputStream);
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

			if (string.isEmpty()) {
				LOGGER.info("Found file {} without verification hash", file);
				return true;
			}

			if (string2.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
				LOGGER.info("Found file {} matching requested hash {}", file, string);
				return true;
			}

			LOGGER.warn("File {} had wrong hash (expected {}, found {}).", file, string, string2);
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

	public CompletableFuture<Void> loadServerPack(File file) {
		PackResourceMetadata packResourceMetadata = null;
		NativeImage nativeImage = null;
		String string = null;

		try {
			ZipResourcePack zipResourcePack = new ZipResourcePack(file);
			Throwable var6 = null;

			try {
				packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);

				try {
					InputStream inputStream = zipResourcePack.openRoot("pack.png");
					Throwable var8 = null;

					try {
						nativeImage = NativeImage.fromInputStream(inputStream);
					} catch (Throwable var35) {
						var8 = var35;
						throw var35;
					} finally {
						if (inputStream != null) {
							if (var8 != null) {
								try {
									inputStream.close();
								} catch (Throwable var34) {
									var8.addSuppressed(var34);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (IllegalArgumentException | IOException var37) {
					LOGGER.info("Could not read pack.png: {}", var37.getMessage());
				}
			} catch (Throwable var38) {
				var6 = var38;
				throw var38;
			} finally {
				if (zipResourcePack != null) {
					if (var6 != null) {
						try {
							zipResourcePack.close();
						} catch (Throwable var33) {
							var6.addSuppressed(var33);
						}
					} else {
						zipResourcePack.close();
					}
				}
			}
		} catch (IOException var40) {
			string = var40.getMessage();
		}

		if (string != null) {
			return SystemUtil.completeExceptionally(new RuntimeException(String.format("Invalid resourcepack at %s: %s", file, string)));
		} else {
			LOGGER.info("Applying server pack {}", file);
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
