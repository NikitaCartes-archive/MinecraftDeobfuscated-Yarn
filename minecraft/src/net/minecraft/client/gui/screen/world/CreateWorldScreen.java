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
	private TextFieldWidget textFieldLevelName;
	private TextFieldWidget textFieldSeed;
	private String saveFolderName;
	private CreateWorldScreen.class_4539 field_3201 = CreateWorldScreen.class_4539.SURVIVAL;
	@Nullable
	private CreateWorldScreen.class_4539 field_3185;
	private boolean structures = true;
	private boolean commandsAllowed;
	private boolean field_3179;
	private boolean enableBonusItems;
	private boolean field_3178;
	private boolean creatingLevel;
	private boolean moreOptionsOpen;
	private ButtonWidget buttonCreateLevel;
	private ButtonWidget buttonGameModeSwitch;
	private ButtonWidget buttonMoreOptions;
	private ButtonWidget buttonGenerateStructures;
	private ButtonWidget buttonGenerateBonusItems;
	private ButtonWidget buttonMapTypeSwitch;
	private ButtonWidget buttonCommandsAllowed;
	private ButtonWidget buttonCustomizeType;
	private String gameModeDescriptionLine1;
	private String gameModeDescriptionLine2;
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
		this.textFieldLevelName.tick();
		this.textFieldSeed.tick();
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.textFieldLevelName = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterName"));
		this.textFieldLevelName.setText(this.levelName);
		this.textFieldLevelName.setChangedListener(string -> {
			this.levelName = string;
			this.buttonCreateLevel.active = !this.textFieldLevelName.getText().isEmpty();
			this.updateSaveFolderName();
		});
		this.children.add(this.textFieldLevelName);
		this.buttonGameModeSwitch = this.addButton(new ButtonWidget(this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode"), buttonWidget -> {
			switch (this.field_3201) {
				case SURVIVAL:
					this.method_22365(CreateWorldScreen.class_4539.HARDCORE);
					break;
				case HARDCORE:
					this.method_22365(CreateWorldScreen.class_4539.CREATIVE);
					break;
				case CREATIVE:
					this.method_22365(CreateWorldScreen.class_4539.SURVIVAL);
			}

			this.updateSettingsLabels();
		}));
		this.textFieldSeed = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterSeed"));
		this.textFieldSeed.setText(this.seed);
		this.textFieldSeed.setChangedListener(string -> this.seed = this.textFieldSeed.getText());
		this.children.add(this.textFieldSeed);
		this.buttonGenerateStructures = this.addButton(
			new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures"), buttonWidget -> {
				this.structures = !this.structures;
				this.updateSettingsLabels();
			})
		);
		this.buttonGenerateStructures.visible = false;
		this.buttonMapTypeSwitch = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType"), buttonWidget -> {
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
			this.updateSettingsLabels();
			this.setMoreOptionsOpen(this.moreOptionsOpen);
		}));
		this.buttonMapTypeSwitch.visible = false;
		this.buttonCustomizeType = this.addButton(new ButtonWidget(this.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType"), buttonWidget -> {
			if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.FLAT) {
				this.minecraft.openScreen(new CustomizeFlatLevelScreen(this, this.generatorOptionsTag));
			}

			if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.BUFFET) {
				this.minecraft.openScreen(new CustomizeBuffetLevelScreen(this, this.generatorOptionsTag));
			}
		}));
		this.buttonCustomizeType.visible = false;
		this.buttonCommandsAllowed = this.addButton(
			new ButtonWidget(this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands"), buttonWidget -> {
				this.field_3179 = true;
				this.commandsAllowed = !this.commandsAllowed;
				this.updateSettingsLabels();
			})
		);
		this.buttonCommandsAllowed.visible = false;
		this.buttonGenerateBonusItems = this.addButton(new ButtonWidget(this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems"), buttonWidget -> {
			this.enableBonusItems = !this.enableBonusItems;
			this.updateSettingsLabels();
		}));
		this.buttonGenerateBonusItems.visible = false;
		this.buttonMoreOptions = this.addButton(
			new ButtonWidget(this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions"), buttonWidget -> this.toggleMoreOptions())
		);
		this.buttonCreateLevel = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create"), buttonWidget -> this.createLevel())
		);
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.setMoreOptionsOpen(this.moreOptionsOpen);
		this.setInitialFocus(this.textFieldLevelName);
		this.method_22365(this.field_3201);
		this.updateSaveFolderName();
		this.updateSettingsLabels();
	}

	private void updateSaveFolderName() {
		this.saveFolderName = this.textFieldLevelName.getText().trim();
		if (this.saveFolderName.length() == 0) {
			this.saveFolderName = "World";
		}

		try {
			this.saveFolderName = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.saveFolderName, "");
		} catch (Exception var4) {
			this.saveFolderName = "World";

			try {
				this.saveFolderName = FileNameUtil.getNextUniqueName(this.minecraft.getLevelStorage().getSavesDirectory(), this.saveFolderName, "");
			} catch (Exception var3) {
				throw new RuntimeException("Could not create save folder", var3);
			}
		}
	}

	private void updateSettingsLabels() {
		this.buttonGameModeSwitch.setMessage(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.field_3201.field_20628));
		this.gameModeDescriptionLine1 = I18n.translate("selectWorld.gameMode." + this.field_3201.field_20628 + ".line1");
		this.gameModeDescriptionLine2 = I18n.translate("selectWorld.gameMode." + this.field_3201.field_20628 + ".line2");
		this.buttonGenerateStructures.setMessage(I18n.translate("selectWorld.mapFeatures") + ' ' + I18n.translate(this.structures ? "options.on" : "options.off"));
		this.buttonGenerateBonusItems
			.setMessage(I18n.translate("selectWorld.bonusItems") + ' ' + I18n.translate(this.enableBonusItems && !this.field_3178 ? "options.on" : "options.off"));
		this.buttonMapTypeSwitch
			.setMessage(I18n.translate("selectWorld.mapType") + ' ' + I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey()));
		this.buttonCommandsAllowed
			.setMessage(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate(this.commandsAllowed && !this.field_3178 ? "options.on" : "options.off"));
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
			String string = this.textFieldSeed.getText();
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

			LevelInfo levelInfo = new LevelInfo(l, this.field_3201.field_20629, this.structures, this.field_3178, LevelGeneratorType.TYPES[this.generatorType]);
			levelInfo.setGeneratorOptions(Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.generatorOptionsTag));
			if (this.enableBonusItems && !this.field_3178) {
				levelInfo.setBonusChest();
			}

			if (this.commandsAllowed && !this.field_3178) {
				levelInfo.enableCommands();
			}

			this.minecraft.startIntegratedServer(this.saveFolderName, this.textFieldLevelName.getText().trim(), levelInfo);
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

	private void method_22365(CreateWorldScreen.class_4539 arg) {
		if (!this.field_3179) {
			this.commandsAllowed = arg == CreateWorldScreen.class_4539.CREATIVE;
		}

		if (arg == CreateWorldScreen.class_4539.HARDCORE) {
			this.field_3178 = true;
			this.buttonCommandsAllowed.active = false;
			this.buttonGenerateBonusItems.active = false;
		} else {
			this.field_3178 = false;
			this.buttonCommandsAllowed.active = true;
			this.buttonGenerateBonusItems.active = true;
		}

		this.field_3201 = arg;
		this.updateSettingsLabels();
	}

	private void setMoreOptionsOpen(boolean bl) {
		this.moreOptionsOpen = bl;
		this.buttonGameModeSwitch.visible = !this.moreOptionsOpen;
		this.buttonMapTypeSwitch.visible = this.moreOptionsOpen;
		if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.buttonGameModeSwitch.active = false;
			if (this.field_3185 == null) {
				this.field_3185 = this.field_3201;
			}

			this.method_22365(CreateWorldScreen.class_4539.DEBUG);
			this.buttonGenerateStructures.visible = false;
			this.buttonGenerateBonusItems.visible = false;
			this.buttonCommandsAllowed.visible = false;
			this.buttonCustomizeType.visible = false;
		} else {
			this.buttonGameModeSwitch.active = true;
			if (this.field_3185 != null) {
				this.method_22365(this.field_3185);
			}

			this.buttonGenerateStructures.visible = this.moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType] != LevelGeneratorType.CUSTOMIZED;
			this.buttonGenerateBonusItems.visible = this.moreOptionsOpen;
			this.buttonCommandsAllowed.visible = this.moreOptionsOpen;
			this.buttonCustomizeType.visible = this.moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType].isCustomizable();
		}

		this.textFieldSeed.setVisible(this.moreOptionsOpen);
		this.textFieldLevelName.setVisible(!this.moreOptionsOpen);
		if (this.moreOptionsOpen) {
			this.buttonMoreOptions.setMessage(I18n.translate("gui.done"));
		} else {
			this.buttonMoreOptions.setMessage(I18n.translate("selectWorld.moreWorldOptions"));
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
			if (this.buttonGenerateStructures.visible) {
				this.drawString(this.font, I18n.translate("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.buttonCommandsAllowed.visible) {
				this.drawString(this.font, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.textFieldSeed.render(i, j, f);
			if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
				this.font
					.drawStringBounded(
						I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getInfoTranslationKey()),
						this.buttonMapTypeSwitch.x + 2,
						this.buttonMapTypeSwitch.y + 22,
						this.buttonMapTypeSwitch.getWidth(),
						10526880
					);
			}
		} else {
			this.drawString(this.font, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.font, I18n.translate("selectWorld.resultFolder") + " " + this.saveFolderName, this.width / 2 - 100, 85, -6250336);
			this.textFieldLevelName.render(i, j, f);
			this.drawCenteredString(this.font, this.gameModeDescriptionLine1, this.width / 2, 137, -6250336);
			this.drawCenteredString(this.font, this.gameModeDescriptionLine2, this.width / 2, 149, -6250336);
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
		this.commandsAllowed = levelProperties.areCommandsAllowed();
		if (levelProperties.isHardcore()) {
			this.field_3201 = CreateWorldScreen.class_4539.HARDCORE;
		} else if (levelProperties.getGameMode().isSurvivalLike()) {
			this.field_3201 = CreateWorldScreen.class_4539.SURVIVAL;
		} else if (levelProperties.getGameMode().isCreative()) {
			this.field_3201 = CreateWorldScreen.class_4539.CREATIVE;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum class_4539 {
		SURVIVAL("survival", GameMode.SURVIVAL),
		HARDCORE("hardcore", GameMode.SURVIVAL),
		CREATIVE("creative", GameMode.CREATIVE),
		DEBUG("spectator", GameMode.SPECTATOR);

		private final String field_20628;
		private final GameMode field_20629;

		private class_4539(String string2, GameMode gameMode) {
			this.field_20628 = string2;
			this.field_20629 = gameMode;
		}
	}
}
