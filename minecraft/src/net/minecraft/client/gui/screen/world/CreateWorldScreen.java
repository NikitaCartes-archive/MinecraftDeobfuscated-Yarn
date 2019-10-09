package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class CreateWorldScreen extends Screen {
	private final Screen parent;
	private TextFieldWidget levelNameField;
	private TextFieldWidget seedField;
	private String saveDirectoryName;
	private CreateWorldScreen.Mode currentMode = CreateWorldScreen.Mode.SURVIVAL;
	@Nullable
	private CreateWorldScreen.Mode lastMode;
	private boolean structures = true;
	private boolean cheatsEnabled;
	private boolean tweakedCheats;
	private boolean bonusChest;
	private boolean hardcore;
	private boolean creatingLevel;
	private boolean moreOptionsOpen;
	private ButtonWidget createLevelButton;
	private ButtonWidget gameModeSwitchButton;
	private ButtonWidget moreOptionsButton;
	private ButtonWidget generateStructuresButton;
	private ButtonWidget generateBonusChestButton;
	private ButtonWidget mapTypeSwitchButton;
	private ButtonWidget enableCheatsButton;
	private ButtonWidget customizeTypeButton;
	private String firstGameModeDescriptionLine;
	private String secondGameModeDescriptionLine;
	private String seed;
	private String levelName;
	private int generatorType;
	public CompoundTag generatorOptionsTag = new CompoundTag();

	public CreateWorldScreen(Screen screen) {
		super(new TranslatableText("selectWorld.create"));
		this.parent = screen;
		this.seed = "";
		this.levelName = I18n.translate("selectWorld.newWorld");
	}

	@Override
	public void tick() {
		this.levelNameField.tick();
		this.seedField.tick();
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.levelNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterName")) {
			@Override
			protected String getNarrationMessage() {
				return super.getNarrationMessage() + ". " + I18n.translate("selectWorld.resultFolder") + " " + CreateWorldScreen.this.saveDirectoryName;
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
			new ButtonWidget(this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode"), buttonWidget -> {
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
				public String getMessage() {
					return I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + CreateWorldScreen.this.currentMode.translationSuffix);
				}

				@Override
				protected String getNarrationMessage() {
					return super.getNarrationMessage()
						+ ". "
						+ CreateWorldScreen.this.firstGameModeDescriptionLine
						+ " "
						+ CreateWorldScreen.this.secondGameModeDescriptionLine;
				}
			}
		);
		this.seedField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterSeed"));
		this.seedField.setText(this.seed);
		this.seedField.setChangedListener(string -> this.seed = this.seedField.getText());
		this.children.add(this.seedField);
		this.generateStructuresButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures"), buttonWidget -> {
				this.structures = !this.structures;
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public String getMessage() {
					return I18n.translate("selectWorld.mapFeatures") + ' ' + I18n.translate(CreateWorldScreen.this.structures ? "options.on" : "options.off");
				}

				@Override
				protected String getNarrationMessage() {
					return super.getNarrationMessage() + ". " + I18n.translate("selectWorld.mapFeatures.info");
				}
			}
		);
		this.generateStructuresButton.visible = false;
		this.mapTypeSwitchButton = this.addButton(
			new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType"), buttonWidget -> {
				this.generatorType++;
				if (this.generatorType >= LevelGeneratorType.TYPES.length) {
					this.generatorType = 0;
				}

				while (!this.isGeneratorTypeValid()) {
					this.generatorType++;
					if (this.generatorType >= LevelGeneratorType.TYPES.length) {
						this.generatorType = 0;
					}
				}

				this.generatorOptionsTag = new CompoundTag();
				this.setMoreOptionsOpen(this.moreOptionsOpen);
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public String getMessage() {
					return I18n.translate("selectWorld.mapType") + ' ' + I18n.translate(LevelGeneratorType.TYPES[CreateWorldScreen.this.generatorType].getTranslationKey());
				}

				@Override
				protected String getNarrationMessage() {
					LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[CreateWorldScreen.this.generatorType];
					return levelGeneratorType.hasInfo()
						? super.getNarrationMessage() + ". " + I18n.translate(levelGeneratorType.getInfoTranslationKey())
						: super.getNarrationMessage();
				}
			}
		);
		this.mapTypeSwitchButton.visible = false;
		this.customizeTypeButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType"), buttonWidget -> {
			if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.FLAT) {
				this.minecraft.openScreen(new CustomizeFlatLevelScreen(this, this.generatorOptionsTag));
			}

			if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.BUFFET) {
				this.minecraft.openScreen(new CustomizeBuffetLevelScreen(this, this.generatorOptionsTag));
			}
		}));
		this.customizeTypeButton.visible = false;
		this.enableCheatsButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands"), buttonWidget -> {
				this.tweakedCheats = true;
				this.cheatsEnabled = !this.cheatsEnabled;
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public String getMessage() {
					return I18n.translate("selectWorld.allowCommands")
						+ ' '
						+ I18n.translate(CreateWorldScreen.this.cheatsEnabled && !CreateWorldScreen.this.hardcore ? "options.on" : "options.off");
				}

				@Override
				protected String getNarrationMessage() {
					return super.getNarrationMessage() + ". " + I18n.translate("selectWorld.allowCommands.info");
				}
			}
		);
		this.enableCheatsButton.visible = false;
		this.generateBonusChestButton = this.addButton(
			new ButtonWidget(this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems"), buttonWidget -> {
				this.bonusChest = !this.bonusChest;
				buttonWidget.queueNarration(250);
			}) {
				@Override
				public String getMessage() {
					return I18n.translate("selectWorld.bonusItems")
						+ ' '
						+ I18n.translate(CreateWorldScreen.this.bonusChest && !CreateWorldScreen.this.hardcore ? "options.on" : "options.off");
				}
			}
		);
		this.generateBonusChestButton.visible = false;
		this.moreOptionsButton = this.addButton(
			new ButtonWidget(this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions"), buttonWidget -> this.toggleMoreOptions())
		);
		this.createLevelButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create"), buttonWidget -> this.createLevel())
		);
		this.createLevelButton.active = !this.levelName.isEmpty();
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.setMoreOptionsOpen(this.moreOptionsOpen);
		this.setInitialFocus(this.levelNameField);
		this.tweakDefaultsTo(this.currentMode);
		this.updateSaveFolderName();
	}

	private void updateSettingsLabels() {
		this.firstGameModeDescriptionLine = I18n.translate("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line1");
		this.secondGameModeDescriptionLine = I18n.translate("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line2");
	}

	private void updateSaveFolderName() {
		this.saveDirectoryName = this.levelNameField.getText().trim();
		if (this.saveDirectoryName.isEmpty()) {
			this.saveDirectoryName = "World";
		}

		try {
			this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
		} catch (Exception var4) {
			this.saveDirectoryName = "World";

			try {
				this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
			} catch (Exception var3) {
				throw new RuntimeException("Could not create save folder", var3);
			}
		}
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	private void createLevel() {
		this.minecraft.openScreen(null);
		if (!this.creatingLevel) {
			this.creatingLevel = true;
			long l = new Random().nextLong();
			String string = this.seedField.getText();
			if (!StringUtils.isEmpty(string)) {
				try {
					long m = Long.parseLong(string);
					if (m != 0L) {
						l = m;
					}
				} catch (NumberFormatException var6) {
					l = (long)string.hashCode();
				}
			}

			LevelInfo levelInfo = new LevelInfo(l, this.currentMode.defaultGameMode, this.structures, this.hardcore, LevelGeneratorType.TYPES[this.generatorType]);
			levelInfo.setGeneratorOptions(Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.generatorOptionsTag));
			if (this.bonusChest && !this.hardcore) {
				levelInfo.setBonusChest();
			}

			if (this.cheatsEnabled && !this.hardcore) {
				levelInfo.enableCommands();
			}

			this.minecraft.startIntegratedServer(this.saveDirectoryName, this.levelNameField.getText().trim(), levelInfo);
		}
	}

	private boolean isGeneratorTypeValid() {
		LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[this.generatorType];
		if (levelGeneratorType == null || !levelGeneratorType.isVisible()) {
			return false;
		} else {
			return levelGeneratorType == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES ? hasShiftDown() : true;
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
			this.generateBonusChestButton.active = false;
		} else {
			this.hardcore = false;
			this.enableCheatsButton.active = true;
			this.generateBonusChestButton.active = true;
		}

		this.currentMode = mode;
		this.updateSettingsLabels();
	}

	private void setMoreOptionsOpen(boolean bl) {
		this.moreOptionsOpen = bl;
		this.gameModeSwitchButton.visible = !this.moreOptionsOpen;
		this.mapTypeSwitchButton.visible = this.moreOptionsOpen;
		if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.gameModeSwitchButton.active = false;
			if (this.lastMode == null) {
				this.lastMode = this.currentMode;
			}

			this.tweakDefaultsTo(CreateWorldScreen.Mode.DEBUG);
			this.generateStructuresButton.visible = false;
			this.generateBonusChestButton.visible = false;
			this.enableCheatsButton.visible = false;
			this.customizeTypeButton.visible = false;
		} else {
			this.gameModeSwitchButton.active = true;
			if (this.lastMode != null) {
				this.tweakDefaultsTo(this.lastMode);
			}

			this.generateStructuresButton.visible = this.moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType] != LevelGeneratorType.CUSTOMIZED;
			this.generateBonusChestButton.visible = this.moreOptionsOpen;
			this.enableCheatsButton.visible = this.moreOptionsOpen;
			this.customizeTypeButton.visible = this.moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType].isCustomizable();
		}

		this.seedField.setVisible(this.moreOptionsOpen);
		this.levelNameField.setVisible(!this.moreOptionsOpen);
		if (this.moreOptionsOpen) {
			this.moreOptionsButton.setMessage(I18n.translate("gui.done"));
		} else {
			this.moreOptionsButton.setMessage(I18n.translate("selectWorld.moreWorldOptions"));
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.createLevel();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, -1);
		if (this.moreOptionsOpen) {
			this.drawString(this.font, I18n.translate("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.font, I18n.translate("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
			if (this.generateStructuresButton.visible) {
				this.drawString(this.font, I18n.translate("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.enableCheatsButton.visible) {
				this.drawString(this.font, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.seedField.render(i, j, f);
			if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
				this.font
					.drawStringBounded(
						I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getInfoTranslationKey()),
						this.mapTypeSwitchButton.x + 2,
						this.mapTypeSwitchButton.y + 22,
						this.mapTypeSwitchButton.getWidth(),
						10526880
					);
			}
		} else {
			this.drawString(this.font, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.font, I18n.translate("selectWorld.resultFolder") + " " + this.saveDirectoryName, this.width / 2 - 100, 85, -6250336);
			this.levelNameField.render(i, j, f);
			this.drawCenteredString(this.font, this.firstGameModeDescriptionLine, this.width / 2, 137, -6250336);
			this.drawCenteredString(this.font, this.secondGameModeDescriptionLine, this.width / 2, 149, -6250336);
		}

		super.render(i, j, f);
	}

	public void recreateLevel(LevelProperties levelProperties) {
		this.levelName = levelProperties.getLevelName();
		this.seed = Long.toString(levelProperties.getSeed());
		LevelGeneratorType levelGeneratorType = levelProperties.getGeneratorType() == LevelGeneratorType.CUSTOMIZED
			? LevelGeneratorType.DEFAULT
			: levelProperties.getGeneratorType();
		this.generatorType = levelGeneratorType.getId();
		this.generatorOptionsTag = levelProperties.getGeneratorOptions();
		this.structures = levelProperties.hasStructures();
		this.cheatsEnabled = levelProperties.areCommandsAllowed();
		if (levelProperties.isHardcore()) {
			this.currentMode = CreateWorldScreen.Mode.HARDCORE;
		} else if (levelProperties.getGameMode().isSurvivalLike()) {
			this.currentMode = CreateWorldScreen.Mode.SURVIVAL;
		} else if (levelProperties.getGameMode().isCreative()) {
			this.currentMode = CreateWorldScreen.Mode.CREATIVE;
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

		private Mode(String string2, GameMode gameMode) {
			this.translationSuffix = string2;
			this.defaultGameMode = gameMode;
		}
	}
}
