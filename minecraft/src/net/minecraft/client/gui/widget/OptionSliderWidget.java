package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public abstract class OptionSliderWidget extends SliderWidget {
	protected final GameOptions options;

	protected OptionSliderWidget(GameOptions options, int x, int y, int width, int height, double value) {
		super(x, y, width, height, LiteralText.EMPTY, value);
		this.options = options;
	}
}
