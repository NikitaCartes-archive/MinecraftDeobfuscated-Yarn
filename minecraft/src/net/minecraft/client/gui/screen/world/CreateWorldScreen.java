package net.minecraft.client.gui.screen.world;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5219;
import net.minecraft.class_5285;
import net.minecraft.class_5292;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;

@Environment(EnvType.CLIENT)
public class CreateWorldScreen extends Screen {
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
	private boolean moreOptionsOpen;
	private ButtonWidget createLevelButton;
	private ButtonWidget gameModeSwitchButton;
	private ButtonWidget field_24286;
	private ButtonWidget moreOptionsButton;
	private ButtonWidget gameRulesButton;
	private ButtonWidget enableCheatsButton;
	private Text firstGameModeDescriptionLine;
	private Text secondGameModeDescriptionLine;
	private String levelName;
	private GameRules gameRules = new GameRules();
	public final class_5292 field_24588;

	public CreateWorldScreen(@Nullable Screen screen, class_5219 arg) {
		this(screen, new class_5292(arg.getLevelInfo().getGeneratorOptions()));
		LevelInfo levelInfo = arg.getLevelInfo();
		this.levelName = levelInfo.getLevelName();
		this.cheatsEnabled = levelInfo.isHardcore();
		this.tweakedCheats = true;
		this.field_24289 = levelInfo.getDifficulty();
		this.field_24290 = this.field_24289;
		this.gameRules.setAllValues(arg.getGameRules(), null);
		if (levelInfo.hasStructures()) {
			this.currentMode = CreateWorldScreen.Mode.HARDCORE;
		} else if (levelInfo.getGameMode().isSurvivalLike()) {
			this.currentMode = CreateWorldScreen.Mode.SURVIVAL;
		} else if (levelInfo.getGameMode().isCreative()) {
			this.currentMode = CreateWorldScreen.Mode.CREATIVE;
		}
	}

	public CreateWorldScreen(@Nullable Screen parent) {
		this(parent, new class_5292());
	}

	private CreateWorldScreen(@Nullable Screen screen, class_5292 arg) {
		super(new TranslatableText("selectWorld.create"));
		this.parent = screen;
		this.levelName = I18n.translate("selectWorld.newWorld");
		this.field_24588 = arg;
	}

	@Override
	public void tick() {
		this.levelNameField.tick();
		this.field_24588.tick();
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
		this.gameModeSwitchButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, 115, 150, 20, new TranslatableText("selectWorld.gameMode"), buttonWidget -> {
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
		this.field_24286 = this.addButton(new ButtonWidget(this.width / 2 + 5, 115, 150, 20, new TranslatableText("options.difficulty"), buttonWidget -> {
			this.field_24289 = this.field_24289.cycle();
			this.field_24290 = this.field_24289;
			buttonWidget.queueNarration(250);
		}) {
			@Override
			public Text getMessage() {
				return new TranslatableText("options.difficulty").append(": ").append(CreateWorldScreen.this.field_24290.getTranslatableName());
			}
		});
		this.field_24588.method_28092(this, this.client, this.textRenderer);
		this.enableCheatsButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), buttonWidget -> {
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
		this.enableCheatsButton.visible = false;
		this.createLevelButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), buttonWidget -> this.createLevel())
		);
		this.createLevelButton.active = !this.levelName.isEmpty();
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
		this.moreOptionsButton = this.addButton(
			new ButtonWidget(this.width / 2 + 5, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), buttonWidget -> this.toggleMoreOptions())
		);
		this.gameRulesButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
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
		this.method_28084();
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
			class_5285 lv = this.field_24588.method_28096(this.hardcore);
			LevelInfo levelInfo;
			if (lv.method_28033()) {
				GameRules gameRules = new GameRules();
				gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
				levelInfo = new LevelInfo(this.levelNameField.getText().trim(), GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, lv);
			} else {
				levelInfo = new LevelInfo(
					this.levelNameField.getText().trim(),
					this.currentMode.defaultGameMode,
					this.hardcore,
					this.field_24290,
					this.cheatsEnabled && !this.hardcore,
					this.gameRules,
					lv
				);
			}

			this.client.startIntegratedServer(this.saveDirectoryName, levelInfo);
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
			this.field_24588.field_24589.active = false;
			this.field_24290 = Difficulty.HARD;
			this.field_24286.active = false;
		} else {
			this.hardcore = false;
			this.enableCheatsButton.active = true;
			this.field_24588.field_24589.active = true;
			this.field_24290 = this.field_24289;
			this.field_24286.active = true;
		}

		this.currentMode = mode;
		this.updateSettingsLabels();
	}

	public void method_28084() {
		this.setMoreOptionsOpen(this.moreOptionsOpen);
	}

	private void setMoreOptionsOpen(boolean moreOptionsOpen) {
		this.moreOptionsOpen = moreOptionsOpen;
		this.gameModeSwitchButton.visible = !this.moreOptionsOpen;
		this.field_24286.visible = !this.moreOptionsOpen;
		if (this.field_24588.method_28085()) {
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

			this.enableCheatsButton.visible = this.moreOptionsOpen;
		}

		this.field_24588.method_28101(this.moreOptionsOpen);
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
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, -1);
		if (this.moreOptionsOpen) {
			this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
			if (this.enableCheatsButton.visible) {
				this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.field_24588.render(matrices, mouseX, mouseY, delta);
		} else {
			this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawStringWithShadow(
				matrices, this.textRenderer, I18n.translate("selectWorld.resultFolder") + " " + this.saveDirectoryName, this.width / 2 - 100, 85, -6250336
			);
			this.levelNameField.render(matrices, mouseX, mouseY, delta);
			this.drawCenteredText(matrices, this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2 - 155 + 75, 137, -6250336);
			this.drawCenteredText(matrices, this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2 - 155 + 75, 149, -6250336);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public <T extends Element> T addChild(T child) {
		return super.addChild(child);
	}

	@Override
	public <T extends AbstractButtonWidget> T addButton(T button) {
		return super.addButton(button);
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
}
