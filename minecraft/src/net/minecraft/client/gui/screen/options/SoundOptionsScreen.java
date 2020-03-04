package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SoundOptionsScreen extends GameOptionsScreen {
	public SoundOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, new TranslatableText("options.sounds.title"));
	}

	@Override
	protected void init() {
		int i = 0;
		this.addButton(new SoundSliderWidget(this.client, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, 310));
		i += 2;

		for (SoundCategory soundCategory : SoundCategory.values()) {
			if (soundCategory != SoundCategory.MASTER) {
				this.addButton(new SoundSliderWidget(this.client, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory, 150));
				i++;
			}
		}

		this.addButton(
			new OptionButtonWidget(
				this.width / 2 - 75,
				this.height / 6 - 12 + 24 * (++i >> 1),
				150,
				20,
				Option.SUBTITLES,
				Option.SUBTITLES.getDisplayString(this.gameOptions),
				buttonWidget -> {
					Option.SUBTITLES.set(this.client.options);
					buttonWidget.setMessage(Option.SUBTITLES.getDisplayString(this.client.options));
					this.client.options.write();
				}
			)
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.client.openScreen(this.parent))
		);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 15, 16777215);
		super.render(mouseX, mouseY, delta);
	}
}
