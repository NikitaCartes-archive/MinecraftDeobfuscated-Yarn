package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class CreateWorldScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String field_32434 = "mcworld-";
	private static final Text GAME_MODE_TEXT = new TranslatableText("selectWorld.gameMode");
	private static final Text ENTER_SEED_TEXT = new TranslatableText("selectWorld.enterSeed");
	private static final Text SEED_INFO_TEXT = new TranslatableText("selectWorld.seedInfo");
	private static final Text ENTER_NAME_TEXT = new TranslatableText("selectWorld.enterName");
	private static final Text RESULT_FOLDER_TEXT = new TranslatableText("selectWorld.resultFolder");
	private static final Text ALLOW_COMMANDS_INFO_TEXT = new TranslatableText("selectWorld.allowCommands.info");
	private final Screen parent;
	private TextFieldWidget levelNameField;
	private String saveDirectoryName;
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

	public CreateWorldScreen(
		@Nullable Screen parent,
		LevelInfo levelInfo,
		GeneratorOptions generatorOptions,
		@Nullable Path dataPackTempDir,
		DataPackSettings dataPackSettings,
		DynamicRegistryManager.Impl registryManager
	) {
		this(
			parent,
			dataPackSettings,
			new MoreOptionsDialog(registryManager, generatorOptions, GeneratorType.fromGeneratorOptions(generatorOptions), OptionalLong.of(generatorOptions.getSeed()))
		);
		this.levelName = levelInfo.getLevelName();
		this.cheatsEnabled = levelInfo.areCommandsAllowed();
		this.tweakedCheats = true;
		this.currentDifficulty = levelInfo.getDifficulty();
		this.gameRules.setAllValues(levelInfo.getGameRules(), null);
		if (levelInfo.isHardcore()) {
			this.currentMode = CreateWorldScreen.Mode.HARDCORE;
		} else if (levelInfo.getGameMode().isSurvivalLike()) {
			this.currentMode = CreateWorldScreen.Mode.SURVIVAL;
		} else if (levelInfo.getGameMode().isCreative()) {
			this.currentMode = CreateWorldScreen.Mode.CREATIVE;
		}

		this.dataPackTempDir = dataPackTempDir;
	}

	public static CreateWorldScreen create(@Nullable Screen parent) {
		DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
		return new CreateWorldScreen(
			parent,
			DataPackSettings.SAFE_MODE,
			new MoreOptionsDialog(
				impl,
				GeneratorOptions.getDefaultOptions(impl.get(Registry.DIMENSION_TYPE_KEY), impl.get(Registry.BIOME_KEY), impl.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY)),
				Optional.of(GeneratorType.DEFAULT),
				OptionalLong.empty()
			)
		);
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
		this.moreOptionsDialog.tick();
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.levelNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterName")) {
			@Override
			protected MutableText getNarrationMessage() {
				return super.getNarrationMessage()
					.append(". ")
					.append(new TranslatableText("selectWorld.resultFolder"))
					.append(" ")
					.append(CreateWorldScreen.this.saveDirectoryName);
			}
		};
		this.levelNameField.setText(this.levelName);
		this.levelNameField.setChangedListener(string -> {
			this.levelName = string;
			this.createLevelButton.active = !this.levelNameField.getText().isEmpty();
			this.updateSaveFolderName();
		});
		this.children.add(this.levelNameField);
		int i = this.width / 2 - 155;
		int j = this.width / 2 + 5;
		this.gameModeSwitchButton = this.addButton(
			CyclingButtonWidget.<CreateWorldScreen.Mode>builder(CreateWorldScreen.Mode::asText)
				.values(CreateWorldScreen.Mode.SURVIVAL, CreateWorldScreen.Mode.HARDCORE, CreateWorldScreen.Mode.CREATIVE)
				.initially(this.currentMode)
				.narration(
					cyclingButtonWidget -> AbstractButtonWidget.getNarrationMessage(cyclingButtonWidget.getMessage())
							.append(". ")
							.append(this.firstGameModeDescriptionLine)
							.append(" ")
							.append(this.secondGameModeDescriptionLine)
				)
				.build(i, 100, 150, 20, GAME_MODE_TEXT, (cyclingButtonWidget, mode) -> this.tweakDefaultsTo(mode))
		);
		this.difficultyButton = this.addButton(
			CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
				.values(Difficulty.values())
				.initially(this.getDifficulty())
				.build(j, 100, 150, 20, new TranslatableText("options.difficulty"), (cyclingButtonWidget, difficulty) -> this.currentDifficulty = difficulty)
		);
		this.enableCheatsButton = this.addButton(
			CyclingButtonWidget.onOffBuilder(this.cheatsEnabled && !this.hardcore)
				.narration(
					cyclingButtonWidget -> cyclingButtonWidget.getGenericNarrationMessage().append(". ").append(new TranslatableText("selectWorld.allowCommands.info"))
				)
				.build(i, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), (cyclingButtonWidget, boolean_) -> {
					this.tweakedCheats = true;
					this.cheatsEnabled = boolean_;
				})
		);
		this.dataPacksButton = this.addButton(new ButtonWidget(j, 151, 150, 20, new TranslatableText("selectWorld.dataPacks"), button -> this.method_29694()));
		this.gameRulesButton = this.addButton(
			new ButtonWidget(
				i,
				185,
				150,
				20,
				new TranslatableText("selectWorld.gameRules"),
				button -> this.client.openScreen(new EditGameRulesScreen(this.gameRules.copy(), optional -> {
						this.client.openScreen(this);
						optional.ifPresent(gameRules -> this.gameRules = gameRules);
					}))
			)
		);
		this.moreOptionsDialog.init(this, this.client, this.textRenderer);
		this.moreOptionsButton = this.addButton(
			new ButtonWidget(j, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), buttonWidget -> this.toggleMoreOptions())
		);
		this.createLevelButton = this.addButton(
			new ButtonWidget(i, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), buttonWidget -> this.createLevel())
		);
		this.createLevelButton.active = !this.levelName.isEmpty();
		this.addButton(new ButtonWidget(j, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.onCloseScreen()));
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

	private void createLevel() {
		this.client.method_29970(new SaveLevelScreen(new TranslatableText("createWorld.preparing")));
		if (this.copyTempDirDataPacks()) {
			this.clearTempResources();
			GeneratorOptions generatorOptions = this.moreOptionsDialog.getGeneratorOptions(this.hardcore);
			LevelInfo levelInfo;
			if (generatorOptions.isDebugWorld()) {
				GameRules gameRules = new GameRules();
				gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
				levelInfo = new LevelInfo(this.levelNameField.getText().trim(), GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataPackSettings.SAFE_MODE);
			} else {
				levelInfo = new LevelInfo(
					this.levelNameField.getText().trim(),
					this.currentMode.defaultGameMode,
					this.hardcore,
					this.getDifficulty(),
					this.cheatsEnabled && !this.hardcore,
					this.gameRules,
					this.dataPackSettings
				);
			}

			this.client.createWorld(this.saveDirectoryName, levelInfo, this.moreOptionsDialog.getRegistryManager(), generatorOptions);
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
	public void onClose() {
		if (this.moreOptionsOpen) {
			this.setMoreOptionsOpen(false);
		} else {
			this.onCloseScreen();
		}
	}

	public void onCloseScreen() {
		this.client.openScreen(this.parent);
		this.clearTempResources();
	}

	private void clearTempResources() {
		if (this.packManager != null) {
			this.packManager.close();
		}

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
	protected <T extends Element> T addChild(T child) {
		return super.addChild(child);
	}

	@Override
	protected <T extends AbstractButtonWidget> T addButton(T button) {
		return super.addButton(button);
	}

	@Nullable
	protected Path getDataPackTempDir() {
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

	private void method_29694() {
		Pair<File, ResourcePackManager> pair = this.method_30296();
		if (pair != null) {
			this.client.openScreen(new PackScreen(this, pair.getSecond(), this::method_29682, pair.getFirst(), new TranslatableText("dataPack.title")));
		}
	}

	private void method_29682(ResourcePackManager resourcePackManager) {
		List<String> list = ImmutableList.copyOf(resourcePackManager.getEnabledNames());
		List<String> list2 = (List<String>)resourcePackManager.getNames().stream().filter(string -> !list.contains(string)).collect(ImmutableList.toImmutableList());
		DataPackSettings dataPackSettings = new DataPackSettings(list, list2);
		if (list.equals(this.dataPackSettings.getEnabled())) {
			this.dataPackSettings = dataPackSettings;
		} else {
			this.client.send(() -> this.client.openScreen(new SaveLevelScreen(new TranslatableText("dataPack.validation.working"))));
			ServerResourceManager.reload(
					resourcePackManager.createResourcePacks(),
					this.moreOptionsDialog.getRegistryManager(),
					CommandManager.RegistrationEnvironment.INTEGRATED,
					2,
					Util.getMainWorkerExecutor(),
					this.client
				)
				.handle(
					(serverResourceManager, throwable) -> {
						if (throwable != null) {
							LOGGER.warn("Failed to validate datapack", throwable);
							this.client
								.send(
									() -> this.client
											.openScreen(
												new ConfirmScreen(
													bl -> {
														if (bl) {
															this.method_29694();
														} else {
															this.dataPackSettings = DataPackSettings.SAFE_MODE;
															this.client.openScreen(this);
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
							this.client.send(() -> {
								this.dataPackSettings = dataPackSettings;
								this.moreOptionsDialog.loadDatapacks(serverResourceManager);
								serverResourceManager.close();
								this.client.openScreen(this);
							});
						}

						return null;
					}
				);
		}
	}

	private void clearDataPackTempDir() {
		if (this.dataPackTempDir != null) {
			try {
				Stream<Path> stream = Files.walk(this.dataPackTempDir);
				Throwable var2 = null;

				try {
					stream.sorted(Comparator.reverseOrder()).forEach(path -> {
						try {
							Files.delete(path);
						} catch (IOException var2x) {
							LOGGER.warn("Failed to remove temporary file {}", path, var2x);
						}
					});
				} catch (Throwable var12) {
					var2 = var12;
					throw var12;
				} finally {
					if (stream != null) {
						if (var2 != null) {
							try {
								stream.close();
							} catch (Throwable var11) {
								var2.addSuppressed(var11);
							}
						} else {
							stream.close();
						}
					}
				}
			} catch (IOException var14) {
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
			throw new CreateWorldScreen.WorldCreationException(var4);
		}
	}

	private boolean copyTempDirDataPacks() {
		if (this.dataPackTempDir != null) {
			try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.saveDirectoryName)) {
				Stream<Path> stream = Files.walk(this.dataPackTempDir);
				Throwable var4 = null;

				try {
					Path path = session.getDirectory(WorldSavePath.DATAPACKS);
					Files.createDirectories(path);
					stream.filter(pathx -> !pathx.equals(this.dataPackTempDir)).forEach(path2 -> copyDataPack(this.dataPackTempDir, path, path2));
				} catch (Throwable var29) {
					var4 = var29;
					throw var29;
				} finally {
					if (stream != null) {
						if (var4 != null) {
							try {
								stream.close();
							} catch (Throwable var28) {
								var4.addSuppressed(var28);
							}
						} else {
							stream.close();
						}
					}
				}
			} catch (CreateWorldScreen.WorldCreationException | IOException var33) {
				LOGGER.warn("Failed to copy datapacks to world {}", this.saveDirectoryName, var33);
				SystemToast.addPackCopyFailure(this.client, this.saveDirectoryName);
				this.onCloseScreen();
				return false;
			}
		}

		return true;
	}

	@Nullable
	public static Path method_29685(Path path, MinecraftClient minecraftClient) {
		MutableObject<Path> mutableObject = new MutableObject<>();

		try {
			Stream<Path> stream = Files.walk(path);
			Throwable var4 = null;

			try {
				stream.filter(path2 -> !path2.equals(path)).forEach(path2 -> {
					Path path3 = mutableObject.getValue();
					if (path3 == null) {
						try {
							path3 = Files.createTempDirectory("mcworld-");
						} catch (IOException var5) {
							LOGGER.warn("Failed to create temporary dir");
							throw new CreateWorldScreen.WorldCreationException(var5);
						}

						mutableObject.setValue(path3);
					}

					copyDataPack(path, path3, path2);
				});
			} catch (Throwable var14) {
				var4 = var14;
				throw var14;
			} finally {
				if (stream != null) {
					if (var4 != null) {
						try {
							stream.close();
						} catch (Throwable var13) {
							var4.addSuppressed(var13);
						}
					} else {
						stream.close();
					}
				}
			}
		} catch (CreateWorldScreen.WorldCreationException | IOException var16) {
			LOGGER.warn("Failed to copy datapacks from world {}", path, var16);
			SystemToast.addPackCopyFailure(minecraftClient, path.toString());
			return null;
		}

		return mutableObject.getValue();
	}

	@Nullable
	private Pair<File, ResourcePackManager> method_30296() {
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

		private final String translationSuffix;
		private final GameMode defaultGameMode;
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

	@Environment(EnvType.CLIENT)
	static class WorldCreationException extends RuntimeException {
		public WorldCreationException(Throwable throwable) {
			super(throwable);
		}
	}
}
