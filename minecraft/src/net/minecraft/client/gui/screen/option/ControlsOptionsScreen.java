package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends GameOptionsScreen {
	private static final int MARGIN_Y = 24;

	public ControlsOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, Text.translatable("controls.title"));
	}

	@Override
	protected void init() {
		super.init();
		int i = this.width / 2 - 155;
		int j = i + 160;
		int k = this.height / 6 - 12;
		this.addDrawableChild(
			ButtonWidget.createBuilder(Text.translatable("options.mouse_settings"), button -> this.client.setScreen(new MouseOptionsScreen(this, this.gameOptions)))
				.setPositionAndSize(i, k, 150, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.createBuilder(Text.translatable("controls.keybinds"), button -> this.client.setScreen(new KeybindsScreen(this, this.gameOptions)))
				.setPositionAndSize(j, k, 150, 20)
				.build()
		);
		k += 24;
		this.addDrawableChild(this.gameOptions.getSneakToggled().createButton(this.gameOptions, i, k, 150));
		this.addDrawableChild(this.gameOptions.getSprintToggled().createButton(this.gameOptions, j, k, 150));
		k += 24;
		this.addDrawableChild(this.gameOptions.getAutoJump().createButton(this.gameOptions, i, k, 150));
		this.addDrawableChild(this.gameOptions.getOperatorItemsTab().createButton(this.gameOptions, j, k, 150));
		k += 24;
		this.addDrawableChild(
			ButtonWidget.createBuilder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent)).setPositionAndSize(this.width / 2 - 100, k, 200, 20).build()
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
