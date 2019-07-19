package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixer.NbtOps;
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
	private String field_3196;
	private String gameMode = "survival";
	private String field_3185;
	private boolean structures = true;
	private boolean cheatsEnabled;
	private boolean field_3179;
	private boolean bonusChest;
	private boolean field_3178;
	private boolean creatingLevel;
	private boolean field_3202;
	private ButtonWidget createLevelButton;
	private ButtonWidget gameModeSwitchButton;
	private ButtonWidget moreOptionsButton;
	private ButtonWidget generateStructuresButton;
	private ButtonWidget generateBonusChestButton;
	private ButtonWidget mapTypeSwitchButton;
	private ButtonWidget enableCheatsButton;
	private ButtonWidget customizeTypeButton;
	private String field_3194;
	private String field_3199;
	private String seed;
	private String levelName;
	private int generatorType;
	public CompoundTag generatorOptionsTag = new CompoundTag();

	public CreateWorldScreen(Screen parent) {
		super(new TranslatableText("selectWorld.create"));
		this.parent = parent;
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
		this.levelNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterName"));
		this.levelNameField.setText(this.levelName);
		this.levelNameField.setChangedListener(string -> {
			this.levelName = string;
			this.createLevelButton.active = !this.levelNameField.getText().isEmpty();
			this.method_2727();
		});
		this.children.add(this.levelNameField);
		this.gameModeSwitchButton = this.addButton(new ButtonWidget(this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode"), buttonWidget -> {
			if ("survival".equals(this.gameMode)) {
				if (!this.field_3179) {
					this.cheatsEnabled = false;
				}

				this.field_3178 = false;
				this.gameMode = "hardcore";
				this.field_3178 = true;
				this.enableCheatsButton.active = false;
				this.generateBonusChestButton.active = false;
				this.method_2722();
			} else if ("hardcore".equals(this.gameMode)) {
				if (!this.field_3179) {
					this.cheatsEnabled = true;
				}

				this.field_3178 = false;
				this.gameMode = "creative";
				this.method_2722();
				this.field_3178 = false;
				this.enableCheatsButton.active = true;
				this.generateBonusChestButton.active = true;
			} else {
				if (!this.field_3179) {
					this.cheatsEnabled = false;
				}

				this.gameMode = "survival";
				this.method_2722();
				this.enableCheatsButton.active = true;
				this.generateBonusChestButton.active = true;
				this.field_3178 = false;
			}

			this.method_2722();
		}));
		this.seedField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterSeed"));
		this.seedField.setText(this.seed);
		this.seedField.setChangedListener(string -> this.seed = this.seedField.getText());
		this.children.add(this.seedField);
		this.generateStructuresButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures"), buttonWidget -> {
				this.structures = !this.structures;
				this.method_2722();
			})
		);
		this.generateStructuresButton.visible = false;
		this.mapTypeSwitchButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType"), buttonWidget -> {
			this.generatorType++;
			if (this.generatorType >= LevelGeneratorType.TYPES.length) {
				this.generatorType = 0;
			}

			while (!this.method_2723()) {
				this.generatorType++;
				if (this.generatorType >= LevelGeneratorType.TYPES.length) {
					this.generatorType = 0;
				}
			}

			this.generatorOptionsTag = new CompoundTag();
			this.method_2722();
			this.method_2710(this.field_3202);
		}));
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
		this.enableCheatsButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands"), buttonWidget -> {
			this.field_3179 = true;
			this.cheatsEnabled = !this.cheatsEnabled;
			this.method_2722();
		}));
		this.enableCheatsButton.visible = false;
		this.generateBonusChestButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems"), buttonWidget -> {
			this.bonusChest = !this.bonusChest;
			this.method_2722();
		}));
		this.generateBonusChestButton.visible = false;
		this.moreOptionsButton = this.addButton(
			new ButtonWidget(this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions"), buttonWidget -> this.method_2721())
		);
		this.createLevelButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create"), buttonWidget -> this.createLevel())
		);
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.method_2710(this.field_3202);
		this.setInitialFocus(this.levelNameField);
		this.method_2727();
		this.method_2722();
	}

	private void method_2727() {
		this.field_3196 = this.levelNameField.getText().trim();
		if (this.field_3196.length() == 0) {
			this.field_3196 = "World";
		}

		try {
			this.field_3196 = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.field_3196, "");
		} catch (Exception var4) {
			this.field_3196 = "World";

			try {
				this.field_3196 = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.field_3196, "");
			} catch (Exception var3) {
				throw new RuntimeException("Could not create save folder", var3);
			}
		}
	}

	private void method_2722() {
		this.gameModeSwitchButton.setMessage(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode));
		this.field_3194 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line1");
		this.field_3199 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line2");
		this.generateStructuresButton.setMessage(I18n.translate("selectWorld.mapFeatures") + ' ' + I18n.translate(this.structures ? "options.on" : "options.off"));
		this.generateBonusChestButton
			.setMessage(I18n.translate("selectWorld.bonusItems") + ' ' + I18n.translate(this.bonusChest && !this.field_3178 ? "options.on" : "options.off"));
		this.mapTypeSwitchButton
			.setMessage(I18n.translate("selectWorld.mapType") + ' ' + I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey()));
		this.enableCheatsButton
			.setMessage(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate(this.cheatsEnabled && !this.field_3178 ? "options.on" : "options.off"));
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

			LevelInfo levelInfo = new LevelInfo(l, GameMode.byName(this.gameMode), this.structures, this.field_3178, LevelGeneratorType.TYPES[this.generatorType]);
			levelInfo.setGeneratorOptions(Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.generatorOptionsTag));
			if (this.bonusChest && !this.field_3178) {
				levelInfo.setBonusChest();
			}

			if (this.cheatsEnabled && !this.field_3178) {
				levelInfo.enableCommands();
			}

			this.minecraft.startIntegratedServer(this.field_3196, this.levelNameField.getText().trim(), levelInfo);
		}
	}

	private boolean method_2723() {
		LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[this.generatorType];
		if (levelGeneratorType == null || !levelGeneratorType.isVisible()) {
			return false;
		} else {
			return levelGeneratorType == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES ? hasShiftDown() : true;
		}
	}

	private void method_2721() {
		this.method_2710(!this.field_3202);
	}

	private void method_2710(boolean bl) {
		this.field_3202 = bl;
		if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.gameModeSwitchButton.visible = !this.field_3202;
			this.gameModeSwitchButton.active = false;
			if (this.field_3185 == null) {
				this.field_3185 = this.gameMode;
			}

			this.gameMode = "spectator";
			this.generateStructuresButton.visible = false;
			this.generateBonusChestButton.visible = false;
			this.mapTypeSwitchButton.visible = this.field_3202;
			this.enableCheatsButton.visible = false;
			this.customizeTypeButton.visible = false;
		} else {
			this.gameModeSwitchButton.visible = !this.field_3202;
			this.gameModeSwitchButton.active = true;
			if (this.field_3185 != null) {
				this.gameMode = this.field_3185;
				this.field_3185 = null;
			}

			this.generateStructuresButton.visible = this.field_3202 && LevelGeneratorType.TYPES[this.generatorType] != LevelGeneratorType.CUSTOMIZED;
			this.generateBonusChestButton.visible = this.field_3202;
			this.mapTypeSwitchButton.visible = this.field_3202;
			this.enableCheatsButton.visible = this.field_3202;
			this.customizeTypeButton.visible = this.field_3202 && LevelGeneratorType.TYPES[this.generatorType].isCustomizable();
		}

		this.method_2722();
		this.seedField.setVisible(this.field_3202);
		this.levelNameField.setVisible(!this.field_3202);
		if (this.field_3202) {
			this.moreOptionsButton.setMessage(I18n.translate("gui.done"));
		} else {
			this.moreOptionsButton.setMessage(I18n.translate("selectWorld.moreWorldOptions"));
		}
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
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, -1);
		if (this.field_3202) {
			this.drawString(this.font, I18n.translate("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.font, I18n.translate("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
			if (this.generateStructuresButton.visible) {
				this.drawString(this.font, I18n.translate("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.enableCheatsButton.visible) {
				this.drawString(this.font, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.seedField.render(mouseX, mouseY, delta);
			if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
				this.font
					.drawTrimmed(
						I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getInfoTranslationKey()),
						this.mapTypeSwitchButton.x + 2,
						this.mapTypeSwitchButton.y + 22,
						this.mapTypeSwitchButton.getWidth(),
						10526880
					);
			}
		} else {
			this.drawString(this.font, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.font, I18n.translate("selectWorld.resultFolder") + " " + this.field_3196, this.width / 2 - 100, 85, -6250336);
			this.levelNameField.render(mouseX, mouseY, delta);
			this.drawCenteredString(this.font, this.field_3194, this.width / 2, 137, -6250336);
			this.drawCenteredString(this.font, this.field_3199, this.width / 2, 149, -6250336);
		}

		super.render(mouseX, mouseY, delta);
	}

	public void recreateLevel(LevelProperties levelProperties) {
		this.levelName = levelProperties.getLevelName();
		this.seed = levelProperties.getSeed() + "";
		LevelGeneratorType levelGeneratorType = levelProperties.getGeneratorType() == LevelGeneratorType.CUSTOMIZED
			? LevelGeneratorType.DEFAULT
			: levelProperties.getGeneratorType();
		this.generatorType = levelGeneratorType.getId();
		this.generatorOptionsTag = levelProperties.getGeneratorOptions();
		this.structures = levelProperties.hasStructures();
		this.cheatsEnabled = levelProperties.areCommandsAllowed();
		if (levelProperties.isHardcore()) {
			this.gameMode = "hardcore";
		} else if (levelProperties.getGameMode().isSurvivalLike()) {
			this.gameMode = "survival";
		} else if (levelProperties.getGameMode().isCreative()) {
			this.gameMode = "creative";
		}
	}
}
