package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SoundOptionsScreen extends GameOptionsScreen {
	private OptionListWidget optionButtons;

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{gameOptions.getShowSubtitles(), gameOptions.getDirectionalAudio()};
	}

	public SoundOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, Text.translatable("options.sounds.title"));
	}

	@Override
	protected void init() {
		this.optionButtons = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height - 64, 32, 25));
		this.optionButtons.addSingleOptionEntry(this.gameOptions.getSoundVolumeOption(SoundCategory.MASTER));
		this.optionButtons.addAll(this.getVolumeOptions());
		this.optionButtons.addSingleOptionEntry(this.gameOptions.getSoundDevice());
		this.optionButtons.addAll(getOptions(this.gameOptions));
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			this.client.options.write();
			this.client.setScreen(this.parent);
		}).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
	}

	private SimpleOption<?>[] getVolumeOptions() {
		return (SimpleOption<?>[])Arrays.stream(SoundCategory.values())
			.filter(category -> category != SoundCategory.MASTER)
			.map(category -> this.gameOptions.getSoundVolumeOption(category))
			.toArray(SimpleOption[]::new);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
	}
}
