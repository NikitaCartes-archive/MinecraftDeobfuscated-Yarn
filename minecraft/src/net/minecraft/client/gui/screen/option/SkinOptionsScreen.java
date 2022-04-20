package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkinOptionsScreen extends GameOptionsScreen {
	public SkinOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, Text.method_43471("options.skinCustomisation.title"));
	}

	@Override
	protected void init() {
		int i = 0;

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			this.addDrawableChild(
				CyclingButtonWidget.onOffBuilder(this.gameOptions.isPlayerModelPartEnabled(playerModelPart))
					.build(
						this.width / 2 - 155 + i % 2 * 160,
						this.height / 6 + 24 * (i >> 1),
						150,
						20,
						playerModelPart.getOptionName(),
						(button, enabled) -> this.gameOptions.togglePlayerModelPart(playerModelPart, enabled)
					)
			);
			i++;
		}

		this.addDrawableChild(this.gameOptions.getMainArm().createButton(this.gameOptions, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150));
		if (++i % 2 == 1) {
			i++;
		}

		this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent))
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
