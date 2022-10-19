package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.CombinedDynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.ServerDynamicRegistryType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CreateWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String TEMP_DIR_PREFIX = "mcworld-";
	private static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
	private static final Text ENTER_SEED_TEXT = Text.translatable("selectWorld.enterSeed");
	private static final Text SEED_INFO_TEXT = Text.translatable("selectWorld.seedInfo");
	private static final Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");
	private static final Text RESULT_FOLDER_TEXT = Text.translatable("selectWorld.resultFolder");
	private static final Text ALLOW_COMMANDS_INFO_TEXT = Text.translatable("selectWorld.allowCommands.info");
	private static final Text PREPARING_TEXT = Text.translatable("createWorld.preparing");
	@Nullable
	private final Screen parent;
	private TextFieldWidget levelNameField;
	String saveDirectoryName;
	private CreateWorldScreen.Mode currentMode = CreateWorldScreen.Mode.SURVIVAL;
	@Nullable
	private CreateWorldScreen.Mode lastMode;
	private Difficulty currentDifficulty = Difficulty.NORMAL;
	private boolean cheatsEnabled;
	private boolean tweakedCheats;
	public boolean hardcore;
	protected DataConfiguration dataConfiguration;
	@Nullable
	private Path dataPackTempDir;
	@Nullable
	private ResourcePackManager packManager;
	private boolean moreOptionsOpen;
	private ButtonWidget createLevelButton;
	private CyclingButtonWidget<CreateWorldScreen.Mode> gameModeSwitchButton;
	private CyclingButtonWidget<Difficulty> difficultyButton;
	private ButtonWidget moreOptionsButton;
	private ButtonWidget gameRulesButton;
	private ButtonWidget dataPacksButton;
	private CyclingButtonWidget<Boolean> enableCheatsButton;
	private Text firstGameModeDescriptionLine;
	private Text secondGameModeDescriptionLine;
	private String levelName;
	private GameRules gameRules = new GameRules();
	public final MoreOptionsDialog moreOptionsDialog;

	public static void create(MinecraftClient client, @Nullable Screen parent) {
		showMessage(client, PREPARING_TEXT);
		ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider());
		SaveLoading.ServerConfig serverConfig = createServerConfig(resourcePackManager, DataConfiguration.SAFE_MODE);
		CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(
			serverConfig,
			context -> new SaveLoading.LoadContext<>(
					new CreateWorldScreen.WorldCreationSettings(
						new WorldGenSettings(GeneratorOptions.createRandom(), WorldPresets.createDemoOptions(context.worldGenRegistryManager())), context.dataConfiguration()
					),
					context.dimensionsRegistryManager()
				),
			(resourceManager, dataPackContents, combinedDynamicRegistries, generatorOptions) -> {
				resourceManager.close();
				return new GeneratorOptionsHolder(generatorOptions.worldGenSettings(), combinedDynamicRegistries, dataPackContents, generatorOptions.dataConfiguration());
			},
			Util.getMainWorkerExecutor(),
			client
		);
		client.runTasks(completableFuture::isDone);
		client.setScreen(
			new CreateWorldScreen(
				parent,
				DataConfiguration.SAFE_MODE,
				new MoreOptionsDialog((GeneratorOptionsHolder)completableFuture.join(), Optional.of(WorldPresets.DEFAULT), OptionalLong.empty())
			)
		);
	}

	public static CreateWorldScreen create(
		@Nullable Screen parent, LevelInfo levelInfo, GeneratorOptionsHolder generatorOptionsHolder, @Nullable Path dataPackTempDir
	) {
		CreateWorldScreen createWorldScreen = new CreateWorldScreen(
			parent,
			generatorOptionsHolder.dataConfiguration(),
			new MoreOptionsDialog(
				generatorOptionsHolder,
				WorldPresets.getWorldPreset(generatorOptionsHolder.selectedDimensions().dimensions()),
				OptionalLong.of(generatorOptionsHolder.generatorOptions().getSeed())
			)
		);
		createWorldScreen.levelName = levelInfo.getLevelName();
		createWorldScreen.cheatsEnabled = levelInfo.areCommandsAllowed();
		createWorldScreen.tweakedCheats = true;
		createWorldScreen.currentDifficulty = levelInfo.getDifficulty();
		createWorldScreen.gameRules.setAllValues(levelInfo.getGameRules(), null);
		if (levelInfo.isHardcore()) {
			createWorldScreen.currentMode = CreateWorldScreen.Mode.HARDCORE;
		} else if (levelInfo.getGameMode().isSurvivalLike()) {
			createWorldScreen.currentMode = CreateWorldScreen.Mode.SURVIVAL;
		} else if (levelInfo.getGameMode().isCreative()) {
			createWorldScreen.currentMode = CreateWorldScreen.Mode.CREATIVE;
		}

		createWorldScreen.dataPackTempDir = dataPackTempDir;
		return createWorldScreen;
	}

	private CreateWorldScreen(@Nullable Screen parent, DataConfiguration dataConfiguration, MoreOptionsDialog moreOptionsDialog) {
		super(Text.translatable("selectWorld.create"));
		this.parent = parent;
		this.levelName = I18n.translate("selectWorld.newWorld");
		this.dataConfiguration = dataConfiguration;
		this.moreOptionsDialog = moreOptionsDialog;
	}

	@Override
	public void tick() {
		this.levelNameField.tick();
		this.moreOptionsDialog.tickSeedTextField();
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.levelNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, Text.translatable("selectWorld.enterName")) {
			@Override
			protected MutableText getNarrationMessage() {
				return ScreenTexts.joinSentences(super.getNarrationMessage(), Text.translatable("selectWorld.resultFolder"))
					.append(" ")
					.append(CreateWorldScreen.this.saveDirectoryName);
			}
		};
		this.levelNameField.setText(this.levelName);
		this.levelNameField.setChangedListener(levelName -> {
			this.levelName = levelName;
			this.createLevelButton.active = !this.levelNameField.getText().isEmpty();
			this.updateSaveFolderName();
		});
		this.addSelectableChild(this.levelNameField);
		int i = this.width / 2 - 155;
		int j = this.width / 2 + 5;
		this.gameModeSwitchButton = this.addDrawableChild(
			CyclingButtonWidget.<CreateWorldScreen.Mode>builder(CreateWorldScreen.Mode::asText)
				.values(CreateWorldScreen.Mode.SURVIVAL, CreateWorldScreen.Mode.HARDCORE, CreateWorldScreen.Mode.CREATIVE)
				.initially(this.currentMode)
				.narration(
					button -> ClickableWidget.getNarrationMessage(button.getMessage())
							.append(ScreenTexts.SENTENCE_SEPARATOR)
							.append(this.firstGameModeDescriptionLine)
							.append(" ")
							.append(this.secondGameModeDescriptionLine)
				)
				.build(i, 100, 150, 20, GAME_MODE_TEXT, (button, mode) -> this.tweakDefaultsTo(mode))
		);
		this.difficultyButton = this.addDrawableChild(
			CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
				.values(Difficulty.values())
				.initially(this.getDifficulty())
				.build(j, 100, 150, 20, Text.translatable("options.difficulty"), (button, difficulty) -> this.currentDifficulty = difficulty)
		);
		this.enableCheatsButton = this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.cheatsEnabled && !this.hardcore)
				.narration(button -> ScreenTexts.joinSentences(button.getGenericNarrationMessage(), Text.translatable("selectWorld.allowCommands.info")))
				.build(i, 151, 150, 20, Text.translatable("selectWorld.allowCommands"), (button, cheatsEnabled) -> {
					this.tweakedCheats = true;
					this.cheatsEnabled = cheatsEnabled;
				})
		);
		this.dataPacksButton = this.addDrawableChild(new ButtonWidget(j, 151, 150, 20, Text.translatable("selectWorld.dataPacks"), button -> this.openPackScreen()));
		this.gameRulesButton = this.addDrawableChild(
			new ButtonWidget(
				i,
				185,
				150,
				20,
				Text.translatable("selectWorld.gameRules"),
				button -> this.client.setScreen(new EditGameRulesScreen(this.gameRules.copy(), optionalGameRules -> {
						this.client.setScreen(this);
						optionalGameRules.ifPresent(gameRules -> this.gameRules = gameRules);
					}))
			)
		);
		this.moreOptionsDialog.init(this, this.client, this.textRenderer);
		this.moreOptionsButton = this.addDrawableChild(
			new ButtonWidget(j, 185, 150, 20, Text.translatable("selectWorld.moreWorldOptions"), button -> this.toggleMoreOptions())
		);
		this.createLevelButton = this.addDrawableChild(
			new ButtonWidget(i, this.height - 28, 150, 20, Text.translatable("selectWorld.create"), button -> this.createLevel())
		);
		this.createLevelButton.active = !this.levelName.isEmpty();
		this.addDrawableChild(new ButtonWidget(j, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.onCloseScreen()));
		this.setMoreOptionsOpen();
		this.setInitialFocus(this.levelNameField);
		this.tweakDefaultsTo(this.currentMode);
		this.updateSaveFolderName();
	}

	private Difficulty getDifficulty() {
		return this.currentMode == CreateWorldScreen.Mode.HARDCORE ? Difficulty.HARD : this.currentDifficulty;
	}

	private void updateSettingsLabels() {
		this.firstGameModeDescriptionLine = Text.translatable("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line1");
		this.secondGameModeDescriptionLine = Text.translatable("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line2");
	}

	private void updateSaveFolderName() {
		this.saveDirectoryName = this.levelNameField.getText().trim();
		if (this.saveDirectoryName.isEmpty()) {
			this.saveDirectoryName = "World";
		}

		try {
			this.saveDirectoryName = PathUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
		} catch (Exception var4) {
			this.saveDirectoryName = "World";

			try {
				this.saveDirectoryName = PathUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
			} catch (Exception var3) {
				throw new RuntimeException("Could not create save folder", var3);
			}
		}
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	private static void showMessage(MinecraftClient client, Text text) {
		client.setScreenAndRender(new MessageScreen(text));
	}

	private void createLevel() {
		GeneratorOptionsHolder generatorOptionsHolder = this.moreOptionsDialog.getGeneratorOptionsHolder();
		DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = generatorOptionsHolder.selectedDimensions()
			.toConfig(generatorOptionsHolder.dimensionOptionsRegistry());
		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = generatorOptionsHolder.combinedDynamicRegistries()
			.with(ServerDynamicRegistryType.DIMENSIONS, dimensionsConfig.toDynamicRegistryManager());
		Lifecycle lifecycle = FeatureFlags.isNotVanilla(generatorOptionsHolder.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
		Lifecycle lifecycle2 = combinedDynamicRegistries.getCombinedRegistryManager().getRegistryLifecycle();
		Lifecycle lifecycle3 = lifecycle2.add(lifecycle);
		IntegratedServerLoader.tryLoad(
			this.client, this, lifecycle3, () -> this.startServer(dimensionsConfig.specialWorldProperty(), combinedDynamicRegistries, lifecycle3)
		);
	}

	private void startServer(
		LevelProperties.SpecialProperty specialProperty, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, Lifecycle lifecycle
	) {
		showMessage(this.client, PREPARING_TEXT);
		Optional<LevelStorage.Session> optional = this.createSession();
		if (!optional.isEmpty()) {
			this.clearDataPackTempDir();
			boolean bl = specialProperty == LevelProperties.SpecialProperty.DEBUG;
			GeneratorOptionsHolder generatorOptionsHolder = this.moreOptionsDialog.getGeneratorOptionsHolder();
			GeneratorOptions generatorOptions = this.moreOptionsDialog.getGeneratorOptionsHolder(bl, this.hardcore);
			LevelInfo levelInfo = this.createLevelInfo(bl);
			SaveProperties saveProperties = new LevelProperties(levelInfo, generatorOptions, specialProperty, lifecycle);
			this.client
				.createIntegratedServerLoader()
				.start((LevelStorage.Session)optional.get(), generatorOptionsHolder.dataPackContents(), combinedDynamicRegistries, saveProperties);
		}
	}

	private LevelInfo createLevelInfo(boolean debugWorld) {
		String string = this.levelNameField.getText().trim();
		if (debugWorld) {
			GameRules gameRules = new GameRules();
			gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
			return new LevelInfo(string, GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataConfiguration.SAFE_MODE);
		} else {
			return new LevelInfo(
				string, this.currentMode.defaultGameMode, this.hardcore, this.getDifficulty(), this.cheatsEnabled && !this.hardcore, this.gameRules, this.dataConfiguration
			);
		}
	}

	private void toggleMoreOptions() {
		this.setMoreOptionsOpen(!this.moreOptionsOpen);
	}

	private void tweakDefaultsTo(CreateWorldScreen.Mode mode) {
		if (!this.tweakedCheats) {
			this.cheatsEnabled = mode == CreateWorldScreen.Mode.CREATIVE;
			this.enableCheatsButton.setValue(this.cheatsEnabled);
		}

		if (mode == CreateWorldScreen.Mode.HARDCORE) {
			this.hardcore = true;
			this.enableCheatsButton.active = false;
			this.enableCheatsButton.setValue(false);
			this.moreOptionsDialog.disableBonusItems();
			this.difficultyButton.setValue(Difficulty.HARD);
			this.difficultyButton.active = false;
		} else {
			this.hardcore = false;
			this.enableCheatsButton.active = true;
			this.enableCheatsButton.setValue(this.cheatsEnabled);
			this.moreOptionsDialog.enableBonusItems();
			this.difficultyButton.setValue(this.currentDifficulty);
			this.difficultyButton.active = true;
		}

		this.currentMode = mode;
		this.updateSettingsLabels();
	}

	public void setMoreOptionsOpen() {
		this.setMoreOptionsOpen(this.moreOptionsOpen);
	}

	private void setMoreOptionsOpen(boolean moreOptionsOpen) {
		this.moreOptionsOpen = moreOptionsOpen;
		this.gameModeSwitchButton.visible = !moreOptionsOpen;
		this.difficultyButton.visible = !moreOptionsOpen;
		if (this.moreOptionsDialog.isDebugWorld()) {
			this.dataPacksButton.visible = false;
			this.gameModeSwitchButton.active = false;
			if (this.lastMode == null) {
				this.lastMode = this.currentMode;
			}

			this.tweakDefaultsTo(CreateWorldScreen.Mode.DEBUG);
			this.enableCheatsButton.visible = false;
		} else {
			this.gameModeSwitchButton.active = true;
			if (this.lastMode != null) {
				this.tweakDefaultsTo(this.lastMode);
			}

			this.enableCheatsButton.visible = !moreOptionsOpen;
			this.dataPacksButton.visible = !moreOptionsOpen;
		}

		this.moreOptionsDialog.setVisible(moreOptionsOpen);
		this.levelNameField.setVisible(!moreOptionsOpen);
		if (moreOptionsOpen) {
			this.moreOptionsButton.setMessage(ScreenTexts.DONE);
		} else {
			this.moreOptionsButton.setMessage(Text.translatable("selectWorld.moreWorldOptions"));
		}

		this.gameRulesButton.visible = !moreOptionsOpen;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
			return false;
		} else {
			this.createLevel();
			return true;
		}
	}

	@Override
	public void close() {
		if (this.moreOptionsOpen) {
			this.setMoreOptionsOpen(false);
		} else {
			this.onCloseScreen();
		}
	}

	public void onCloseScreen() {
		this.client.setScreen(this.parent);
		this.clearDataPackTempDir();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, -1);
		if (this.moreOptionsOpen) {
			drawTextWithShadow(matrices, this.textRenderer, ENTER_SEED_TEXT, this.width / 2 - 100, 47, -6250336);
			drawTextWithShadow(matrices, this.textRenderer, SEED_INFO_TEXT, this.width / 2 - 100, 85, -6250336);
			this.moreOptionsDialog.render(matrices, mouseX, mouseY, delta);
		} else {
			drawTextWithShadow(matrices, this.textRenderer, ENTER_NAME_TEXT, this.width / 2 - 100, 47, -6250336);
			drawTextWithShadow(
				matrices, this.textRenderer, Text.empty().append(RESULT_FOLDER_TEXT).append(" ").append(this.saveDirectoryName), this.width / 2 - 100, 85, -6250336
			);
			this.levelNameField.render(matrices, mouseX, mouseY, delta);
			drawTextWithShadow(matrices, this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2 - 150, 122, -6250336);
			drawTextWithShadow(matrices, this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2 - 150, 134, -6250336);
			if (this.enableCheatsButton.visible) {
				drawTextWithShadow(matrices, this.textRenderer, ALLOW_COMMANDS_INFO_TEXT, this.width / 2 - 150, 172, -6250336);
			}
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected <T extends Element & Selectable> T addSelectableChild(T child) {
		return super.addSelectableChild(child);
	}

	@Override
	protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		return super.addDrawableChild(drawableElement);
	}

	@Nullable
	private Path getDataPackTempDir() {
		if (this.dataPackTempDir == null) {
			try {
				this.dataPackTempDir = Files.createTempDirectory("mcworld-");
			} catch (IOException var2) {
				LOGGER.warn("Failed to create temporary dir", (Throwable)var2);
				SystemToast.addPackCopyFailure(this.client, this.saveDirectoryName);
				this.onCloseScreen();
			}
		}

		return this.dataPackTempDir;
	}

	private void openPackScreen() {
		Pair<Path, ResourcePackManager> pair = this.getScannedPack();
		if (pair != null) {
			this.client.setScreen(new PackScreen(this, pair.getSecond(), this::applyDataPacks, pair.getFirst(), Text.translatable("dataPack.title")));
		}
	}

	private void applyDataPacks(ResourcePackManager dataPackManager) {
		List<String> list = ImmutableList.copyOf(dataPackManager.getEnabledNames());
		List<String> list2 = (List<String>)dataPackManager.getNames().stream().filter(name -> !list.contains(name)).collect(ImmutableList.toImmutableList());
		DataConfiguration dataConfiguration = new DataConfiguration(new DataPackSettings(list, list2), this.dataConfiguration.enabledFeatures());
		if (list.equals(this.dataConfiguration.dataPacks().getEnabled())) {
			this.dataConfiguration = dataConfiguration;
		} else {
			FeatureSet featureSet = dataPackManager.getRequestedFeatures();
			if (FeatureFlags.isNotVanilla(featureSet)) {
				this.client.send(() -> this.client.setScreen(new ExperimentalWarningScreen(dataPackManager.getEnabledProfiles(), bl -> {
						if (bl) {
							this.validateDataPacks(dataPackManager, dataConfiguration);
						} else {
							this.openPackScreen();
						}
					})));
			} else {
				this.validateDataPacks(dataPackManager, dataConfiguration);
			}
		}
	}

	private void validateDataPacks(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration) {
		this.client.send(() -> this.client.setScreen(new MessageScreen(Text.translatable("dataPack.validation.working"))));
		SaveLoading.ServerConfig serverConfig = createServerConfig(dataPackManager, dataConfiguration);
		SaveLoading.<CreateWorldScreen.WorldCreationSettings, GeneratorOptionsHolder>load(
				serverConfig,
				context -> {
					if (context.worldGenRegistryManager().get(Registry.WORLD_PRESET_KEY).size() == 0) {
						throw new IllegalStateException("Needs at least one world preset to continue");
					} else if (context.worldGenRegistryManager().get(Registry.BIOME_KEY).size() == 0) {
						throw new IllegalStateException("Needs at least one biome continue");
					} else {
						GeneratorOptionsHolder generatorOptionsHolder = this.moreOptionsDialog.getGeneratorOptionsHolder();
						DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, generatorOptionsHolder.getCombinedRegistryManager());
						DataResult<JsonElement> dataResult = WorldGenSettings.encode(
								dynamicOps, generatorOptionsHolder.generatorOptions(), generatorOptionsHolder.selectedDimensions()
							)
							.setLifecycle(Lifecycle.stable());
						DynamicOps<JsonElement> dynamicOps2 = RegistryOps.of(JsonOps.INSTANCE, context.worldGenRegistryManager());
						WorldGenSettings worldGenSettings = dataResult.<WorldGenSettings>flatMap(json -> WorldGenSettings.CODEC.parse(dynamicOps2, json))
							.getOrThrow(false, Util.addPrefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error));
						return new SaveLoading.LoadContext<>(
							new CreateWorldScreen.WorldCreationSettings(worldGenSettings, context.dataConfiguration()), context.dimensionsRegistryManager()
						);
					}
				},
				(resourceManager, dataPackContents, combinedDynamicRegistries, context) -> {
					resourceManager.close();
					return new GeneratorOptionsHolder(context.worldGenSettings(), combinedDynamicRegistries, dataPackContents, context.dataConfiguration());
				},
				Util.getMainWorkerExecutor(),
				this.client
			)
			.thenAcceptAsync(generatorOptionsHolder -> {
				this.dataConfiguration = generatorOptionsHolder.dataConfiguration();
				this.moreOptionsDialog.setGeneratorOptionsHolder(generatorOptionsHolder);
				this.clearAndInit();
			}, this.client)
			.handle(
				(void_, throwable) -> {
					if (throwable != null) {
						LOGGER.warn("Failed to validate datapack", throwable);
						this.client
							.send(
								() -> this.client
										.setScreen(
											new ConfirmScreen(
												confirmed -> {
													if (confirmed) {
														this.openPackScreen();
													} else {
														this.dataConfiguration = DataConfiguration.SAFE_MODE;
														this.client.setScreen(this);
													}
												},
												Text.translatable("dataPack.validation.failed"),
												ScreenTexts.EMPTY,
												Text.translatable("dataPack.validation.back"),
												Text.translatable("dataPack.validation.reset")
											)
										)
							);
					} else {
						this.client.send(() -> this.client.setScreen(this));
					}

					return null;
				}
			);
	}

	private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration) {
		SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataConfiguration, false, true);
		return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
	}

	private void clearDataPackTempDir() {
		if (this.dataPackTempDir != null) {
			try {
				Stream<Path> stream = Files.walk(this.dataPackTempDir);

				try {
					stream.sorted(Comparator.reverseOrder()).forEach(path -> {
						try {
							Files.delete(path);
						} catch (IOException var2) {
							LOGGER.warn("Failed to remove temporary file {}", path, var2);
						}
					});
				} catch (Throwable var5) {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable var4) {
							var5.addSuppressed(var4);
						}
					}

					throw var5;
				}

				if (stream != null) {
					stream.close();
				}
			} catch (IOException var6) {
				LOGGER.warn("Failed to list temporary dir {}", this.dataPackTempDir);
			}

			this.dataPackTempDir = null;
		}
	}

	private static void copyDataPack(Path srcFolder, Path destFolder, Path dataPackFile) {
		try {
			Util.relativeCopy(srcFolder, destFolder, dataPackFile);
		} catch (IOException var4) {
			LOGGER.warn("Failed to copy datapack file from {} to {}", dataPackFile, destFolder);
			throw new UncheckedIOException(var4);
		}
	}

	private Optional<LevelStorage.Session> createSession() {
		try {
			LevelStorage.Session session = this.client.getLevelStorage().createSession(this.saveDirectoryName);
			if (this.dataPackTempDir == null) {
				return Optional.of(session);
			}

			try {
				Stream<Path> stream = Files.walk(this.dataPackTempDir);

				Optional var4;
				try {
					Path path = session.getDirectory(WorldSavePath.DATAPACKS);
					Files.createDirectories(path);
					stream.filter(pathx -> !pathx.equals(this.dataPackTempDir)).forEach(pathx -> copyDataPack(this.dataPackTempDir, path, pathx));
					var4 = Optional.of(session);
				} catch (Throwable var6) {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable var5) {
							var6.addSuppressed(var5);
						}
					}

					throw var6;
				}

				if (stream != null) {
					stream.close();
				}

				return var4;
			} catch (UncheckedIOException | IOException var7) {
				LOGGER.warn("Failed to copy datapacks to world {}", this.saveDirectoryName, var7);
				session.close();
			}
		} catch (UncheckedIOException | IOException var8) {
			LOGGER.warn("Failed to create access for {}", this.saveDirectoryName, var8);
		}

		SystemToast.addPackCopyFailure(this.client, this.saveDirectoryName);
		this.onCloseScreen();
		return Optional.empty();
	}

	@Nullable
	public static Path copyDataPack(Path srcFolder, MinecraftClient client) {
		MutableObject<Path> mutableObject = new MutableObject<>();

		try {
			Stream<Path> stream = Files.walk(srcFolder);

			try {
				stream.filter(dataPackFile -> !dataPackFile.equals(srcFolder)).forEach(dataPackFile -> {
					Path path2 = mutableObject.getValue();
					if (path2 == null) {
						try {
							path2 = Files.createTempDirectory("mcworld-");
						} catch (IOException var5) {
							LOGGER.warn("Failed to create temporary dir");
							throw new UncheckedIOException(var5);
						}

						mutableObject.setValue(path2);
					}

					copyDataPack(srcFolder, path2, dataPackFile);
				});
			} catch (Throwable var7) {
				if (stream != null) {
					try {
						stream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (stream != null) {
				stream.close();
			}
		} catch (UncheckedIOException | IOException var8) {
			LOGGER.warn("Failed to copy datapacks from world {}", srcFolder, var8);
			SystemToast.addPackCopyFailure(client, srcFolder.toString());
			return null;
		}

		return mutableObject.getValue();
	}

	@Nullable
	private Pair<Path, ResourcePackManager> getScannedPack() {
		Path path = this.getDataPackTempDir();
		if (path != null) {
			if (this.packManager == null) {
				this.packManager = VanillaDataPackProvider.createManager(path);
				this.packManager.scanPacks();
			}

			this.packManager.setEnabledProfiles(this.dataConfiguration.dataPacks().getEnabled());
			return Pair.of(path, this.packManager);
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum Mode {
		SURVIVAL("survival", GameMode.SURVIVAL),
		HARDCORE("hardcore", GameMode.SURVIVAL),
		CREATIVE("creative", GameMode.CREATIVE),
		DEBUG("spectator", GameMode.SPECTATOR);

		final String translationSuffix;
		final GameMode defaultGameMode;
		private final Text text;

		private Mode(String translationSuffix, GameMode defaultGameMode) {
			this.translationSuffix = translationSuffix;
			this.defaultGameMode = defaultGameMode;
			this.text = Text.translatable("selectWorld.gameMode." + translationSuffix);
		}

		public Text asText() {
			return this.text;
		}
	}

	@Environment(EnvType.CLIENT)
	static record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
	}
}
