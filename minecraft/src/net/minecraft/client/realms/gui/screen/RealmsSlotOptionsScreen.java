package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RealmsSlotOptionsScreen extends RealmsScreen {
	public static final Text[] DIFFICULTIES = new Text[]{
		new TranslatableText("options.difficulty.peaceful"),
		new TranslatableText("options.difficulty.easy"),
		new TranslatableText("options.difficulty.normal"),
		new TranslatableText("options.difficulty.hard")
	};
	public static final Text[] GAME_MODES = new Text[]{
		new TranslatableText("selectWorld.gameMode.survival"),
		new TranslatableText("selectWorld.gameMode.creative"),
		new TranslatableText("selectWorld.gameMode.adventure")
	};
	private static final Text field_24207 = new TranslatableText("mco.configure.world.on");
	private static final Text field_24208 = new TranslatableText("mco.configure.world.off");
	private static final Text field_25884 = new TranslatableText("selectWorld.gameMode");
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
	private ButtonWidget gameModeButton;
	private RealmsLabel titleLabel;
	private RealmsLabel toastMessage;

	public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen parent, RealmsWorldOptions options, RealmsServer.WorldType worldType, int activeSlot) {
		this.parent = parent;
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
			Text text;
			if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
				text = new TranslatableText("mco.configure.world.edit.subscreen.adventuremap");
			} else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
				text = new TranslatableText("mco.configure.world.edit.subscreen.inspiration");
			} else {
				text = new TranslatableText("mco.configure.world.edit.subscreen.experience");
			}

			this.toastMessage = new RealmsLabel(text, this.width / 2, 26, 16711680);
			this.pvp = true;
			this.spawnProtection = 0;
			this.forceGameMode = false;
			this.spawnAnimals = true;
			this.spawnMonsters = true;
			this.spawnNPCs = true;
			this.commandBlocks = true;
		}

		this.nameEdit = new TextFieldWidget(
			this.client.textRenderer, this.column1_x + 2, row(1), this.column_width - 4, 20, null, new TranslatableText("mco.configure.world.edit.slot.name")
		);
		this.nameEdit.setMaxLength(10);
		this.nameEdit.setText(this.options.getSlotName(this.activeSlot));
		this.focusOn(this.nameEdit);
		this.pvpButton = this.addButton(new ButtonWidget(this.column2_x, row(1), this.column_width, 20, this.pvpTitle(), buttonWidget -> {
			this.pvp = !this.pvp;
			buttonWidget.setMessage(this.pvpTitle());
		}));
		this.addButton(new ButtonWidget(this.column1_x, row(3), this.column_width, 20, this.gameModeTitle(), buttonWidget -> {
			this.gameModeIndex = (this.gameModeIndex + 1) % GAME_MODES.length;
			buttonWidget.setMessage(this.gameModeTitle());
		}));
		this.spawnAnimalsButton = this.addButton(new ButtonWidget(this.column2_x, row(3), this.column_width, 20, this.spawnAnimalsTitle(), buttonWidget -> {
			this.spawnAnimals = !this.spawnAnimals;
			buttonWidget.setMessage(this.spawnAnimalsTitle());
		}));
		this.addButton(new ButtonWidget(this.column1_x, row(5), this.column_width, 20, this.difficultyTitle(), buttonWidget -> {
			this.difficultyIndex = (this.difficultyIndex + 1) % DIFFICULTIES.length;
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
		this.gameModeButton = this.addButton(new ButtonWidget(this.column1_x, row(9), this.column_width, 20, this.forceGameModeTitle(), buttonWidget -> {
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
			this.gameModeButton.active = false;
		}

		if (this.difficultyIndex == 0) {
			this.spawnMonstersButton.active = false;
		}

		this.addButton(
			new ButtonWidget(
				this.column1_x, row(13), this.column_width, 20, new TranslatableText("mco.configure.world.buttons.done"), buttonWidget -> this.saveSettings()
			)
		);
		this.addButton(new ButtonWidget(this.column2_x, row(13), this.column_width, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
		this.addChild(this.nameEdit);
		this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.buttons.options"), this.width / 2, 17, 16777215));
		if (this.toastMessage != null) {
			this.addChild(this.toastMessage);
		}

		this.narrateLabels();
	}

	private Text difficultyTitle() {
		return new TranslatableText("options.difficulty").append(": ").append(DIFFICULTIES[this.difficultyIndex]);
	}

	private Text gameModeTitle() {
		return new TranslatableText("options.generic_value", field_25884, GAME_MODES[this.gameModeIndex]);
	}

	private Text pvpTitle() {
		return new TranslatableText("mco.configure.world.pvp").append(": ").append(getWorldConfigureMessage(this.pvp));
	}

	private Text spawnAnimalsTitle() {
		return new TranslatableText("mco.configure.world.spawnAnimals").append(": ").append(getWorldConfigureMessage(this.spawnAnimals));
	}

	private Text spawnMonstersTitle() {
		return this.difficultyIndex == 0
			? new TranslatableText("mco.configure.world.spawnMonsters").append(": ").append(new TranslatableText("mco.configure.world.off"))
			: new TranslatableText("mco.configure.world.spawnMonsters").append(": ").append(getWorldConfigureMessage(this.spawnMonsters));
	}

	private Text spawnNPCsTitle() {
		return new TranslatableText("mco.configure.world.spawnNPCs").append(": ").append(getWorldConfigureMessage(this.spawnNPCs));
	}

	private Text commandBlocksTitle() {
		return new TranslatableText("mco.configure.world.commandBlocks").append(": ").append(getWorldConfigureMessage(this.commandBlocks));
	}

	private Text forceGameModeTitle() {
		return new TranslatableText("mco.configure.world.forceGameMode").append(": ").append(getWorldConfigureMessage(this.forceGameMode));
	}

	private static Text getWorldConfigureMessage(boolean enabled) {
		return enabled ? field_24207 : field_24208;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		String string = I18n.translate("mco.configure.world.edit.slot.name");
		this.textRenderer
			.draw(matrices, string, (float)(this.column1_x + this.column_width / 2 - this.textRenderer.getWidth(string) / 2), (float)(row(0) - 5), 16777215);
		this.titleLabel.render(this, matrices);
		if (this.toastMessage != null) {
			this.toastMessage.render(this, matrices);
		}

		this.nameEdit.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
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
		private final double min;
		private final double max;

		public SettingsSlider(int id, int x, int y, int width, float min, float max) {
			super(id, x, y, 20, LiteralText.EMPTY, 0.0);
			this.min = (double)min;
			this.max = (double)max;
			this.value = (double)((MathHelper.clamp((float)width, min, max) - min) / (max - min));
			this.updateMessage();
		}

		@Override
		public void applyValue() {
			if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
				RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
			}
		}

		@Override
		protected void updateMessage() {
			this.setMessage(
				new TranslatableText("mco.configure.world.spawnProtection")
					.append(": ")
					.append(
						(Text)(RealmsSlotOptionsScreen.this.spawnProtection == 0
							? new TranslatableText("mco.configure.world.off")
							: new LiteralText(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection)))
					)
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
