package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RealmsSlotOptionsScreen extends RealmsScreen {
	public static final String[] field_22723 = new String[]{
		"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"
	};
	public static final String[] field_22724 = new String[]{"selectWorld.gameMode.survival", "selectWorld.gameMode.creative", "selectWorld.gameMode.adventure"};
	private TextFieldWidget nameEdit;
	protected final RealmsConfigureWorldScreen parent;
	private int column1_x;
	private int column_width;
	private int column2_x;
	private final RealmsWorldOptions options;
	private final RealmsServer.WorldType worldType;
	private final int activeSlot;
	private int difficultyIndex;
	private int gameModeIndex;
	private Boolean pvp;
	private Boolean spawnNPCs;
	private Boolean spawnAnimals;
	private Boolean spawnMonsters;
	private Integer spawnProtection;
	private Boolean commandBlocks;
	private Boolean forceGameMode;
	private ButtonWidget pvpButton;
	private ButtonWidget spawnAnimalsButton;
	private ButtonWidget spawnMonstersButton;
	private ButtonWidget spawnNPCsButton;
	private RealmsSlotOptionsScreen.SettingsSlider spawnProtectionButton;
	private ButtonWidget commandBlocksButton;
	private ButtonWidget field_22722;
	private RealmsLabel titleLabel;
	private RealmsLabel field_20502;

	public RealmsSlotOptionsScreen(
		RealmsConfigureWorldScreen realmsConfigureWorldScreen, RealmsWorldOptions options, RealmsServer.WorldType worldType, int activeSlot
	) {
		this.parent = realmsConfigureWorldScreen;
		this.options = options;
		this.worldType = worldType;
		this.activeSlot = activeSlot;
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public void tick() {
		this.nameEdit.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void init() {
		this.column_width = 170;
		this.column1_x = this.width / 2 - this.column_width;
		this.column2_x = this.width / 2 + 10;
		this.difficultyIndex = this.options.difficulty;
		this.gameModeIndex = this.options.gameMode;
		if (this.worldType == RealmsServer.WorldType.NORMAL) {
			this.pvp = this.options.pvp;
			this.spawnProtection = this.options.spawnProtection;
			this.forceGameMode = this.options.forceGameMode;
			this.spawnAnimals = this.options.spawnAnimals;
			this.spawnMonsters = this.options.spawnMonsters;
			this.spawnNPCs = this.options.spawnNPCs;
			this.commandBlocks = this.options.commandBlocks;
		} else {
			String string;
			if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
				string = I18n.translate("mco.configure.world.edit.subscreen.adventuremap");
			} else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
				string = I18n.translate("mco.configure.world.edit.subscreen.inspiration");
			} else {
				string = I18n.translate("mco.configure.world.edit.subscreen.experience");
			}

			this.field_20502 = new RealmsLabel(string, this.width / 2, 26, 16711680);
			this.pvp = true;
			this.spawnProtection = 0;
			this.forceGameMode = false;
			this.spawnAnimals = true;
			this.spawnMonsters = true;
			this.spawnNPCs = true;
			this.commandBlocks = true;
		}

		this.nameEdit = new TextFieldWidget(
			this.client.textRenderer, this.column1_x + 2, row(1), this.column_width - 4, 20, null, I18n.translate("mco.configure.world.edit.slot.name")
		);
		this.nameEdit.setMaxLength(10);
		this.nameEdit.setText(this.options.getSlotName(this.activeSlot));
		this.focusOn(this.nameEdit);
		this.pvpButton = this.addButton(new ButtonWidget(this.column2_x, row(1), this.column_width, 20, this.pvpTitle(), buttonWidget -> {
			this.pvp = !this.pvp;
			buttonWidget.setMessage(this.pvpTitle());
		}));
		this.addButton(new ButtonWidget(this.column1_x, row(3), this.column_width, 20, this.gameModeTitle(), buttonWidget -> {
			this.gameModeIndex = (this.gameModeIndex + 1) % field_22724.length;
			buttonWidget.setMessage(this.gameModeTitle());
		}));
		this.spawnAnimalsButton = this.addButton(new ButtonWidget(this.column2_x, row(3), this.column_width, 20, this.spawnAnimalsTitle(), buttonWidget -> {
			this.spawnAnimals = !this.spawnAnimals;
			buttonWidget.setMessage(this.spawnAnimalsTitle());
		}));
		this.addButton(new ButtonWidget(this.column1_x, row(5), this.column_width, 20, this.difficultyTitle(), buttonWidget -> {
			this.difficultyIndex = (this.difficultyIndex + 1) % field_22723.length;
			buttonWidget.setMessage(this.difficultyTitle());
			if (this.worldType == RealmsServer.WorldType.NORMAL) {
				this.spawnMonstersButton.active = this.difficultyIndex != 0;
				this.spawnMonstersButton.setMessage(this.spawnMonstersTitle());
			}
		}));
		this.spawnMonstersButton = this.addButton(new ButtonWidget(this.column2_x, row(5), this.column_width, 20, this.spawnMonstersTitle(), buttonWidget -> {
			this.spawnMonsters = !this.spawnMonsters;
			buttonWidget.setMessage(this.spawnMonstersTitle());
		}));
		this.spawnProtectionButton = this.addButton(
			new RealmsSlotOptionsScreen.SettingsSlider(this.column1_x, row(7), this.column_width, this.spawnProtection, 0.0F, 16.0F)
		);
		this.spawnNPCsButton = this.addButton(new ButtonWidget(this.column2_x, row(7), this.column_width, 20, this.spawnNPCsTitle(), buttonWidget -> {
			this.spawnNPCs = !this.spawnNPCs;
			buttonWidget.setMessage(this.spawnNPCsTitle());
		}));
		this.field_22722 = this.addButton(new ButtonWidget(this.column1_x, row(9), this.column_width, 20, this.forceGameModeTitle(), buttonWidget -> {
			this.forceGameMode = !this.forceGameMode;
			buttonWidget.setMessage(this.forceGameModeTitle());
		}));
		this.commandBlocksButton = this.addButton(new ButtonWidget(this.column2_x, row(9), this.column_width, 20, this.commandBlocksTitle(), buttonWidget -> {
			this.commandBlocks = !this.commandBlocks;
			buttonWidget.setMessage(this.commandBlocksTitle());
		}));
		if (this.worldType != RealmsServer.WorldType.NORMAL) {
			this.pvpButton.active = false;
			this.spawnAnimalsButton.active = false;
			this.spawnNPCsButton.active = false;
			this.spawnMonstersButton.active = false;
			this.spawnProtectionButton.active = false;
			this.commandBlocksButton.active = false;
			this.field_22722.active = false;
		}

		if (this.difficultyIndex == 0) {
			this.spawnMonstersButton.active = false;
		}

		this.addButton(
			new ButtonWidget(this.column1_x, row(13), this.column_width, 20, I18n.translate("mco.configure.world.buttons.done"), buttonWidget -> this.saveSettings())
		);
		this.addButton(
			new ButtonWidget(this.column2_x, row(13), this.column_width, 20, I18n.translate("gui.cancel"), buttonWidget -> this.client.openScreen(this.parent))
		);
		this.addChild(this.nameEdit);
		this.titleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.configure.world.buttons.options"), this.width / 2, 17, 16777215));
		if (this.field_20502 != null) {
			this.addChild(this.field_20502);
		}

		this.narrateLabels();
	}

	private String difficultyTitle() {
		return I18n.translate("options.difficulty") + ": " + I18n.translate(field_22723[this.difficultyIndex]);
	}

	private String gameModeTitle() {
		return I18n.translate("selectWorld.gameMode") + ": " + I18n.translate(field_22724[this.gameModeIndex]);
	}

	private String pvpTitle() {
		return I18n.translate("mco.configure.world.pvp") + ": " + method_25258(this.pvp);
	}

	private String spawnAnimalsTitle() {
		return I18n.translate("mco.configure.world.spawnAnimals") + ": " + method_25258(this.spawnAnimals);
	}

	private String spawnMonstersTitle() {
		return this.difficultyIndex == 0
			? I18n.translate("mco.configure.world.spawnMonsters") + ": " + I18n.translate("mco.configure.world.off")
			: I18n.translate("mco.configure.world.spawnMonsters") + ": " + method_25258(this.spawnMonsters);
	}

	private String spawnNPCsTitle() {
		return I18n.translate("mco.configure.world.spawnNPCs") + ": " + method_25258(this.spawnNPCs);
	}

	private String commandBlocksTitle() {
		return I18n.translate("mco.configure.world.commandBlocks") + ": " + method_25258(this.commandBlocks);
	}

	private String forceGameModeTitle() {
		return I18n.translate("mco.configure.world.forceGameMode") + ": " + method_25258(this.forceGameMode);
	}

	private static String method_25258(boolean bl) {
		return I18n.translate(bl ? "mco.configure.world.on" : "mco.configure.world.off");
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		String string = I18n.translate("mco.configure.world.edit.slot.name");
		this.textRenderer.draw(string, (float)(this.column1_x + this.column_width / 2 - this.textRenderer.getStringWidth(string) / 2), (float)(row(0) - 5), 16777215);
		this.titleLabel.render(this);
		if (this.field_20502 != null) {
			this.field_20502.render(this);
		}

		this.nameEdit.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}

	private String getSlotName() {
		return this.nameEdit.getText().equals(this.options.getDefaultSlotName(this.activeSlot)) ? "" : this.nameEdit.getText();
	}

	private void saveSettings() {
		if (this.worldType != RealmsServer.WorldType.ADVENTUREMAP
			&& this.worldType != RealmsServer.WorldType.EXPERIENCE
			&& this.worldType != RealmsServer.WorldType.INSPIRATION) {
			this.parent
				.saveSlotSettings(
					new RealmsWorldOptions(
						this.pvp,
						this.spawnAnimals,
						this.spawnMonsters,
						this.spawnNPCs,
						this.spawnProtection,
						this.commandBlocks,
						this.difficultyIndex,
						this.gameModeIndex,
						this.forceGameMode,
						this.getSlotName()
					)
				);
		} else {
			this.parent
				.saveSlotSettings(
					new RealmsWorldOptions(
						this.options.pvp,
						this.options.spawnAnimals,
						this.options.spawnMonsters,
						this.options.spawnNPCs,
						this.options.spawnProtection,
						this.options.commandBlocks,
						this.difficultyIndex,
						this.gameModeIndex,
						this.options.forceGameMode,
						this.getSlotName()
					)
				);
		}
	}

	@Environment(EnvType.CLIENT)
	class SettingsSlider extends SliderWidget {
		private final double field_22725;
		private final double field_22726;

		public SettingsSlider(int id, int x, int y, int width, float f, float g) {
			super(id, x, y, 20, "", 0.0);
			this.field_22725 = (double)f;
			this.field_22726 = (double)g;
			this.value = (double)((MathHelper.clamp((float)width, f, g) - f) / (g - f));
			this.updateMessage();
		}

		@Override
		public void applyValue() {
			if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
				RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.field_22725, this.field_22726);
			}
		}

		@Override
		protected void updateMessage() {
			this.setMessage(
				I18n.translate("mco.configure.world.spawnProtection")
					+ ": "
					+ (RealmsSlotOptionsScreen.this.spawnProtection == 0 ? I18n.translate("mco.configure.world.off") : RealmsSlotOptionsScreen.this.spawnProtection)
			);
		}

		@Override
		public void onClick(double mouseX, double mouseY) {
		}

		@Override
		public void onRelease(double mouseX, double mouseY) {
		}
	}
}
