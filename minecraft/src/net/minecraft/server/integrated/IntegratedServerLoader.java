package net.minecraft.server.integrated;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DatapackFailureScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
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

	public void start(Screen parent, String levelName) {
		this.start(parent, levelName, false, true);
	}

	public void createAndStart(
		String levelName,
		LevelInfo levelInfo,
		GeneratorOptions dynamicRegistryManager,
		Function<DynamicRegistryManager, DimensionOptionsRegistryHolder> dimensionsRegistrySupplier
	) {
		LevelStorage.Session session = this.createSession(levelName);
		if (session != null) {
			ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
			DataConfiguration dataConfiguration = levelInfo.getDataConfiguration();

			try {
				SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataConfiguration, false, false);
				SaveLoader saveLoader = this.load(
					dataPacks,
					context -> {
						DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = ((DimensionOptionsRegistryHolder)dimensionsRegistrySupplier.apply(
								context.worldGenRegistryManager()
							))
							.toConfig(context.dimensionsRegistryManager().get(RegistryKeys.DIMENSION));
						return new SaveLoading.LoadContext<>(
							new LevelProperties(levelInfo, dynamicRegistryManager, dimensionsConfig.specialWorldProperty(), dimensionsConfig.getLifecycle()),
							dimensionsConfig.toDynamicRegistryManager()
						);
					},
					SaveLoader::new
				);
				this.client.startIntegratedServer(levelName, session, resourcePackManager, saveLoader, true);
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
		LevelStorage.Session session,
		DataPackContents dataPackContents,
		CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistryManager,
		SaveProperties saveProperties
	) {
		ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
		LifecycledResourceManager lifecycledResourceManager = new SaveLoading.DataPacks(resourcePackManager, saveProperties.getDataConfiguration(), false, false)
			.load()
			.getSecond();
		this.client
			.startIntegratedServer(
				session.getDirectoryName(),
				session,
				resourcePackManager,
				new SaveLoader(lifecycledResourceManager, dataPackContents, dynamicRegistryManager, saveProperties),
				true
			);
	}

	private SaveLoader createSaveLoader(LevelStorage.Session session, boolean safeMode, ResourcePackManager dataPackManager) throws Exception {
		SaveLoading.DataPacks dataPacks = this.createDataPackConfig(session, safeMode, dataPackManager);
		return this.load(
			dataPacks,
			context -> {
				DynamicOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.worldGenRegistryManager());
				Registry<DimensionOptions> registry = context.dimensionsRegistryManager().get(RegistryKeys.DIMENSION);
				Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> pair = session.readLevelProperties(
					dynamicOps, context.dataConfiguration(), registry, context.worldGenRegistryManager().getRegistryLifecycle()
				);
				if (pair == null) {
					throw new IllegalStateException("Failed to load world");
				} else {
					return new SaveLoading.LoadContext<>(pair.getFirst(), pair.getSecond().toDynamicRegistryManager());
				}
			},
			SaveLoader::new
		);
	}

	public Pair<LevelInfo, GeneratorOptionsHolder> loadForRecreation(LevelStorage.Session session) throws Exception {
		ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
		SaveLoading.DataPacks dataPacks = this.createDataPackConfig(session, false, resourcePackManager);

		@Environment(EnvType.CLIENT)
		record CurrentSettings(LevelInfo levelInfo, GeneratorOptions options, Registry<DimensionOptions> existingDimensionRegistry) {
		}

		return this.load(
			dataPacks,
			context -> {
				DynamicOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.worldGenRegistryManager());
				Registry<DimensionOptions> registry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.stable()).freeze();
				Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> pair = session.readLevelProperties(
					dynamicOps, context.dataConfiguration(), registry, context.worldGenRegistryManager().getRegistryLifecycle()
				);
				if (pair == null) {
					throw new IllegalStateException("Failed to load world");
				} else {
					return new SaveLoading.LoadContext<>(
						new CurrentSettings(pair.getFirst().getLevelInfo(), pair.getFirst().getGeneratorOptions(), pair.getSecond().dimensions()),
						context.dimensionsRegistryManager()
					);
				}
			},
			(resourceManager, dataPackContents, combinedRegistryManager, currentSettings) -> {
				resourceManager.close();
				return Pair.of(
					currentSettings.levelInfo,
					new GeneratorOptionsHolder(
						currentSettings.options,
						new DimensionOptionsRegistryHolder(currentSettings.existingDimensionRegistry),
						combinedRegistryManager,
						dataPackContents,
						currentSettings.levelInfo.getDataConfiguration()
					)
				);
			}
		);
	}

	private SaveLoading.DataPacks createDataPackConfig(LevelStorage.Session session, boolean safeMode, ResourcePackManager dataPackManager) {
		DataConfiguration dataConfiguration = session.getDataPackSettings();
		if (dataConfiguration == null) {
			throw new IllegalStateException("Failed to load data pack config");
		} else {
			return new SaveLoading.DataPacks(dataPackManager, dataConfiguration, safeMode, false);
		}
	}

	public SaveLoader createSaveLoader(LevelStorage.Session session, boolean safeMode) throws Exception {
		ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
		return this.createSaveLoader(session, safeMode, resourcePackManager);
	}

	private <D, R> R load(
		SaveLoading.DataPacks dataPacks, SaveLoading.LoadContextSupplier<D> loadContextSupplier, SaveLoading.SaveApplierFactory<D, R> saveApplierFactory
	) throws Exception {
		SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
		CompletableFuture<R> completableFuture = SaveLoading.load(serverConfig, loadContextSupplier, saveApplierFactory, Util.getMainWorkerExecutor(), this.client);
		this.client.runTasks(completableFuture::isDone);
		return (R)completableFuture.get();
	}

	private void start(Screen parent, String levelName, boolean safeMode, boolean canShowBackupPrompt) {
		LevelStorage.Session session = this.createSession(levelName);
		if (session != null) {
			ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);

			SaveLoader saveLoader;
			try {
				saveLoader = this.createSaveLoader(session, safeMode, resourcePackManager);
			} catch (Exception var11) {
				LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", (Throwable)var11);
				this.client.setScreen(new DatapackFailureScreen(() -> this.start(parent, levelName, true, canShowBackupPrompt)));
				close(session, levelName);
				return;
			}

			SaveProperties saveProperties = saveLoader.saveProperties();
			boolean bl = saveProperties.getGeneratorOptions().isLegacyCustomizedType();
			boolean bl2 = saveProperties.getLifecycle() != Lifecycle.stable();
			if (!canShowBackupPrompt || !bl && !bl2) {
				this.client.getServerResourcePackProvider().loadServerPack(session).thenApply(void_ -> true).exceptionallyComposeAsync(throwable -> {
					LOGGER.warn("Failed to load pack: ", throwable);
					return this.showPackLoadFailureScreen();
				}, this.client).thenAcceptAsync(proceed -> {
					if (proceed) {
						this.client.startIntegratedServer(levelName, session, resourcePackManager, saveLoader, false);
					} else {
						saveLoader.close();
						close(session, levelName);
						this.client.getServerResourcePackProvider().clear().thenRunAsync(() -> this.client.setScreen(parent), this.client);
					}
				}, this.client).exceptionally(throwable -> {
					this.client.setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Load world"));
					return null;
				});
			} else {
				this.showBackupPromptScreen(parent, levelName, bl, () -> this.start(parent, levelName, safeMode, false));
				saveLoader.close();
				close(session, levelName);
			}
		}
	}

	private CompletableFuture<Boolean> showPackLoadFailureScreen() {
		CompletableFuture<Boolean> completableFuture = new CompletableFuture();
		this.client
			.setScreen(
				new ConfirmScreen(
					completableFuture::complete,
					Text.translatable("multiplayer.texturePrompt.failure.line1"),
					Text.translatable("multiplayer.texturePrompt.failure.line2"),
					ScreenTexts.PROCEED,
					ScreenTexts.CANCEL
				)
			);
		return completableFuture;
	}

	private static void close(LevelStorage.Session session, String levelName) {
		try {
			session.close();
		} catch (IOException var3) {
			LOGGER.warn("Failed to unlock access to level {}", levelName, var3);
		}
	}

	private void showBackupPromptScreen(Screen parent, String levelName, boolean customized, Runnable callback) {
		Text text;
		Text text2;
		if (customized) {
			text = Text.translatable("selectWorld.backupQuestion.customized");
			text2 = Text.translatable("selectWorld.backupWarning.customized");
		} else {
			text = Text.translatable("selectWorld.backupQuestion.experimental");
			text2 = Text.translatable("selectWorld.backupWarning.experimental");
		}

		this.client.setScreen(new BackupPromptScreen(parent, (backup, eraseCache) -> {
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
					booleanConsumer, Text.translatable("selectWorld.warning.experimental.title"), Text.translatable("selectWorld.warning.experimental.question")
				)
			);
		} else {
			client.setScreen(
				new ConfirmScreen(booleanConsumer, Text.translatable("selectWorld.warning.deprecated.title"), Text.translatable("selectWorld.warning.deprecated.question"))
			);
		}
	}
}
