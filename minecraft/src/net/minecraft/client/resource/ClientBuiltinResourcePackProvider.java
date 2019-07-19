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
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
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
	public <T extends ResourcePackProfile> void register(Map<String, T> registry, ResourcePackProfile.Factory<T> factory) {
		T resourcePackProfile = ResourcePackProfile.of("vanilla", true, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM);
		if (resourcePackProfile != null) {
			registry.put("vanilla", resourcePackProfile);
		}

		if (this.serverContainer != null) {
			registry.put("server", this.serverContainer);
		}

		File file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"));
		if (file != null && file.isFile()) {
			T resourcePackProfile2 = ResourcePackProfile.of("programer_art", false, () -> new ZipResourcePack(file) {
					@Override
					public String getName() {
						return "Programmer Art";
					}
				}, factory, ResourcePackProfile.InsertionPosition.TOP);
			if (resourcePackProfile2 != null) {
				registry.put("programer_art", resourcePackProfile2);
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
				completableFuture = NetworkUtils.download(file, string, map, 52428800, progressScreen, minecraftClient.getNetworkProxy());
			}

			this.downloadTask = completableFuture.thenCompose(
					object -> !this.verifyFile(string4, file)
							? Util.completeExceptionally(new RuntimeException("Hash check failure for file " + file + ", see log"))
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

	public CompletableFuture<Void> loadServerPack(File packZip) {
		PackResourceMetadata packResourceMetadata = null;
		NativeImage nativeImage = null;
		String string = null;

		try {
			ZipResourcePack zipResourcePack = new ZipResourcePack(packZip);
			Throwable var6 = null;

			try {
				packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);

				try {
					InputStream inputStream = zipResourcePack.openRoot("pack.png");
					Throwable var8 = null;

					try {
						nativeImage = NativeImage.read(inputStream);
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
			return Util.completeExceptionally(new RuntimeException(String.format("Invalid resourcepack at %s: %s", packZip, string)));
		} else {
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
				nativeImage
			);
			return MinecraftClient.getInstance().reloadResourcesConcurrently();
		}
	}
}
