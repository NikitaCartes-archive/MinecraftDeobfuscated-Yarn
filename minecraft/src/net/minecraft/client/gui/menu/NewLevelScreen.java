package net.minecraft.client.gui.menu;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.WorldNameProvider;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class NewLevelScreen extends Screen {
	private final Screen parent;
	private TextFieldWidget textFieldLevelName;
	private TextFieldWidget textFieldSeed;
	private String field_3196;
	private String gameMode = "survival";
	private String field_3185;
	private boolean structures = true;
	private boolean commandsAllowed;
	private boolean field_3179;
	private boolean enableBonusItems;
	private boolean field_3178;
	private boolean isCreatingLevel;
	private boolean field_3202;
	private ButtonWidget buttonCreateLevel;
	private ButtonWidget buttonGameModeSwitch;
	private ButtonWidget buttonMoreOptions;
	private ButtonWidget buttonGenerateStructures;
	private ButtonWidget buttonGenerateBonusItems;
	private ButtonWidget buttonMapTypeSwitch;
	private ButtonWidget buttonCommandsAllowed;
	private ButtonWidget buttonCustomizeType;
	private String field_3194;
	private String field_3199;
	private String seed;
	private String levelName;
	private int generatorType;
	public CompoundTag field_3200 = new CompoundTag();

	public NewLevelScreen(Screen screen) {
		this.parent = screen;
		this.seed = "";
		this.levelName = I18n.translate("selectWorld.newWorld");
	}

	@Override
	public void update() {
		this.textFieldLevelName.tick();
		this.textFieldSeed.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.textFieldLevelName = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 60, 200, 20);
		this.textFieldLevelName.setText(this.levelName);
		this.textFieldLevelName.setChangedListener(string -> {
			this.levelName = string;
			this.buttonCreateLevel.active = !this.textFieldLevelName.getText().isEmpty();
			this.method_2727();
		});
		this.listeners.add(this.textFieldLevelName);
		this.buttonGameModeSwitch = this.addButton(new ButtonWidget(this.screenWidth / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode")) {
			@Override
			public void onPressed() {
				if ("survival".equals(NewLevelScreen.this.gameMode)) {
					if (!NewLevelScreen.this.field_3179) {
						NewLevelScreen.this.commandsAllowed = false;
					}

					NewLevelScreen.this.field_3178 = false;
					NewLevelScreen.this.gameMode = "hardcore";
					NewLevelScreen.this.field_3178 = true;
					NewLevelScreen.this.buttonCommandsAllowed.active = false;
					NewLevelScreen.this.buttonGenerateBonusItems.active = false;
					NewLevelScreen.this.method_2722();
				} else if ("hardcore".equals(NewLevelScreen.this.gameMode)) {
					if (!NewLevelScreen.this.field_3179) {
						NewLevelScreen.this.commandsAllowed = true;
					}

					NewLevelScreen.this.field_3178 = false;
					NewLevelScreen.this.gameMode = "creative";
					NewLevelScreen.this.method_2722();
					NewLevelScreen.this.field_3178 = false;
					NewLevelScreen.this.buttonCommandsAllowed.active = true;
					NewLevelScreen.this.buttonGenerateBonusItems.active = true;
				} else {
					if (!NewLevelScreen.this.field_3179) {
						NewLevelScreen.this.commandsAllowed = false;
					}

					NewLevelScreen.this.gameMode = "survival";
					NewLevelScreen.this.method_2722();
					NewLevelScreen.this.buttonCommandsAllowed.active = true;
					NewLevelScreen.this.buttonGenerateBonusItems.active = true;
					NewLevelScreen.this.field_3178 = false;
				}

				NewLevelScreen.this.method_2722();
			}
		});
		this.textFieldSeed = new TextFieldWidget(this.fontRenderer, this.screenWidth / 2 - 100, 60, 200, 20);
		this.textFieldSeed.setText(this.seed);
		this.textFieldSeed.setChangedListener(string -> this.seed = this.textFieldSeed.getText());
		this.listeners.add(this.textFieldSeed);
		this.buttonGenerateStructures = this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures")) {
			@Override
			public void onPressed() {
				NewLevelScreen.this.structures = !NewLevelScreen.this.structures;
				NewLevelScreen.this.method_2722();
			}
		});
		this.buttonGenerateStructures.visible = false;
		this.buttonMapTypeSwitch = this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType")) {
			@Override
			public void onPressed() {
				NewLevelScreen.this.generatorType++;
				if (NewLevelScreen.this.generatorType >= LevelGeneratorType.TYPES.length) {
					NewLevelScreen.this.generatorType = 0;
				}

				while (!NewLevelScreen.this.method_2723()) {
					NewLevelScreen.this.generatorType++;
					if (NewLevelScreen.this.generatorType >= LevelGeneratorType.TYPES.length) {
						NewLevelScreen.this.generatorType = 0;
					}
				}

				NewLevelScreen.this.field_3200 = new CompoundTag();
				NewLevelScreen.this.method_2722();
				NewLevelScreen.this.method_2710(NewLevelScreen.this.field_3202);
			}
		});
		this.buttonMapTypeSwitch.visible = false;
		this.buttonCustomizeType = this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType")) {
			@Override
			public void onPressed() {
				if (LevelGeneratorType.TYPES[NewLevelScreen.this.generatorType] == LevelGeneratorType.FLAT) {
					NewLevelScreen.this.client.openScreen(new CustomizeFlatLevelScreen(NewLevelScreen.this, NewLevelScreen.this.field_3200));
				}

				if (LevelGeneratorType.TYPES[NewLevelScreen.this.generatorType] == LevelGeneratorType.BUFFET) {
					NewLevelScreen.this.client.openScreen(new CustomizeBuffetLevelScreen(NewLevelScreen.this, NewLevelScreen.this.field_3200));
				}
			}
		});
		this.buttonCustomizeType.visible = false;
		this.buttonCommandsAllowed = this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands")) {
			@Override
			public void onPressed() {
				NewLevelScreen.this.field_3179 = true;
				NewLevelScreen.this.commandsAllowed = !NewLevelScreen.this.commandsAllowed;
				NewLevelScreen.this.method_2722();
			}
		});
		this.buttonCommandsAllowed.visible = false;
		this.buttonGenerateBonusItems = this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems")) {
			@Override
			public void onPressed() {
				NewLevelScreen.this.enableBonusItems = !NewLevelScreen.this.enableBonusItems;
				NewLevelScreen.this.method_2722();
			}
		});
		this.buttonGenerateBonusItems.visible = false;
		this.buttonMoreOptions = this.addButton(new ButtonWidget(this.screenWidth / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions")) {
			@Override
			public void onPressed() {
				NewLevelScreen.this.method_2721();
			}
		});
		this.buttonCreateLevel = this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight - 28, 150, 20, I18n.translate("selectWorld.create")) {
			@Override
			public void onPressed() {
				NewLevelScreen.this.createLevel();
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed() {
				NewLevelScreen.this.client.openScreen(NewLevelScreen.this.parent);
			}
		});
		this.method_2710(this.field_3202);
		this.focusOn(this.textFieldLevelName);
		this.method_2727();
		this.method_2722();
	}

	private void method_2727() {
		this.field_3196 = this.textFieldLevelName.getText().trim();
		if (this.field_3196.length() == 0) {
			this.field_3196 = "World";
		}

		try {
			this.field_3196 = WorldNameProvider.transformWorldName(this.client.getLevelStorage().method_19636(), this.field_3196);
		} catch (Exception var4) {
			this.field_3196 = "World";

			try {
				this.field_3196 = WorldNameProvider.transformWorldName(this.client.getLevelStorage().method_19636(), this.field_3196);
			} catch (Exception var3) {
				throw new RuntimeException("Could not create save folder", var3);
			}
		}
	}

	private void method_2722() {
		this.buttonGameModeSwitch.setMessage(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode));
		this.field_3194 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line1");
		this.field_3199 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line2");
		this.buttonGenerateStructures.setMessage(I18n.translate("selectWorld.mapFeatures") + ' ' + I18n.translate(this.structures ? "options.on" : "options.off"));
		this.buttonGenerateBonusItems
			.setMessage(I18n.translate("selectWorld.bonusItems") + ' ' + I18n.translate(this.enableBonusItems && !this.field_3178 ? "options.on" : "options.off"));
		this.buttonMapTypeSwitch
			.setMessage(I18n.translate("selectWorld.mapType") + ' ' + I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey()));
		this.buttonCommandsAllowed
			.setMessage(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate(this.commandsAllowed && !this.field_3178 ? "options.on" : "options.off"));
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void createLevel() {
		this.client.openScreen(null);
		if (!this.isCreatingLevel) {
			this.isCreatingLevel = true;
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

			LevelInfo levelInfo = new LevelInfo(l, GameMode.byName(this.gameMode), this.structures, this.field_3178, LevelGeneratorType.TYPES[this.generatorType]);
			levelInfo.setGeneratorOptions(Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.field_3200));
			if (this.enableBonusItems && !this.field_3178) {
				levelInfo.setBonusChest();
			}

			if (this.commandsAllowed && !this.field_3178) {
				levelInfo.enableCommands();
			}

			this.client.startIntegratedServer(this.field_3196, this.textFieldLevelName.getText().trim(), levelInfo);
		}
	}

	private boolean method_2723() {
		LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[this.generatorType];
		if (levelGeneratorType == null || !levelGeneratorType.isVisible()) {
			return false;
		} else {
			return levelGeneratorType == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES ? isShiftPressed() : true;
		}
	}

	private void method_2721() {
		this.method_2710(!this.field_3202);
	}

	private void method_2710(boolean bl) {
		this.field_3202 = bl;
		if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.buttonGameModeSwitch.visible = !this.field_3202;
			this.buttonGameModeSwitch.active = false;
			if (this.field_3185 == null) {
				this.field_3185 = this.gameMode;
			}

			this.gameMode = "spectator";
			this.buttonGenerateStructures.visible = false;
			this.buttonGenerateBonusItems.visible = false;
			this.buttonMapTypeSwitch.visible = this.field_3202;
			this.buttonCommandsAllowed.visible = false;
			this.buttonCustomizeType.visible = false;
		} else {
			this.buttonGameModeSwitch.visible = !this.field_3202;
			this.buttonGameModeSwitch.active = true;
			if (this.field_3185 != null) {
				this.gameMode = this.field_3185;
				this.field_3185 = null;
			}

			this.buttonGenerateStructures.visible = this.field_3202 && LevelGeneratorType.TYPES[this.generatorType] != LevelGeneratorType.CUSTOMIZED;
			this.buttonGenerateBonusItems.visible = this.field_3202;
			this.buttonMapTypeSwitch.visible = this.field_3202;
			this.buttonCommandsAllowed.visible = this.field_3202;
			this.buttonCustomizeType.visible = this.field_3202 && LevelGeneratorType.TYPES[this.generatorType].isCustomizable();
		}

		this.method_2722();
		this.textFieldSeed.setVisible(this.field_3202);
		this.textFieldLevelName.setVisible(!this.field_3202);
		if (this.field_3202) {
			this.buttonMoreOptions.setMessage(I18n.translate("gui.done"));
		} else {
			this.buttonMoreOptions.setMessage(I18n.translate("selectWorld.moreWorldOptions"));
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("selectWorld.create"), this.screenWidth / 2, 20, -1);
		if (this.field_3202) {
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.enterSeed"), this.screenWidth / 2 - 100, 47, -6250336);
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.seedInfo"), this.screenWidth / 2 - 100, 85, -6250336);
			if (this.buttonGenerateStructures.visible) {
				this.drawString(this.fontRenderer, I18n.translate("selectWorld.mapFeatures.info"), this.screenWidth / 2 - 150, 122, -6250336);
			}

			if (this.buttonCommandsAllowed.visible) {
				this.drawString(this.fontRenderer, I18n.translate("selectWorld.allowCommands.info"), this.screenWidth / 2 - 150, 172, -6250336);
			}

			this.textFieldSeed.render(i, j, f);
			if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
				this.fontRenderer
					.drawStringBounded(
						I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getInfoTranslationKey()),
						this.buttonMapTypeSwitch.x + 2,
						this.buttonMapTypeSwitch.y + 22,
						this.buttonMapTypeSwitch.getWidth(),
						10526880
					);
			}
		} else {
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.enterName"), this.screenWidth / 2 - 100, 47, -6250336);
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.resultFolder") + " " + this.field_3196, this.screenWidth / 2 - 100, 85, -6250336);
			this.textFieldLevelName.render(i, j, f);
			this.drawStringCentered(this.fontRenderer, this.field_3194, this.screenWidth / 2, 137, -6250336);
			this.drawStringCentered(this.fontRenderer, this.field_3199, this.screenWidth / 2, 149, -6250336);
		}

		super.render(i, j, f);
	}

	public void recreateLevel(LevelProperties levelProperties) {
		this.levelName = levelProperties.getLevelName();
		this.seed = levelProperties.getSeed() + "";
		LevelGeneratorType levelGeneratorType = levelProperties.getGeneratorType() == LevelGeneratorType.CUSTOMIZED
			? LevelGeneratorType.DEFAULT
			: levelProperties.getGeneratorType();
		this.generatorType = levelGeneratorType.getId();
		this.field_3200 = levelProperties.getGeneratorOptions();
		this.structures = levelProperties.hasStructures();
		this.commandsAllowed = levelProperties.areCommandsAllowed();
		if (levelProperties.isHardcore()) {
			this.gameMode = "hardcore";
		} else if (levelProperties.getGameMode().isSurvivalLike()) {
			this.gameMode = "survival";
		} else if (levelProperties.getGameMode().isCreative()) {
			this.gameMode = "creative";
		}
	}
}
