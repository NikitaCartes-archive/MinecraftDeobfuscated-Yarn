package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SoundOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.sounds.title");

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{gameOptions.getShowSubtitles(), gameOptions.getDirectionalAudio()};
	}

	public SoundOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, TITLE_TEXT);
	}

	@Override
	protected void method_60325() {
		this.field_51824.addSingleOptionEntry(this.gameOptions.getSoundVolumeOption(SoundCategory.MASTER));
		this.field_51824.addAll(this.getVolumeOptions());
		this.field_51824.addSingleOptionEntry(this.gameOptions.getSoundDevice());
		this.field_51824.addAll(getOptions(this.gameOptions));
	}

	private SimpleOption<?>[] getVolumeOptions() {
		return (SimpleOption<?>[])Arrays.stream(SoundCategory.values())
			.filter(category -> category != SoundCategory.MASTER)
			.map(category -> this.gameOptions.getSoundVolumeOption(category))
			.toArray(SimpleOption[]::new);
	}
}
