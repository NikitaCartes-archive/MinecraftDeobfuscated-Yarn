package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SoundOptionsScreen extends GameOptionsScreen {
	public SoundOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, new TranslatableText("options.sounds.title"));
	}

	@Override
	protected void init() {
		int i = this.height / 6 - 12;
		int j = 22;
		int k = 0;
		this.addDrawableChild(new SoundSliderWidget(this.client, this.width / 2 - 155 + k % 2 * 160, i + 22 * (k >> 1), SoundCategory.MASTER, 310));
		k += 2;

		for (SoundCategory soundCategory : SoundCategory.values()) {
			if (soundCategory != SoundCategory.MASTER) {
				this.addDrawableChild(new SoundSliderWidget(this.client, this.width / 2 - 155 + k % 2 * 160, i + 22 * (k >> 1), soundCategory, 150));
				k++;
			}
		}

		if (k % 2 == 1) {
			k++;
		}

		this.addDrawableChild(Option.AUDIO_DEVICE.createButton(this.gameOptions, this.width / 2 - 155, i + 22 * (k >> 1), 310));
		k += 2;
		this.addDrawableChild(Option.SUBTITLES.createButton(this.gameOptions, this.width / 2 - 75, i + 22 * (k >> 1), 150));
		k += 2;
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, i + 22 * (k >> 1), 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
