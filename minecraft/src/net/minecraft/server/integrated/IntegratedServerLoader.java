package net.minecraft.server.integrated;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.BackupPromptScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.DataPackFailureScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.InitialWorldOptions;
import net.minecraft.client.gui.screen.world.RecoverWorldScreen;
import net.minecraft.client.gui.screen.world.SymlinkWarningScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.ParsedSaveProperties;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class IntegratedServerLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final UUID WORLD_PACK_ID = UUID.fromString("640a6a92-b6cb-48a0-b391-831586500359");
	private final MinecraftClient client;
	private final LevelStorage storage;

	public IntegratedServerLoader(MinecraftClient client, LevelStorage storage) {
		this.client = client;
		this.storage = storage;
	}

	public void createAndStart(
		String levelName,
		LevelInfo levelInfo,
		GeneratorOptions dynamicRegistryManager,
		Function<RegistryWrapper.WrapperLookup, DimensionOptionsRegistryHolder> dimensionsRegistrySupplier,
		Screen screen
	) {
		this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
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
							.toConfig(context.dimensionsRegistryManager().getOrThrow(RegistryKeys.DIMENSION));
						return new SaveLoading.LoadContext<>(
							new LevelProperties(levelInfo, dynamicRegistryManager, dimensionsConfig.specialWorldProperty(), dimensionsConfig.getLifecycle()),
							dimensionsConfig.toDynamicRegistryManager()
						);
					},
					SaveLoader::new
				);
				this.client.startIntegratedServer(session, resourcePackManager, saveLoader, true);
			} catch (Exception var11) {
				LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)var11);
				session.tryClose();
				this.client.setScreen(screen);
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
		} catch (SymlinkValidationException var4) {
			LOGGER.warn("{}", var4.getMessage());
			this.client.setScreen(SymlinkWarningScreen.world(() -> this.client.setScreen(null)));
			return null;
		}
	}

	public void startNewWorld(
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
				session, resourcePackManager, new SaveLoader(lifecycledResourceManager, dataPackContents, dynamicRegistryManager, saveProperties), true
			);
	}

	public SaveLoader load(Dynamic<?> levelProperties, boolean safeMode, ResourcePackManager dataPackManager) throws Exception {
		SaveLoading.DataPacks dataPacks = LevelStorage.parseDataPacks(levelProperties, dataPackManager, safeMode);
		return this.load(
			dataPacks,
			context -> {
				Registry<DimensionOptions> registry = context.dimensionsRegistryManager().getOrThrow(RegistryKeys.DIMENSION);
				ParsedSaveProperties parsedSaveProperties = LevelStorage.parseSaveProperties(
					levelProperties, context.dataConfiguration(), registry, context.worldGenRegistryManager()
				);
				return new SaveLoading.LoadContext<>(parsedSaveProperties.properties(), parsedSaveProperties.dimensions().toDynamicRegistryManager());
			},
			SaveLoader::new
		);
	}

	public Pair<LevelInfo, GeneratorOptionsHolder> loadForRecreation(LevelStorage.Session session) throws Exception {
		ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
		Dynamic<?> dynamic = session.readLevelProperties();
		SaveLoading.DataPacks dataPacks = LevelStorage.parseDataPacks(dynamic, resourcePackManager, false);

		@Environment(EnvType.CLIENT)
		record CurrentSettings(LevelInfo levelInfo, GeneratorOptions options, Registry<DimensionOptions> existingDimensionRegistry) {
		}

		return this.load(
			dataPacks,
			context -> {
				Registry<DimensionOptions> registry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.stable()).freeze();
				ParsedSaveProperties parsedSaveProperties = LevelStorage.parseSaveProperties(
					dynamic, context.dataConfiguration(), registry, context.worldGenRegistryManager()
				);
				return new SaveLoading.LoadContext<>(
					new CurrentSettings(
						parsedSaveProperties.properties().getLevelInfo(), parsedSaveProperties.properties().getGeneratorOptions(), parsedSaveProperties.dimensions().dimensions()
					),
					context.dimensionsRegistryManager()
				);
			},
			(resourceManager, dataPackContents, combinedRegistryManager, currentSettings) -> {
				resourceManager.close();
				InitialWorldOptions initialWorldOptions = new InitialWorldOptions(WorldCreator.Mode.SURVIVAL, Set.of(), null);
				return Pair.of(
					currentSettings.levelInfo,
					new GeneratorOptionsHolder(
						currentSettings.options,
						new DimensionOptionsRegistryHolder(currentSettings.existingDimensionRegistry),
						combinedRegistryManager,
						dataPackContents,
						currentSettings.levelInfo.getDataConfiguration(),
						initialWorldOptions
					)
				);
			}
		);
	}

	private <D, R> R load(
		SaveLoading.DataPacks dataPacks, SaveLoading.LoadContextSupplier<D> loadContextSupplier, SaveLoading.SaveApplierFactory<D, R> saveApplierFactory
	) throws Exception {
		SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
		CompletableFuture<R> completableFuture = SaveLoading.load(serverConfig, loadContextSupplier, saveApplierFactory, Util.getMainWorkerExecutor(), this.client);
		this.client.runTasks(completableFuture::isDone);
		return (R)completableFuture.get();
	}

	private void showBackupPromptScreen(LevelStorage.Session session, boolean customized, Runnable callback, Runnable onCancel) {
		Text text;
		Text text2;
		if (customized) {
			text = Text.translatable("selectWorld.backupQuestion.customized");
			text2 = Text.translatable("selectWorld.backupWarning.customized");
		} else {
			text = Text.translatable("selectWorld.backupQuestion.experimental");
			text2 = Text.translatable("selectWorld.backupWarning.experimental");
		}

		this.client.setScreen(new BackupPromptScreen(onCancel, (backup, eraseCache) -> {
			if (backup) {
				EditWorldScreen.backupLevel(session);
			}

			callback.run();
		}, text, text2, false));
	}

	public static void tryLoad(MinecraftClient client, CreateWorldScreen parent, Lifecycle lifecycle, Runnable loader, boolean bypassWarnings) {
		BooleanConsumer booleanConsumer = confirmed -> {
			if (confirmed) {
				loader.run();
			} else {
				client.setScreen(parent);
			}
		};
		if (bypassWarnings || lifecycle == Lifecycle.stable()) {
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

	public void start(String name, Runnable onCancel) {
		this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
		LevelStorage.Session session = this.createSession(name);
		if (session != null) {
			this.start(session, onCancel);
		}
	}

	private void start(LevelStorage.Session session, Runnable onCancel) {
		this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));

		Dynamic<?> dynamic;
		LevelSummary levelSummary;
		try {
			dynamic = session.readLevelProperties();
			levelSummary = session.getLevelSummary(dynamic);
		} catch (NbtException | NbtCrashException | IOException var10) {
			this.client.setScreen(new RecoverWorldScreen(this.client, confirmed -> {
				if (confirmed) {
					this.start(session, onCancel);
				} else {
					session.tryClose();
					onCancel.run();
				}
			}, session));
			return;
		} catch (OutOfMemoryError var11) {
			CrashMemoryReserve.releaseMemory();
			String string = "Ran out of memory trying to read level data of world folder \"" + session.getDirectoryName() + "\"";
			LOGGER.error(LogUtils.FATAL_MARKER, string);
			OutOfMemoryError outOfMemoryError2 = new OutOfMemoryError("Ran out of memory reading level data");
			outOfMemoryError2.initCause(var11);
			CrashReport crashReport = CrashReport.create(outOfMemoryError2, string);
			CrashReportSection crashReportSection = crashReport.addElement("World details");
			crashReportSection.add("World folder", session.getDirectoryName());
			throw new CrashException(crashReport);
		}

		this.start(session, levelSummary, dynamic, onCancel);
	}

	private void start(LevelStorage.Session session, LevelSummary summary, Dynamic<?> levelProperties, Runnable onCancel) {
		if (!summary.isVersionAvailable()) {
			session.tryClose();
			this.client
				.setScreen(
					new NoticeScreen(
						onCancel,
						Text.translatable("selectWorld.incompatible.title").withColor(Colors.RED),
						Text.translatable("selectWorld.incompatible.description", summary.getVersion())
					)
				);
		} else {
			LevelSummary.ConversionWarning conversionWarning = summary.getConversionWarning();
			if (conversionWarning.promptsBackup()) {
				String string = "selectWorld.backupQuestion." + conversionWarning.getTranslationKeySuffix();
				String string2 = "selectWorld.backupWarning." + conversionWarning.getTranslationKeySuffix();
				MutableText mutableText = Text.translatable(string);
				if (conversionWarning.isDangerous()) {
					mutableText.withColor(Colors.LIGHT_RED);
				}

				Text text = Text.translatable(string2, summary.getVersion(), SharedConstants.getGameVersion().getName());
				this.client.setScreen(new BackupPromptScreen(() -> {
					session.tryClose();
					onCancel.run();
				}, (backup, eraseCache) -> {
					if (backup) {
						EditWorldScreen.backupLevel(session);
					}

					this.start(session, levelProperties, false, onCancel);
				}, mutableText, text, false));
			} else {
				this.start(session, levelProperties, false, onCancel);
			}
		}
	}

	private void start(LevelStorage.Session session, Dynamic<?> levelProperties, boolean safeMode, Runnable onCancel) {
		this.client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.resource_load")));
		ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);

		SaveLoader saveLoader;
		try {
			saveLoader = this.load(levelProperties, safeMode, resourcePackManager);

			for (DimensionOptions dimensionOptions : saveLoader.combinedDynamicRegistries().getCombinedRegistryManager().getOrThrow(RegistryKeys.DIMENSION)) {
				dimensionOptions.chunkGenerator().initializeIndexedFeaturesList();
			}
		} catch (Exception var9) {
			LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", (Throwable)var9);
			if (!safeMode) {
				this.client.setScreen(new DataPackFailureScreen(() -> {
					session.tryClose();
					onCancel.run();
				}, () -> this.start(session, levelProperties, true, onCancel)));
			} else {
				session.tryClose();
				this.client
					.setScreen(
						new NoticeScreen(
							onCancel,
							Text.translatable("datapackFailure.safeMode.failed.title"),
							Text.translatable("datapackFailure.safeMode.failed.description"),
							ScreenTexts.BACK,
							true
						)
					);
			}

			return;
		}

		this.checkBackupAndStart(session, saveLoader, resourcePackManager, onCancel);
	}

	private void checkBackupAndStart(LevelStorage.Session session, SaveLoader saveLoader, ResourcePackManager dataPackManager, Runnable onCancel) {
		SaveProperties saveProperties = saveLoader.saveProperties();
		boolean bl = saveProperties.getGeneratorOptions().isLegacyCustomizedType();
		boolean bl2 = saveProperties.getLifecycle() != Lifecycle.stable();
		if (!bl && !bl2) {
			this.start(session, saveLoader, dataPackManager, onCancel);
		} else {
			this.showBackupPromptScreen(session, bl, () -> this.start(session, saveLoader, dataPackManager, onCancel), () -> {
				saveLoader.close();
				session.tryClose();
				onCancel.run();
			});
		}
	}

	private void start(LevelStorage.Session session, SaveLoader saveLoader, ResourcePackManager dataPackManager, Runnable onCancel) {
		ServerResourcePackLoader serverResourcePackLoader = this.client.getServerResourcePackProvider();
		this.applyWorldPack(serverResourcePackLoader, session).thenApply(v -> true).exceptionallyComposeAsync(throwable -> {
			LOGGER.warn("Failed to load pack: ", throwable);
			return this.showPackLoadFailureScreen();
		}, this.client).thenAcceptAsync(successful -> {
			if (successful) {
				this.start(session, saveLoader, serverResourcePackLoader, dataPackManager, onCancel);
			} else {
				serverResourcePackLoader.removeAll();
				saveLoader.close();
				session.tryClose();
				onCancel.run();
			}
		}, this.client).exceptionally(throwable -> {
			this.client.setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Load world"));
			return null;
		});
	}

	private void start(
		LevelStorage.Session session, SaveLoader saveLoader, ServerResourcePackLoader resourcePackLoader, ResourcePackManager dataPackManager, Runnable onCancel
	) {
		if (session.shouldShowLowDiskSpaceWarning()) {
			this.client
				.setScreen(
					new ConfirmScreen(
						confirmed -> {
							if (confirmed) {
								this.start(session, saveLoader, dataPackManager);
							} else {
								resourcePackLoader.removeAll();
								saveLoader.close();
								session.tryClose();
								onCancel.run();
							}
						},
						Text.translatable("selectWorld.warning.lowDiskSpace.title").formatted(Formatting.RED),
						Text.translatable("selectWorld.warning.lowDiskSpace.description"),
						ScreenTexts.CONTINUE,
						ScreenTexts.BACK
					)
				);
		} else {
			this.start(session, saveLoader, dataPackManager);
		}
	}

	private void start(LevelStorage.Session session, SaveLoader saveLoader, ResourcePackManager dataPackManager) {
		this.client.startIntegratedServer(session, dataPackManager, saveLoader, false);
	}

	private CompletableFuture<Void> applyWorldPack(ServerResourcePackLoader loader, LevelStorage.Session session) {
		Path path = session.getDirectory(WorldSavePath.RESOURCES_ZIP);
		if (Files.exists(path, new LinkOption[0]) && !Files.isDirectory(path, new LinkOption[0])) {
			loader.initWorldPack();
			CompletableFuture<Void> completableFuture = loader.getPackLoadFuture(WORLD_PACK_ID);
			loader.addResourcePack(WORLD_PACK_ID, path);
			return completableFuture;
		} else {
			return CompletableFuture.completedFuture(null);
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
}
