package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsSlotOptionsScreen extends RealmsScreen {
	private static final int field_32125 = 2;
	public static final List<Difficulty> DIFFICULTIES = ImmutableList.of(Difficulty.PEACEFUL, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD);
	private static final int field_32126 = 0;
	public static final List<GameMode> GAME_MODES = ImmutableList.of(GameMode.SURVIVAL, GameMode.CREATIVE, GameMode.ADVENTURE);
	private static final Text EDIT_SLOT_NAME = new TranslatableText("mco.configure.world.edit.slot.name");
	static final Text SPAWN_PROTECTION = new TranslatableText("mco.configure.world.spawnProtection");
	private TextFieldWidget nameEdit;
	protected final RealmsConfigureWorldScreen parent;
	private int column1_x;
	private int column2_x;
	private final RealmsWorldOptions options;
	private final RealmsServer.WorldType worldType;
	private final int activeSlot;
	private Difficulty field_27943;
	private GameMode gameModeIndex;
	private boolean pvp;
	private boolean spawnNPCs;
	private boolean spawnAnimals;
	private boolean spawnMonsters;
	int difficultyIndex;
	private boolean commandBlocks;
	private boolean forceGameMode;
	RealmsSlotOptionsScreen.SettingsSlider spawnProtectionButton;
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
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public void tick() {
		this.nameEdit.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private static <T> T method_32498(List<T> list, int i, int j) {
		try {
			return (T)list.get(i);
		} catch (IndexOutOfBoundsException var4) {
			return (T)list.get(j);
		}
	}

	private static <T> int method_32499(List<T> list, T object, int i) {
		int j = list.indexOf(object);
		return j == -1 ? i : j;
	}

	@Override
	public void init() {
		this.column2_x = 170;
		this.column1_x = this.width / 2 - this.column2_x;
		int i = this.width / 2 + 10;
		this.field_27943 = method_32498(DIFFICULTIES, this.options.difficulty, 2);
		this.gameModeIndex = method_32498(GAME_MODES, this.options.gameMode, 0);
		if (this.worldType == RealmsServer.WorldType.NORMAL) {
			this.pvp = this.options.pvp;
			this.difficultyIndex = this.options.spawnProtection;
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
			this.difficultyIndex = 0;
			this.forceGameMode = false;
			this.spawnAnimals = true;
			this.spawnMonsters = true;
			this.spawnNPCs = true;
			this.commandBlocks = true;
		}

		this.nameEdit = new TextFieldWidget(
			this.client.textRenderer, this.column1_x + 2, row(1), this.column2_x - 4, 20, null, new TranslatableText("mco.configure.world.edit.slot.name")
		);
		this.nameEdit.setMaxLength(10);
		this.nameEdit.setText(this.options.getSlotName(this.activeSlot));
		this.focusOn(this.nameEdit);
		CyclingButtonWidget<Boolean> cyclingButtonWidget = this.addButton(
			CyclingButtonWidget.onOffBuilder(this.pvp)
				.build(i, row(1), this.column2_x, 20, new TranslatableText("mco.configure.world.pvp"), (cyclingButtonWidgetx, boolean_) -> this.pvp = boolean_)
		);
		this.addButton(
			CyclingButtonWidget.<GameMode>builder(GameMode::getSimpleTranslatableName)
				.values(GAME_MODES)
				.initially(this.gameModeIndex)
				.build(
					this.column1_x,
					row(3),
					this.column2_x,
					20,
					new TranslatableText("selectWorld.gameMode"),
					(cyclingButtonWidgetx, gameMode) -> this.gameModeIndex = gameMode
				)
		);
		CyclingButtonWidget<Boolean> cyclingButtonWidget2 = this.addButton(
			CyclingButtonWidget.onOffBuilder(this.spawnAnimals)
				.build(
					i, row(3), this.column2_x, 20, new TranslatableText("mco.configure.world.spawnAnimals"), (cyclingButtonWidgetx, boolean_) -> this.spawnAnimals = boolean_
				)
		);
		CyclingButtonWidget<Boolean> cyclingButtonWidget3 = CyclingButtonWidget.onOffBuilder(this.field_27943 != Difficulty.PEACEFUL && this.spawnMonsters)
			.build(
				i, row(5), this.column2_x, 20, new TranslatableText("mco.configure.world.spawnMonsters"), (cyclingButtonWidgetx, boolean_) -> this.spawnMonsters = boolean_
			);
		this.addButton(
			CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
				.values(DIFFICULTIES)
				.initially(this.field_27943)
				.build(this.column1_x, row(5), this.column2_x, 20, new TranslatableText("options.difficulty"), (cyclingButtonWidget2x, difficulty) -> {
					this.field_27943 = difficulty;
					if (this.worldType == RealmsServer.WorldType.NORMAL) {
						boolean bl = this.field_27943 != Difficulty.PEACEFUL;
						cyclingButtonWidget3.active = bl;
						cyclingButtonWidget3.setValue(bl && this.spawnMonsters);
					}
				})
		);
		this.addButton(cyclingButtonWidget3);
		this.spawnProtectionButton = this.addButton(
			new RealmsSlotOptionsScreen.SettingsSlider(this.column1_x, row(7), this.column2_x, this.difficultyIndex, 0.0F, 16.0F)
		);
		CyclingButtonWidget<Boolean> cyclingButtonWidget4 = this.addButton(
			CyclingButtonWidget.onOffBuilder(this.spawnNPCs)
				.build(i, row(7), this.column2_x, 20, new TranslatableText("mco.configure.world.spawnNPCs"), (cyclingButtonWidgetx, boolean_) -> this.spawnNPCs = boolean_)
		);
		CyclingButtonWidget<Boolean> cyclingButtonWidget5 = this.addButton(
			CyclingButtonWidget.onOffBuilder(this.forceGameMode)
				.build(
					this.column1_x,
					row(9),
					this.column2_x,
					20,
					new TranslatableText("mco.configure.world.forceGameMode"),
					(cyclingButtonWidgetx, boolean_) -> this.forceGameMode = boolean_
				)
		);
		CyclingButtonWidget<Boolean> cyclingButtonWidget6 = this.addButton(
			CyclingButtonWidget.onOffBuilder(this.commandBlocks)
				.build(
					i,
					row(9),
					this.column2_x,
					20,
					new TranslatableText("mco.configure.world.commandBlocks"),
					(cyclingButtonWidgetx, boolean_) -> this.commandBlocks = boolean_
				)
		);
		if (this.worldType != RealmsServer.WorldType.NORMAL) {
			cyclingButtonWidget.active = false;
			cyclingButtonWidget2.active = false;
			cyclingButtonWidget4.active = false;
			cyclingButtonWidget3.active = false;
			this.spawnProtectionButton.active = false;
			cyclingButtonWidget6.active = false;
			cyclingButtonWidget5.active = false;
		}

		if (this.field_27943 == Difficulty.PEACEFUL) {
			cyclingButtonWidget3.active = false;
		}

		this.addButton(
			new ButtonWidget(this.column1_x, row(13), this.column2_x, 20, new TranslatableText("mco.configure.world.buttons.done"), buttonWidget -> this.saveSettings())
		);
		this.addButton(new ButtonWidget(i, row(13), this.column2_x, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
		this.addChild(this.nameEdit);
		this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.buttons.options"), this.width / 2, 17, 16777215));
		if (this.toastMessage != null) {
			this.addChild(this.toastMessage);
		}

		this.narrateLabels();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.textRenderer
			.draw(matrices, EDIT_SLOT_NAME, (float)(this.column1_x + this.column2_x / 2 - this.textRenderer.getWidth(EDIT_SLOT_NAME) / 2), (float)(row(0) - 5), 16777215);
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
		int i = method_32499(DIFFICULTIES, this.field_27943, 2);
		int j = method_32499(GAME_MODES, this.gameModeIndex, 0);
		if (this.worldType != RealmsServer.WorldType.ADVENTUREMAP
			&& this.worldType != RealmsServer.WorldType.EXPERIENCE
			&& this.worldType != RealmsServer.WorldType.INSPIRATION) {
			this.parent
				.saveSlotSettings(
					new RealmsWorldOptions(
						this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.difficultyIndex, this.commandBlocks, i, j, this.forceGameMode, this.getSlotName()
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
						i,
						j,
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

		public SettingsSlider(int x, int y, int width, int value, float min, float max) {
			super(x, y, width, 20, LiteralText.EMPTY, 0.0);
			this.min = (double)min;
			this.max = (double)max;
			this.value = (double)((MathHelper.clamp((float)value, min, max) - min) / (max - min));
			this.updateMessage();
		}

		@Override
		public void applyValue() {
			if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
				RealmsSlotOptionsScreen.this.difficultyIndex = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
			}
		}

		@Override
		protected void updateMessage() {
			this.setMessage(
				ScreenTexts.composeGenericOptionText(
					RealmsSlotOptionsScreen.SPAWN_PROTECTION,
					(Text)(RealmsSlotOptionsScreen.this.difficultyIndex == 0 ? ScreenTexts.OFF : new LiteralText(String.valueOf(RealmsSlotOptionsScreen.this.difficultyIndex)))
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
