package net.minecraft.client.gui.menu;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class NewLevelGui extends Gui {
	private final Gui parent;
	private TextFieldWidget field_3188;
	private TextFieldWidget field_3184;
	private String field_3196;
	private String gameMode = "survival";
	private String field_3185;
	private boolean structures = true;
	private boolean commandsAllowed;
	private boolean field_3179;
	private boolean field_3191;
	private boolean field_3178;
	private boolean field_3190;
	private boolean field_3202;
	private ButtonWidget field_3205;
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
	private static final String[] INVALID_LEVEL_NAMES = new String[]{
		"CON",
		"COM",
		"PRN",
		"AUX",
		"CLOCK$",
		"NUL",
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
		"COM7",
		"COM8",
		"COM9",
		"LPT1",
		"LPT2",
		"LPT3",
		"LPT4",
		"LPT5",
		"LPT6",
		"LPT7",
		"LPT8",
		"LPT9"
	};

	public NewLevelGui(Gui gui) {
		this.parent = gui;
		this.seed = "";
		this.levelName = I18n.translate("selectWorld.newWorld");
	}

	@Override
	public void update() {
		this.field_3188.tick();
		this.field_3184.tick();
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_3205 = this.addButton(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelGui.this.method_2736();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelGui.this.client.openGui(NewLevelGui.this.parent);
			}
		});
		this.buttonGameModeSwitch = this.addButton(new ButtonWidget(2, this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode")) {
			@Override
			public void onPressed(double d, double e) {
				if ("survival".equals(NewLevelGui.this.gameMode)) {
					if (!NewLevelGui.this.field_3179) {
						NewLevelGui.this.commandsAllowed = false;
					}

					NewLevelGui.this.field_3178 = false;
					NewLevelGui.this.gameMode = "hardcore";
					NewLevelGui.this.field_3178 = true;
					NewLevelGui.this.buttonCommandsAllowed.enabled = false;
					NewLevelGui.this.buttonGenerateBonusItems.enabled = false;
					NewLevelGui.this.method_2722();
				} else if ("hardcore".equals(NewLevelGui.this.gameMode)) {
					if (!NewLevelGui.this.field_3179) {
						NewLevelGui.this.commandsAllowed = true;
					}

					NewLevelGui.this.field_3178 = false;
					NewLevelGui.this.gameMode = "creative";
					NewLevelGui.this.method_2722();
					NewLevelGui.this.field_3178 = false;
					NewLevelGui.this.buttonCommandsAllowed.enabled = true;
					NewLevelGui.this.buttonGenerateBonusItems.enabled = true;
				} else {
					if (!NewLevelGui.this.field_3179) {
						NewLevelGui.this.commandsAllowed = false;
					}

					NewLevelGui.this.gameMode = "survival";
					NewLevelGui.this.method_2722();
					NewLevelGui.this.buttonCommandsAllowed.enabled = true;
					NewLevelGui.this.buttonGenerateBonusItems.enabled = true;
					NewLevelGui.this.field_3178 = false;
				}

				NewLevelGui.this.method_2722();
			}
		});
		this.buttonMoreOptions = this.addButton(new ButtonWidget(3, this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelGui.this.method_2721();
			}
		});
		this.buttonGenerateStructures = this.addButton(new ButtonWidget(4, this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelGui.this.structures = !NewLevelGui.this.structures;
				NewLevelGui.this.method_2722();
			}
		});
		this.buttonGenerateStructures.visible = false;
		this.buttonGenerateBonusItems = this.addButton(new ButtonWidget(7, this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelGui.this.field_3191 = !NewLevelGui.this.field_3191;
				NewLevelGui.this.method_2722();
			}
		});
		this.buttonGenerateBonusItems.visible = false;
		this.buttonMapTypeSwitch = this.addButton(new ButtonWidget(5, this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelGui.this.generatorType++;
				if (NewLevelGui.this.generatorType >= LevelGeneratorType.TYPES.length) {
					NewLevelGui.this.generatorType = 0;
				}

				while (!NewLevelGui.this.method_2723()) {
					NewLevelGui.this.generatorType++;
					if (NewLevelGui.this.generatorType >= LevelGeneratorType.TYPES.length) {
						NewLevelGui.this.generatorType = 0;
					}
				}

				NewLevelGui.this.field_3200 = new CompoundTag();
				NewLevelGui.this.method_2722();
				NewLevelGui.this.method_2710(NewLevelGui.this.field_3202);
			}
		});
		this.buttonMapTypeSwitch.visible = false;
		this.buttonCommandsAllowed = this.addButton(new ButtonWidget(6, this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelGui.this.field_3179 = true;
				NewLevelGui.this.commandsAllowed = !NewLevelGui.this.commandsAllowed;
				NewLevelGui.this.method_2722();
			}
		});
		this.buttonCommandsAllowed.visible = false;
		this.buttonCustomizeType = this.addButton(new ButtonWidget(8, this.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType")) {
			@Override
			public void onPressed(double d, double e) {
				if (LevelGeneratorType.TYPES[NewLevelGui.this.generatorType] == LevelGeneratorType.FLAT) {
					NewLevelGui.this.client.openGui(new CustomizeFlatLevelGui(NewLevelGui.this, NewLevelGui.this.field_3200));
				}

				if (LevelGeneratorType.TYPES[NewLevelGui.this.generatorType] == LevelGeneratorType.BUFFET) {
					NewLevelGui.this.client.openGui(new CustomizeBuffetLevelGui(NewLevelGui.this, NewLevelGui.this.field_3200));
				}
			}
		});
		this.buttonCustomizeType.visible = false;
		this.field_3188 = new TextFieldWidget(9, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		this.field_3188.setFocused(true);
		this.field_3188.setText(this.levelName);
		this.field_3184 = new TextFieldWidget(10, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		this.field_3184.setText(this.seed);
		this.method_2710(this.field_3202);
		this.method_2727();
		this.method_2722();
	}

	private void method_2727() {
		this.field_3196 = this.field_3188.getText().trim();

		for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
			this.field_3196 = this.field_3196.replace(c, '_');
		}

		if (StringUtils.isEmpty(this.field_3196)) {
			this.field_3196 = "World";
		}

		this.field_3196 = method_2724(this.client.getLevelStorage(), this.field_3196);
	}

	private void method_2722() {
		this.buttonGameModeSwitch.text = I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode);
		this.field_3194 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line1");
		this.field_3199 = I18n.translate("selectWorld.gameMode." + this.gameMode + ".line2");
		this.buttonGenerateStructures.text = I18n.translate("selectWorld.mapFeatures") + " ";
		if (this.structures) {
			this.buttonGenerateStructures.text = this.buttonGenerateStructures.text + I18n.translate("options.on");
		} else {
			this.buttonGenerateStructures.text = this.buttonGenerateStructures.text + I18n.translate("options.off");
		}

		this.buttonGenerateBonusItems.text = I18n.translate("selectWorld.bonusItems") + " ";
		if (this.field_3191 && !this.field_3178) {
			this.buttonGenerateBonusItems.text = this.buttonGenerateBonusItems.text + I18n.translate("options.on");
		} else {
			this.buttonGenerateBonusItems.text = this.buttonGenerateBonusItems.text + I18n.translate("options.off");
		}

		this.buttonMapTypeSwitch.text = I18n.translate("selectWorld.mapType")
			+ " "
			+ I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey());
		this.buttonCommandsAllowed.text = I18n.translate("selectWorld.allowCommands") + " ";
		if (this.commandsAllowed && !this.field_3178) {
			this.buttonCommandsAllowed.text = this.buttonCommandsAllowed.text + I18n.translate("options.on");
		} else {
			this.buttonCommandsAllowed.text = this.buttonCommandsAllowed.text + I18n.translate("options.off");
		}
	}

	public static String method_2724(LevelStorage levelStorage, String string) {
		string = string.replaceAll("[\\./\"]", "_");

		for (String string2 : INVALID_LEVEL_NAMES) {
			if (string.equalsIgnoreCase(string2)) {
				string = "_" + string + "_";
			}
		}

		while (levelStorage.requiresConversion(string) != null) {
			string = string + "-";
		}

		return string;
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	private void method_2736() {
		this.client.openGui(null);
		if (!this.field_3190) {
			this.field_3190 = true;
			long l = new Random().nextLong();
			String string = this.field_3184.getText();
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
			levelInfo.method_8579(Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.field_3200));
			if (this.field_3191 && !this.field_3178) {
				levelInfo.setBonusChest();
			}

			if (this.commandsAllowed && !this.field_3178) {
				levelInfo.enableCommands();
			}

			this.client.startIntegratedServer(this.field_3196, this.field_3188.getText().trim(), levelInfo);
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
			this.buttonGameModeSwitch.enabled = false;
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
			this.buttonGameModeSwitch.enabled = true;
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
		if (this.field_3202) {
			this.buttonMoreOptions.text = I18n.translate("gui.done");
		} else {
			this.buttonMoreOptions.text = I18n.translate("selectWorld.moreWorldOptions");
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_3188.isFocused() && !this.field_3202) {
			this.field_3188.charTyped(c, i);
			this.levelName = this.field_3188.getText();
			this.field_3205.enabled = !this.field_3188.getText().isEmpty();
			this.method_2727();
			return true;
		} else if (this.field_3184.isFocused() && this.field_3202) {
			this.field_3184.charTyped(c, i);
			this.seed = this.field_3184.getText();
			return true;
		} else {
			return super.charTyped(c, i);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_3188.isFocused() && !this.field_3202) {
			this.field_3188.keyPressed(i, j, k);
			this.levelName = this.field_3188.getText();
			this.field_3205.enabled = !this.field_3188.getText().isEmpty();
			this.method_2727();
		} else if (this.field_3184.isFocused() && this.field_3202) {
			this.field_3184.keyPressed(i, j, k);
			this.seed = this.field_3184.getText();
		}

		if (this.field_3205.enabled && (i == 257 || i == 335)) {
			this.method_2736();
		}

		return true;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_3202 ? this.field_3184.mouseClicked(d, e, i) : this.field_3188.mouseClicked(d, e, i);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("selectWorld.create"), this.width / 2, 20, -1);
		if (this.field_3202) {
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
			if (this.buttonGenerateStructures.visible) {
				this.drawString(this.fontRenderer, I18n.translate("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.buttonCommandsAllowed.visible) {
				this.drawString(this.fontRenderer, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.field_3184.render(i, j, f);
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
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.fontRenderer, I18n.translate("selectWorld.resultFolder") + " " + this.field_3196, this.width / 2 - 100, 85, -6250336);
			this.field_3188.render(i, j, f);
			this.drawStringCentered(this.fontRenderer, this.field_3194, this.width / 2, 137, -6250336);
			this.drawStringCentered(this.fontRenderer, this.field_3199, this.width / 2, 149, -6250336);
		}

		super.draw(i, j, f);
	}

	public void method_2737(LevelProperties levelProperties) {
		this.levelName = I18n.translate("selectWorld.newWorld.copyOf", levelProperties.getLevelName());
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
