package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class FontOptionsScreen extends GameOptionsScreen {
	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{gameOptions.getForceUnicodeFont(), gameOptions.getJapaneseGlyphVariants()};
	}

	public FontOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, Text.translatable("options.font.title"));
	}

	@Override
	protected void addOptions() {
		this.body.addAll(getOptions(this.gameOptions));
	}
}
