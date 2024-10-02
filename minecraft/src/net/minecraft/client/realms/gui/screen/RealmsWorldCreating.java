package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.FailedRealmsUploadException;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.client.realms.util.RealmsUploader;
import net.minecraft.client.realms.util.UploadProgressTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelProperties;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsWorldCreating {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void showCreateWorldScreen(
		MinecraftClient client, Screen parent, Screen realmsScreen, int slotId, RealmsServer server, @Nullable WorldCreationTask creationTask
	) {
		CreateWorldScreen.show(
			client,
			parent,
			(screen, dynamicRegistries, levelProperties, dataPackTempDir) -> {
				Path path;
				try {
					path = saveTempWorld(dynamicRegistries, levelProperties, dataPackTempDir);
				} catch (IOException var13) {
					LOGGER.warn("Failed to create temporary world folder.");
					client.setScreen(new RealmsGenericErrorScreen(Text.translatable("mco.create.world.failed"), realmsScreen));
					return true;
				}

				RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.create(levelProperties.getLevelInfo(), SharedConstants.getGameVersion().getName());
				RealmsUploader realmsUploader = new RealmsUploader(path, realmsWorldOptions, client.getSession(), server.id, slotId, UploadProgressTracker.create());
				client.setScreenAndRender(
					new NoticeScreen(realmsUploader::cancel, Text.translatable("mco.create.world.reset.title"), Text.empty(), ScreenTexts.CANCEL, false)
				);
				if (creationTask != null) {
					creationTask.run();
				}

				realmsUploader.upload().handleAsync((v, throwable) -> {
					if (throwable != null) {
						if (throwable instanceof CompletionException completionException) {
							throwable = completionException.getCause();
						}

						if (throwable instanceof CancelledRealmsUploadException) {
							client.setScreenAndRender(realmsScreen);
						} else {
							if (throwable instanceof FailedRealmsUploadException failedRealmsUploadException) {
								LOGGER.warn("Failed to create realms world {}", failedRealmsUploadException.getStatus());
							} else {
								LOGGER.warn("Failed to create realms world {}", throwable.getMessage());
							}

							client.setScreenAndRender(new RealmsGenericErrorScreen(Text.translatable("mco.create.world.failed"), realmsScreen));
						}
					} else {
						if (parent instanceof RealmsConfigureWorldScreen realmsConfigureWorldScreen) {
							realmsConfigureWorldScreen.fetchServerData(server.id);
						}

						if (creationTask != null) {
							RealmsMainScreen.play(server, parent, true);
						} else {
							client.setScreenAndRender(parent);
						}

						RealmsMainScreen.resetServerList();
					}

					return null;
				}, client);
				return true;
			}
		);
	}

	private static Path saveTempWorld(
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, LevelProperties levelProperties, @Nullable Path dataPackTempDir
	) throws IOException {
		Path path = Files.createTempDirectory("minecraft_realms_world_upload");
		if (dataPackTempDir != null) {
			Files.move(dataPackTempDir, path.resolve("datapacks"));
		}

		NbtCompound nbtCompound = levelProperties.cloneWorldNbt(dynamicRegistries.getCombinedRegistryManager(), null);
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.put("Data", nbtCompound);
		Path path2 = Files.createFile(path.resolve("level.dat"));
		NbtIo.writeCompressed(nbtCompound2, path2);
		return path;
	}
}
