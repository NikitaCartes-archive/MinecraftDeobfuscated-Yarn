package net.minecraft.server.integrated;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DatapackFailureScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class IntegratedServerLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftClient client;
	private final LevelStorage storage;

	public IntegratedServerLoader(MinecraftClient client, LevelStorage storage) {
		this.client = client;
		this.storage = storage;
	}

	public void start(String levelName) {
		this.start(levelName, false, true);
	}

	public void createAndStart(String levelName, LevelInfo levelInfo, DynamicRegistryManager dynamicRegistryManager, GeneratorOptions generatorOptions) {
		LevelStorage.Session session = this.createSession(levelName);
		if (session != null) {
			ResourcePackManager resourcePackManager = createDataPackManager(session);
			DataPackSettings dataPackSettings = levelInfo.getDataPackSettings();

			try {
				SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataPackSettings, false);
				SaveLoader saveLoader = this.createSaveLoader(
					dataPacks,
					(resourceManager, dataPackSettingsx) -> Pair.of(new LevelProperties(levelInfo, generatorOptions, Lifecycle.stable()), dynamicRegistryManager.toImmutable())
				);
				this.client.startIntegratedServer(levelName, session, resourcePackManager, saveLoader);
			} catch (Exception var10) {
				LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)var10);
				close(session, levelName);
			}
		}
	}

	@Nullable
	private LevelStorage.Session createSession(String levelName) {
		try {
			return this.storage.createSession(levelName);
		} catch (IOException var3) {
			LOGGER.warn("Failed to read level {} data", levelName, var3);
			SystemToast.addWorldAccessFailureToast(this.client, levelName);
			this.client.setScreen(null);
			return null;
		}
	}

	public void start(
		LevelStorage.Session session, DataPackContents dataPackContents, DynamicRegistryManager.Immutable dynamicRegistryManager, SaveProperties saveProperties
	) {
		ResourcePackManager resourcePackManager = createDataPackManager(session);
		LifecycledResourceManager lifecycledResourceManager = new SaveLoading.DataPacks(resourcePackManager, saveProperties.getDataPackSettings(), false)
			.load()
			.getSecond();
		this.client
			.startIntegratedServer(
				session.getDirectoryName(),
				session,
				resourcePackManager,
				new SaveLoader(lifecycledResourceManager, dataPackContents, dynamicRegistryManager, saveProperties)
			);
	}

	private static ResourcePackManager createDataPackManager(LevelStorage.Session session) {
		return new ResourcePackManager(
			ResourceType.SERVER_DATA,
			new VanillaDataPackProvider(),
			new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD)
		);
	}

	private SaveLoader createSaveLoader(LevelStorage.Session session, boolean safeMode, ResourcePackManager dataPackManager) throws Exception {
		DataPackSettings dataPackSettings = session.getDataPackSettings();
		if (dataPackSettings == null) {
			throw new IllegalStateException("Failed to load data pack config");
		} else {
			SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataPackSettings, safeMode);
			return this.createSaveLoader(dataPacks, (resourceManager, dataPackSettingsx) -> {
				DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
				DynamicOps<NbtElement> dynamicOps = RegistryOps.ofLoaded(NbtOps.INSTANCE, mutable, resourceManager);
				SaveProperties saveProperties = session.readLevelProperties(dynamicOps, dataPackSettingsx, mutable.getRegistryLifecycle());
				if (saveProperties == null) {
					throw new IllegalStateException("Failed to load world");
				} else {
					return Pair.of(saveProperties, mutable.toImmutable());
				}
			});
		}
	}

	public SaveLoader createSaveLoader(LevelStorage.Session session, boolean safeMode) throws Exception {
		ResourcePackManager resourcePackManager = createDataPackManager(session);
		return this.createSaveLoader(session, safeMode, resourcePackManager);
	}

	private SaveLoader createSaveLoader(SaveLoading.DataPacks dataPacks, SaveLoading.LoadContextSupplier<SaveProperties> savePropertiesSupplier) throws Exception {
		SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
		CompletableFuture<SaveLoader> completableFuture = SaveLoader.load(serverConfig, savePropertiesSupplier, Util.getMainWorkerExecutor(), this.client);
		this.client.runTasks(completableFuture::isDone);
		return (SaveLoader)completableFuture.get();
	}

	private void start(String levelName, boolean safeMode, boolean canShowBackupPrompt) {
		LevelStorage.Session session = this.createSession(levelName);
		if (session != null) {
			ResourcePackManager resourcePackManager = createDataPackManager(session);

			SaveLoader saveLoader;
			try {
				saveLoader = this.createSaveLoader(session, safeMode, resourcePackManager);
			} catch (Exception var10) {
				LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)var10);
				this.client.setScreen(new DatapackFailureScreen(() -> this.start(levelName, true, canShowBackupPrompt)));
				close(session, levelName);
				return;
			}

			SaveProperties saveProperties = saveLoader.saveProperties();
			boolean bl = saveProperties.getGeneratorOptions().isLegacyCustomizedType();
			boolean bl2 = saveProperties.getLifecycle() != Lifecycle.stable();
			if (!canShowBackupPrompt || !bl && !bl2) {
				this.client.startIntegratedServer(levelName, session, resourcePackManager, saveLoader);
			} else {
				this.showBackupPromptScreen(levelName, bl, () -> this.start(levelName, safeMode, false));
				saveLoader.close();
				close(session, levelName);
			}
		}
	}

	private static void close(LevelStorage.Session session, String levelName) {
		try {
			session.close();
		} catch (IOException var3) {
			LOGGER.warn("Failed to unlock access to level {}", levelName, var3);
		}
	}

	private void showBackupPromptScreen(String levelName, boolean customized, Runnable callback) {
		Text text;
		Text text2;
		if (customized) {
			text = new TranslatableText("selectWorld.backupQuestion.customized");
			text2 = new TranslatableText("selectWorld.backupWarning.customized");
		} else {
			text = new TranslatableText("selectWorld.backupQuestion.experimental");
			text2 = new TranslatableText("selectWorld.backupWarning.experimental");
		}

		this.client.setScreen(new BackupPromptScreen(null, (backup, eraseCache) -> {
			if (backup) {
				EditWorldScreen.onBackupConfirm(this.storage, levelName);
			}

			callback.run();
		}, text, text2, false));
	}

	public static void tryLoad(MinecraftClient client, CreateWorldScreen parent, Lifecycle lifecycle, Runnable loader) {
		BooleanConsumer booleanConsumer = confirmed -> {
			if (confirmed) {
				loader.run();
			} else {
				client.setScreen(parent);
			}
		};
		if (lifecycle == Lifecycle.stable()) {
			loader.run();
		} else if (lifecycle == Lifecycle.experimental()) {
			client.setScreen(
				new ConfirmScreen(
					booleanConsumer,
					new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"),
					new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")
				)
			);
		} else {
			client.setScreen(
				new ConfirmScreen(
					booleanConsumer,
					new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"),
					new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")
				)
			);
		}
	}
}
