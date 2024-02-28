package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SoundOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.sounds.title");
	private OptionListWidget optionButtons;

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{gameOptions.getShowSubtitles(), gameOptions.getDirectionalAudio()};
	}

	public SoundOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, TITLE_TEXT);
	}

	@Override
	protected void init() {
		this.optionButtons = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height, this));
		this.optionButtons.addSingleOptionEntry(this.gameOptions.getSoundVolumeOption(SoundCategory.MASTER));
		this.optionButtons.addAll(this.getVolumeOptions());
		this.optionButtons.addSingleOptionEntry(this.gameOptions.getSoundDevice());
		this.optionButtons.addAll(getOptions(this.gameOptions));
		super.init();
	}

	@Override
	protected void initTabNavigation() {
		super.initTabNavigation();
		this.optionButtons.position(this.width, this.layout);
	}

	private SimpleOption<?>[] getVolumeOptions() {
		return (SimpleOption<?>[])Arrays.stream(SoundCategory.values())
			.filter(category -> category != SoundCategory.MASTER)
			.map(category -> this.gameOptions.getSoundVolumeOption(category))
			.toArray(SimpleOption[]::new);
	}
}
