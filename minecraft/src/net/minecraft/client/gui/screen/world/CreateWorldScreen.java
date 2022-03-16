package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.File;
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
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CreateWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String TEMP_DIR_PREFIX = "mcworld-";
	private static final Text GAME_MODE_TEXT = new TranslatableText("selectWorld.gameMode");
	private static final Text ENTER_SEED_TEXT = new TranslatableText("selectWorld.enterSeed");
	private static final Text SEED_INFO_TEXT = new TranslatableText("selectWorld.seedInfo");
	private static final Text ENTER_NAME_TEXT = new TranslatableText("selectWorld.enterName");
	private static final Text RESULT_FOLDER_TEXT = new TranslatableText("selectWorld.resultFolder");
	private static final Text ALLOW_COMMANDS_INFO_TEXT = new TranslatableText("selectWorld.allowCommands.info");
	private static final Text PREPARING_TEXT = new TranslatableText("createWorld.preparing");
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
	protected DataPackSettings dataPackSettings;
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
		ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider());
		SaveLoading.ServerConfig serverConfig = createServerConfig(resourcePackManager, DataPackSettings.SAFE_MODE);
		CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(serverConfig, (resourceManager, dataPackSettings) -> {
			DynamicRegistryManager.Immutable immutable = (DynamicRegistryManager.Immutable)DynamicRegistryManager.BUILTIN.get();
			GeneratorOptions generatorOptions = WorldPresets.createDefaultOptions(immutable);
			return Pair.of(generatorOptions, immutable);
		}, (resourceManager, dataPackContents, dynamicRegistryManager, generatorOptions) -> {
			resourceManager.close();
			return new GeneratorOptionsHolder(generatorOptions, Lifecycle.stable(), dynamicRegistryManager, dataPackContents);
		}, Util.getMainWorkerExecutor(), client);
		client.runTasks(completableFuture::isDone);
		client.setScreen(
			new CreateWorldScreen(
				parent,
				DataPackSettings.SAFE_MODE,
				new MoreOptionsDialog((GeneratorOptionsHolder)completableFuture.join(), Optional.of(WorldPresets.DEFAULT), OptionalLong.empty())
			)
		);
	}

	public static CreateWorldScreen create(@Nullable Screen parent, SaveLoader source, @Nullable Path path) {
		SaveProperties saveProperties = source.saveProperties();
		LevelInfo levelInfo = saveProperties.getLevelInfo();
		GeneratorOptions generatorOptions = saveProperties.getGeneratorOptions();
		DynamicRegistryManager.Immutable immutable = source.dynamicRegistryManager();
		GeneratorOptionsHolder generatorOptionsHolder = new GeneratorOptionsHolder(
			generatorOptions, saveProperties.getLifecycle(), immutable, source.dataPackContents()
		);
		DataPackSettings dataPackSettings = levelInfo.getDataPackSettings();
		CreateWorldScreen createWorldScreen = new CreateWorldScreen(
			parent,
			dataPackSettings,
			new MoreOptionsDialog(generatorOptionsHolder, WorldPresets.getWorldPreset(generatorOptions), OptionalLong.of(generatorOptions.getSeed()))
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

		createWorldScreen.dataPackTempDir = path;
		return createWorldScreen;
	}

	private CreateWorldScreen(@Nullable Screen parent, DataPackSettings dataPackSettings, MoreOptionsDialog moreOptionsDialog) {
		super(new TranslatableText("selectWorld.create"));
		this.parent = parent;
		this.levelName = I18n.translate("selectWorld.newWorld");
		this.dataPackSettings = dataPackSettings;
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
		this.levelNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterName")) {
			@Override
			protected MutableText getNarrationMessage() {
				return ScreenTexts.joinSentences(super.getNarrationMessage(), new TranslatableText("selectWorld.resultFolder"))
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
				.build(j, 100, 150, 20, new TranslatableText("options.difficulty"), (button, difficulty) -> this.currentDifficulty = difficulty)
		);
		this.enableCheatsButton = this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.cheatsEnabled && !this.hardcore)
				.narration(button -> ScreenTexts.joinSentences(button.getGenericNarrationMessage(), new TranslatableText("selectWorld.allowCommands.info")))
				.build(i, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), (button, cheatsEnabled) -> {
					this.tweakedCheats = true;
					this.cheatsEnabled = cheatsEnabled;
				})
		);
		this.dataPacksButton = this.addDrawableChild(
			new ButtonWidget(j, 151, 150, 20, new TranslatableText("selectWorld.dataPacks"), button -> this.openPackScreen())
		);
		this.gameRulesButton = this.addDrawableChild(
			new ButtonWidget(
				i,
				185,
				150,
				20,
				new TranslatableText("selectWorld.gameRules"),
				button -> this.client.setScreen(new EditGameRulesScreen(this.gameRules.copy(), optionalGameRules -> {
						this.client.setScreen(this);
						optionalGameRules.ifPresent(gameRules -> this.gameRules = gameRules);
					}))
			)
		);
		this.moreOptionsDialog.init(this, this.client, this.textRenderer);
		this.moreOptionsButton = this.addDrawableChild(
			new ButtonWidget(j, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), button -> this.toggleMoreOptions())
		);
		this.createLevelButton = this.addDrawableChild(
			new ButtonWidget(i, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), button -> this.createLevel())
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
		this.firstGameModeDescriptionLine = new TranslatableText("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line1");
		this.secondGameModeDescriptionLine = new TranslatableText("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line2");
	}

	private void updateSaveFolderName() {
		this.saveDirectoryName = this.levelNameField.getText().trim();
		if (this.saveDirectoryName.isEmpty()) {
			this.saveDirectoryName = "World";
		}

		try {
			this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
		} catch (Exception var4) {
			this.saveDirectoryName = "World";

			try {
				this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
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
		IntegratedServerLoader.tryLoad(this.client, this, this.moreOptionsDialog.method_41884().worldSettingsStability(), this::startServer);
	}

	private void startServer() {
		showMessage(this.client, PREPARING_TEXT);
		Optional<LevelStorage.Session> optional = this.createSession();
		if (!optional.isEmpty()) {
			this.clearDataPackTempDir();
			GeneratorOptionsHolder generatorOptionsHolder = this.moreOptionsDialog.getGeneratorOptionsHolder(this.hardcore);
			LevelInfo levelInfo = this.createLevelInfo(generatorOptionsHolder.generatorOptions().isDebugWorld());
			SaveProperties saveProperties = new LevelProperties(levelInfo, generatorOptionsHolder.generatorOptions(), generatorOptionsHolder.worldSettingsStability());
			this.client
				.method_41735()
				.start((LevelStorage.Session)optional.get(), generatorOptionsHolder.dataPackContents(), generatorOptionsHolder.dynamicRegistryManager(), saveProperties);
		}
	}

	private LevelInfo createLevelInfo(boolean debugWorld) {
		String string = this.levelNameField.getText().trim();
		if (debugWorld) {
			GameRules gameRules = new GameRules();
			gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
			return new LevelInfo(string, GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataPackSettings.SAFE_MODE);
		} else {
			return new LevelInfo(
				string, this.currentMode.defaultGameMode, this.hardcore, this.getDifficulty(), this.cheatsEnabled && !this.hardcore, this.gameRules, this.dataPackSettings
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
			this.moreOptionsButton.setMessage(new TranslatableText("selectWorld.moreWorldOptions"));
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
				matrices, this.textRenderer, new LiteralText("").append(RESULT_FOLDER_TEXT).append(" ").append(this.saveDirectoryName), this.width / 2 - 100, 85, -6250336
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
		Pair<File, ResourcePackManager> pair = this.getScannedPack();
		if (pair != null) {
			this.client.setScreen(new PackScreen(this, pair.getSecond(), this::applyDataPacks, pair.getFirst(), new TranslatableText("dataPack.title")));
		}
	}

	private void applyDataPacks(ResourcePackManager dataPackManager) {
		List<String> list = ImmutableList.copyOf(dataPackManager.getEnabledNames());
		List<String> list2 = (List<String>)dataPackManager.getNames().stream().filter(name -> !list.contains(name)).collect(ImmutableList.toImmutableList());
		DataPackSettings dataPackSettings = new DataPackSettings(list, list2);
		if (list.equals(this.dataPackSettings.getEnabled())) {
			this.dataPackSettings = dataPackSettings;
		} else {
			this.client.send(() -> this.client.setScreen(new MessageScreen(new TranslatableText("dataPack.validation.working"))));
			SaveLoading.ServerConfig serverConfig = createServerConfig(dataPackManager, dataPackSettings);
			SaveLoading.<Pair, GeneratorOptionsHolder>load(
					serverConfig,
					(resourceManager, dataPackSettingsx) -> {
						GeneratorOptionsHolder generatorOptionsHolder = this.moreOptionsDialog.method_41884();
						DynamicRegistryManager dynamicRegistryManager = generatorOptionsHolder.dynamicRegistryManager();
						DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
						DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
						DynamicOps<JsonElement> dynamicOps2 = RegistryOps.ofLoaded(JsonOps.INSTANCE, mutable, resourceManager);
						DataResult<JsonElement> dataResult = GeneratorOptions.CODEC
							.encodeStart(dynamicOps, generatorOptionsHolder.generatorOptions())
							.setLifecycle(Lifecycle.stable());
						DataResult<GeneratorOptions> dataResult2 = dataResult.flatMap(json -> GeneratorOptions.CODEC.parse(dynamicOps2, json));
						DynamicRegistryManager.Immutable immutable = mutable.toImmutable();
						Lifecycle lifecycle = dataResult2.lifecycle().add(immutable.getRegistryLifecycle());
						GeneratorOptions generatorOptions = dataResult2.getOrThrow(
							false, Util.addPrefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error)
						);
						if (immutable.get(Registry.WORLD_PRESET_WORLDGEN).size() == 0) {
							throw new IllegalStateException("Needs at least one world preset to continue");
						} else if (immutable.get(Registry.BIOME_KEY).size() == 0) {
							throw new IllegalStateException("Needs at least one biome continue");
						} else {
							return Pair.of(Pair.of(generatorOptions, lifecycle), immutable);
						}
					},
					(resourceManager, dataPackContents, dynamicRegistryManager, pair) -> {
						resourceManager.close();
						return new GeneratorOptionsHolder((GeneratorOptions)pair.getFirst(), (Lifecycle)pair.getSecond(), dynamicRegistryManager, dataPackContents);
					},
					Util.getMainWorkerExecutor(),
					this.client
				)
				.thenAcceptAsync(generatorOptionsHolder -> {
					this.dataPackSettings = dataPackSettings;
					this.moreOptionsDialog.setGeneratorOptionsHolder(generatorOptionsHolder);
					this.clearAndInit();
				}, this.client)
				.handle(
					(v, throwable) -> {
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
															this.dataPackSettings = DataPackSettings.SAFE_MODE;
															this.client.setScreen(this);
														}
													},
													new TranslatableText("dataPack.validation.failed"),
													LiteralText.EMPTY,
													new TranslatableText("dataPack.validation.back"),
													new TranslatableText("dataPack.validation.reset")
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
	}

	private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings) {
		SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataPackSettings, false);
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
	private Pair<File, ResourcePackManager> getScannedPack() {
		Path path = this.getDataPackTempDir();
		if (path != null) {
			File file = path.toFile();
			if (this.packManager == null) {
				this.packManager = new ResourcePackManager(
					ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(file, ResourcePackSource.PACK_SOURCE_NONE)
				);
				this.packManager.scanPacks();
			}

			this.packManager.setEnabledProfiles(this.dataPackSettings.getEnabled());
			return Pair.of(file, this.packManager);
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
			this.text = new TranslatableText("selectWorld.gameMode." + translationSuffix);
		}

		public Text asText() {
			return this.text;
		}
	}
}
