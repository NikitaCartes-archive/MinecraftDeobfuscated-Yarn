package net.minecraft.client.gui.screen.world;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5359;
import net.minecraft.class_5368;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class CreateWorldScreen extends Screen {
	private static final Logger field_25480 = LogManager.getLogger();
	private final Screen parent;
	private TextFieldWidget levelNameField;
	private String saveDirectoryName;
	private CreateWorldScreen.Mode currentMode = CreateWorldScreen.Mode.SURVIVAL;
	@Nullable
	private CreateWorldScreen.Mode lastMode;
	private Difficulty field_24289 = Difficulty.NORMAL;
	private Difficulty field_24290 = Difficulty.NORMAL;
	private boolean cheatsEnabled;
	private boolean tweakedCheats;
	public boolean hardcore;
	private boolean creatingLevel;
	protected class_5359 field_25479 = class_5359.field_25393;
	@Nullable
	private Path field_25477;
	private boolean moreOptionsOpen;
	private ButtonWidget createLevelButton;
	private ButtonWidget gameModeSwitchButton;
	private ButtonWidget field_24286;
	private ButtonWidget moreOptionsButton;
	private ButtonWidget gameRulesButton;
	private ButtonWidget field_25478;
	private ButtonWidget enableCheatsButton;
	private Text firstGameModeDescriptionLine;
	private Text secondGameModeDescriptionLine;
	private String levelName;
	private GameRules gameRules = new GameRules();
	public final MoreOptionsDialog moreOptionsDialog;

	public CreateWorldScreen(
		@Nullable Screen screen, LevelInfo levelInfo, GeneratorOptions generatorOptions, @Nullable Path path, DimensionTracker.Modifiable modifiable
	) {
		this(screen, new MoreOptionsDialog(modifiable, generatorOptions));
		this.levelName = levelInfo.getLevelName();
		this.cheatsEnabled = levelInfo.isHardcore();
		this.tweakedCheats = true;
		this.field_24289 = levelInfo.getDifficulty();
		this.field_24290 = this.field_24289;
		this.gameRules.setAllValues(levelInfo.getGameRules(), null);
		this.field_25479 = levelInfo.method_29558();
		if (levelInfo.hasStructures()) {
			this.currentMode = CreateWorldScreen.Mode.HARDCORE;
		} else if (levelInfo.getGameMode().isSurvivalLike()) {
			this.currentMode = CreateWorldScreen.Mode.SURVIVAL;
		} else if (levelInfo.getGameMode().isCreative()) {
			this.currentMode = CreateWorldScreen.Mode.CREATIVE;
		}

		this.field_25477 = path;
	}

	public CreateWorldScreen(@Nullable Screen parent) {
		this(parent, new MoreOptionsDialog());
	}

	private CreateWorldScreen(@Nullable Screen screen, MoreOptionsDialog moreOptionsDialog) {
		super(new TranslatableText("selectWorld.create"));
		this.parent = screen;
		this.levelName = I18n.translate("selectWorld.newWorld");
		this.moreOptionsDialog = moreOptionsDialog;
	}

	@Override
	public void tick() {
		this.levelNameField.tick();
		this.moreOptionsDialog.tick();
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
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
			new ButtonWidget(i, 100, 150, 20, new TranslatableText("selectWorld.gameMode"), buttonWidget -> {
				switch (this.currentMode) {
					case SURVIVAL:
						this.tweakDefaultsTo(CreateWorldScreen.Mode.HARDCORE);
						break;
					case HARDCORE:
						this.tweakDefaultsTo(CreateWorldScreen.Mode.CREATIVE);
						break;
					case CREATIVE:
						this.tweakDefaultsTo(CreateWorldScreen.Mode.SURVIVAL);
				}

				buttonWidget.queueNarration(250);
			}) {
				@Override
				public Text getMessage() {
					return super.getMessage()
						.shallowCopy()
						.append(": ")
						.append(new TranslatableText("selectWorld.gameMode." + CreateWorldScreen.this.currentMode.translationSuffix));
				}

				@Override
				protected MutableText getNarrationMessage() {
					return super.getNarrationMessage()
						.append(". ")
						.append(CreateWorldScreen.this.firstGameModeDescriptionLine)
						.append(" ")
						.append(CreateWorldScreen.this.secondGameModeDescriptionLine);
				}
			}
		);
		this.field_24286 = this.addButton(new ButtonWidget(j, 100, 150, 20, new TranslatableText("options.difficulty"), buttonWidget -> {
			this.field_24289 = this.field_24289.cycle();
			this.field_24290 = this.field_24289;
			buttonWidget.queueNarration(250);
		}) {
			@Override
			public Text getMessage() {
				return new TranslatableText("options.difficulty").append(": ").append(CreateWorldScreen.this.field_24290.getTranslatableName());
			}
		});
		this.enableCheatsButton = this.addButton(
			new ButtonWidget(i, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), buttonWidget -> {
				this.tweakedCheats = true;
				this.cheatsEnabled = !this.cheatsEnabled;
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public Text getMessage() {
					return super.getMessage()
						.shallowCopy()
						.append(" ")
						.append(ScreenTexts.getToggleText(CreateWorldScreen.this.cheatsEnabled && !CreateWorldScreen.this.hardcore));
				}

				@Override
				protected MutableText getNarrationMessage() {
					return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.allowCommands.info"));
				}
			}
		);
		this.field_25478 = this.addButton(new ButtonWidget(j, 151, 150, 20, new TranslatableText("selectWorld.dataPacks"), buttonWidget -> this.method_29694()));
		this.gameRulesButton = this.addButton(
			new ButtonWidget(
				i,
				185,
				150,
				20,
				new TranslatableText("selectWorld.gameRules"),
				buttonWidget -> this.client.openScreen(new EditGameRulesScreen(this.gameRules.copy(), optional -> {
						this.client.openScreen(this);
						optional.ifPresent(gameRules -> this.gameRules = gameRules);
					}))
			)
		);
		this.moreOptionsDialog.method_28092(this, this.client, this.textRenderer);
		this.moreOptionsButton = this.addButton(
			new ButtonWidget(j, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), buttonWidget -> this.toggleMoreOptions())
		);
		this.createLevelButton = this.addButton(
			new ButtonWidget(i, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), buttonWidget -> this.createLevel())
		);
		this.createLevelButton.active = !this.levelName.isEmpty();
		this.addButton(new ButtonWidget(j, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> {
			this.method_29695();
			this.client.openScreen(this.parent);
		}));
		this.setMoreOptionsOpen();
		this.setInitialFocus(this.levelNameField);
		this.tweakDefaultsTo(this.currentMode);
		this.updateSaveFolderName();
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
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void createLevel() {
		this.client.openScreen(null);
		if (!this.creatingLevel) {
			this.creatingLevel = true;
			if (this.method_29696()) {
				GeneratorOptions generatorOptions = this.moreOptionsDialog.getGeneratorOptions(this.hardcore);
				LevelInfo levelInfo;
				if (generatorOptions.isDebugWorld()) {
					GameRules gameRules = new GameRules();
					gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
					levelInfo = new LevelInfo(this.levelNameField.getText().trim(), GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, class_5359.field_25393);
				} else {
					levelInfo = new LevelInfo(
						this.levelNameField.getText().trim(),
						this.currentMode.defaultGameMode,
						this.hardcore,
						this.field_24290,
						this.cheatsEnabled && !this.hardcore,
						this.gameRules,
						this.field_25479
					);
				}

				this.client.method_29607(this.saveDirectoryName, levelInfo, this.moreOptionsDialog.method_29700(), generatorOptions);
			}
		}
	}

	private void toggleMoreOptions() {
		this.setMoreOptionsOpen(!this.moreOptionsOpen);
	}

	private void tweakDefaultsTo(CreateWorldScreen.Mode mode) {
		if (!this.tweakedCheats) {
			this.cheatsEnabled = mode == CreateWorldScreen.Mode.CREATIVE;
		}

		if (mode == CreateWorldScreen.Mode.HARDCORE) {
			this.hardcore = true;
			this.enableCheatsButton.active = false;
			this.moreOptionsDialog.bonusItemsButton.active = false;
			this.field_24290 = Difficulty.HARD;
			this.field_24286.active = false;
		} else {
			this.hardcore = false;
			this.enableCheatsButton.active = true;
			this.moreOptionsDialog.bonusItemsButton.active = true;
			this.field_24290 = this.field_24289;
			this.field_24286.active = true;
		}

		this.currentMode = mode;
		this.updateSettingsLabels();
	}

	public void setMoreOptionsOpen() {
		this.setMoreOptionsOpen(this.moreOptionsOpen);
	}

	private void setMoreOptionsOpen(boolean moreOptionsOpen) {
		this.moreOptionsOpen = moreOptionsOpen;
		this.gameModeSwitchButton.visible = !this.moreOptionsOpen;
		this.field_24286.visible = !this.moreOptionsOpen;
		if (this.moreOptionsDialog.isDebugWorld()) {
			this.field_25478.visible = false;
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

			this.enableCheatsButton.visible = !this.moreOptionsOpen;
			this.field_25478.visible = !this.moreOptionsOpen;
		}

		this.moreOptionsDialog.setVisible(this.moreOptionsOpen);
		this.levelNameField.setVisible(!this.moreOptionsOpen);
		if (this.moreOptionsOpen) {
			this.moreOptionsButton.setMessage(ScreenTexts.DONE);
		} else {
			this.moreOptionsButton.setMessage(new TranslatableText("selectWorld.moreWorldOptions"));
		}

		this.gameRulesButton.visible = !this.moreOptionsOpen;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode != 257 && keyCode != 335) {
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
			this.client.openScreen(this.parent);
		}

		this.method_29695();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, -1);
		if (this.moreOptionsOpen) {
			this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
			this.moreOptionsDialog.render(matrices, mouseX, mouseY, delta);
		} else {
			this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawStringWithShadow(
				matrices, this.textRenderer, I18n.translate("selectWorld.resultFolder") + " " + this.saveDirectoryName, this.width / 2 - 100, 85, -6250336
			);
			this.levelNameField.render(matrices, mouseX, mouseY, delta);
			this.drawCenteredText(matrices, this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2 - 155 + 75, 122, -6250336);
			this.drawCenteredText(matrices, this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2 - 155 + 75, 134, -6250336);
			if (this.enableCheatsButton.visible) {
				this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
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
	protected Path method_29693() {
		if (this.field_25477 == null) {
			try {
				this.field_25477 = Files.createTempDirectory("mcworld-");
			} catch (IOException var2) {
				field_25480.warn("Failed to create temporary dir", (Throwable)var2);
				SystemToast.method_29627(this.client, this.saveDirectoryName);
				this.client.openScreen(this.parent);
			}
		}

		return this.field_25477;
	}

	private void method_29694() {
		Path path = this.method_29693();
		if (path != null) {
			this.client.openScreen(new class_5368(this, this.field_25479, this::method_29682, path.toFile()));
		}
	}

	private void method_29682(class_5359 arg, ResourcePackManager<ResourcePackProfile> resourcePackManager) {
		this.client.send(() -> this.client.openScreen(new SaveLevelScreen(new TranslatableText("dataPack.validation.working"))));
		ServerResourceManager.reload(
				resourcePackManager.method_29211(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getServerWorkerExecutor(), this.client
			)
			.handle(
				(serverResourceManager, throwable) -> {
					if (throwable != null) {
						field_25480.warn("Failed to validate datapack", throwable);
						this.client
							.send(
								() -> this.client
										.openScreen(
											new ConfirmScreen(
												bl -> {
													if (bl) {
														this.method_29694();
													} else {
														this.field_25479 = class_5359.field_25393;
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
							this.field_25479 = arg;
							this.client.openScreen(this);
						});
					}

					return null;
				}
			);
	}

	private void method_29695() {
		if (this.field_25477 != null) {
			try {
				Stream<Path> stream = Files.walk(this.field_25477);
				Throwable var2 = null;

				try {
					stream.sorted(Comparator.reverseOrder()).forEach(path -> {
						try {
							Files.delete(path);
						} catch (IOException var2x) {
							field_25480.warn("Failed to remove temporary file {}", path, var2x);
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
				field_25480.warn("Failed to list temporary dir {}", this.field_25477);
			}

			this.field_25477 = null;
		}
	}

	private static void method_29687(Path path, Path path2, Path path3) {
		try {
			Util.method_29775(path, path2, path3);
		} catch (IOException var4) {
			field_25480.warn("Failed to copy datapack file from {} to {}", path3, path2);
			throw new CreateWorldScreen.class_5376(var4);
		}
	}

	private boolean method_29696() {
		if (this.field_25477 != null) {
			try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.saveDirectoryName)) {
				Stream<Path> stream = Files.walk(this.field_25477);
				Throwable var4 = null;

				try {
					Path path = session.getDirectory(WorldSavePath.DATAPACKS);
					Files.createDirectories(path);
					stream.filter(pathx -> !pathx.equals(this.field_25477)).forEach(path2 -> method_29687(this.field_25477, path, path2));
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
			} catch (CreateWorldScreen.class_5376 | IOException var33) {
				field_25480.warn("Failed to copy datapacks to world {}", this.saveDirectoryName, var33);
				SystemToast.method_29627(this.client, this.saveDirectoryName);
				this.client.openScreen(this.parent);
				this.method_29695();
				return false;
			}

			this.method_29695();
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
							field_25480.warn("Failed to create temporary dir");
							throw new CreateWorldScreen.class_5376(var5);
						}

						mutableObject.setValue(path3);
					}

					method_29687(path, path3, path2);
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
		} catch (CreateWorldScreen.class_5376 | IOException var16) {
			field_25480.warn("Failed to copy datapacks from world {}", path, var16);
			SystemToast.method_29627(minecraftClient, path.toString());
			return null;
		}

		return mutableObject.getValue();
	}

	@Environment(EnvType.CLIENT)
	static enum Mode {
		SURVIVAL("survival", GameMode.SURVIVAL),
		HARDCORE("hardcore", GameMode.SURVIVAL),
		CREATIVE("creative", GameMode.CREATIVE),
		DEBUG("spectator", GameMode.SPECTATOR);

		private final String translationSuffix;
		private final GameMode defaultGameMode;

		private Mode(String translationSuffix, GameMode defaultGameMode) {
			this.translationSuffix = translationSuffix;
			this.defaultGameMode = defaultGameMode;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_5376 extends RuntimeException {
		public class_5376(Throwable throwable) {
			super(throwable);
		}
	}
}
