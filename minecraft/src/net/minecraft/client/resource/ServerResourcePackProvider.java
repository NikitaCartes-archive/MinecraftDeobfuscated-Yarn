package net.minecraft.client.resource;

import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerResourcePackProvider implements ResourcePackProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
	private static final int MAX_FILE_SIZE = 262144000;
	private static final int MAX_SAVED_PACKS = 10;
	private static final String SERVER = "server";
	private static final Text SERVER_NAME_TEXT = Text.translatable("resourcePack.server.name");
	private static final Text APPLYING_PACK_TEXT = Text.translatable("multiplayer.applyingPack");
	private final File serverPacksRoot;
	private final ReentrantLock lock = new ReentrantLock();
	@Nullable
	private CompletableFuture<?> downloadTask;
	@Nullable
	private ResourcePackProfile serverContainer;

	public ServerResourcePackProvider(File serverPacksRoot) {
		this.serverPacksRoot = serverPacksRoot;
	}

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder) {
		if (this.serverContainer != null) {
			profileAdder.accept(this.serverContainer);
		}
	}

	private static Map<String, String> getDownloadHeaders() {
		return Map.of(
			"X-Minecraft-Username",
			MinecraftClient.getInstance().getSession().getUsername(),
			"X-Minecraft-UUID",
			MinecraftClient.getInstance().getSession().getUuid(),
			"X-Minecraft-Version",
			SharedConstants.getGameVersion().getName(),
			"X-Minecraft-Version-ID",
			SharedConstants.getGameVersion().getId(),
			"X-Minecraft-Pack-Format",
			String.valueOf(ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion())),
			"User-Agent",
			"Minecraft Java/" + SharedConstants.getGameVersion().getName()
		);
	}

	public CompletableFuture<?> download(URL url, String packSha1, boolean closeAfterDownload) {
		String string = Hashing.sha1().hashString(url.toString(), StandardCharsets.UTF_8).toString();
		String string2 = SHA1_PATTERN.matcher(packSha1).matches() ? packSha1 : "";
		this.lock.lock();

		CompletableFuture var14;
		try {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			File file = new File(this.serverPacksRoot, string);
			CompletableFuture<?> completableFuture;
			if (file.exists()) {
				completableFuture = CompletableFuture.completedFuture("");
			} else {
				ProgressScreen progressScreen = new ProgressScreen(closeAfterDownload);
				Map<String, String> map = getDownloadHeaders();
				minecraftClient.submitAndJoin(() -> minecraftClient.setScreen(progressScreen));
				completableFuture = NetworkUtils.downloadResourcePack(file, url, map, 262144000, progressScreen, minecraftClient.getNetworkProxy());
			}

			this.downloadTask = completableFuture.thenCompose(object -> {
					if (!this.verifyFile(string2, file)) {
						return CompletableFuture.failedFuture(new RuntimeException("Hash check failure for file " + file + ", see log"));
					} else {
						minecraftClient.execute(() -> {
							if (!closeAfterDownload) {
								minecraftClient.setScreen(new MessageScreen(APPLYING_PACK_TEXT));
							}
						});
						return this.loadServerPack(file, ResourcePackSource.SERVER);
					}
				})
				.exceptionallyCompose(
					throwable -> this.clear()
							.thenAcceptAsync(void_ -> {
								LOGGER.warn("Pack application failed: {}, deleting file {}", throwable.getMessage(), file);
								delete(file);
							}, Util.getIoWorkerExecutor())
							.thenAcceptAsync(
								void_ -> minecraftClient.setScreen(
										new ConfirmScreen(
											confirmed -> {
												if (confirmed) {
													minecraftClient.setScreen(null);
												} else {
													ClientPlayNetworkHandler clientPlayNetworkHandler = minecraftClient.getNetworkHandler();
													if (clientPlayNetworkHandler != null) {
														clientPlayNetworkHandler.getConnection().disconnect(Text.translatable("connect.aborted"));
													}
												}
											},
											Text.translatable("multiplayer.texturePrompt.failure.line1"),
											Text.translatable("multiplayer.texturePrompt.failure.line2"),
											ScreenTexts.PROCEED,
											Text.translatable("menu.disconnect")
										)
									),
								minecraftClient
							)
				)
				.thenAcceptAsync(void_ -> this.deleteOldServerPack(), Util.getIoWorkerExecutor());
			var14 = this.downloadTask;
		} finally {
			this.lock.unlock();
		}

		return var14;
	}

	private static void delete(File file) {
		try {
			Files.delete(file.toPath());
		} catch (IOException var2) {
			LOGGER.warn("Failed to delete file {}: {}", file, var2.getMessage());
		}
	}

	public CompletableFuture<Void> clear() {
		this.lock.lock();

		try {
			if (this.downloadTask != null) {
				this.downloadTask.cancel(true);
			}

			this.downloadTask = null;
			if (this.serverContainer != null) {
				this.serverContainer = null;
				return MinecraftClient.getInstance().reloadResourcesConcurrently();
			}
		} finally {
			this.lock.unlock();
		}

		return CompletableFuture.completedFuture(null);
	}

	private boolean verifyFile(String expectedSha1, File file) {
		try {
			String string = com.google.common.io.Files.asByteSource(file).hash(Hashing.sha1()).toString();
			if (expectedSha1.isEmpty()) {
				LOGGER.info("Found file {} without verification hash", file);
				return true;
			}

			if (string.toLowerCase(Locale.ROOT).equals(expectedSha1.toLowerCase(Locale.ROOT))) {
				LOGGER.info("Found file {} matching requested hash {}", file, expectedSha1);
				return true;
			}

			LOGGER.warn("File {} had wrong hash (expected {}, found {}).", file, expectedSha1, string);
		} catch (IOException var4) {
			LOGGER.warn("File {} couldn't be hashed.", file, var4);
		}

		return false;
	}

	private void deleteOldServerPack() {
		if (this.serverPacksRoot.isDirectory()) {
			try {
				List<File> list = new ArrayList(FileUtils.listFiles(this.serverPacksRoot, TrueFileFilter.TRUE, null));
				list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
				int i = 0;

				for (File file : list) {
					if (i++ >= 10) {
						LOGGER.info("Deleting old server resource pack {}", file.getName());
						FileUtils.deleteQuietly(file);
					}
				}
			} catch (Exception var5) {
				LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
			}
		}
	}

	public CompletableFuture<Void> loadServerPack(File packZip, ResourcePackSource packSource) {
		ResourcePackProfile.PackFactory packFactory = name -> new ZipResourcePack(name, packZip);
		ResourcePackProfile.Metadata metadata = ResourcePackProfile.loadMetadata("server", packFactory);
		if (metadata == null) {
			return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid pack metadata at " + packZip));
		} else {
			LOGGER.info("Applying server pack {}", packZip);
			this.serverContainer = ResourcePackProfile.of(
				"server", SERVER_NAME_TEXT, true, packFactory, metadata, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.TOP, true, packSource
			);
			return MinecraftClient.getInstance().reloadResourcesConcurrently();
		}
	}

	public CompletableFuture<Void> loadServerPack(LevelStorage.Session session) {
		Path path = session.getDirectory(WorldSavePath.RESOURCES_ZIP);
		return Files.exists(path, new LinkOption[0]) && !Files.isDirectory(path, new LinkOption[0])
			? this.loadServerPack(path.toFile(), ResourcePackSource.WORLD)
			: CompletableFuture.completedFuture(null);
	}
}
