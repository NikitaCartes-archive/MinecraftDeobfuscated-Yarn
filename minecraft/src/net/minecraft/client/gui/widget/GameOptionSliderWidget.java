package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class GameOptionSliderWidget extends SliderWidget {
	private final DoubleOption option;

	public GameOptionSliderWidget(GameOptions gameOptions, int i, int j, int k, int l, DoubleOption doubleOption) {
		super(gameOptions, i, j, k, l, (double)((float)doubleOption.getRatio(doubleOption.get(gameOptions))));
		this.option = doubleOption;
		this.updateMessage();
	}

	@Override
	protected void applyValue() {
		this.option.set(this.options, this.option.getValue(this.value));
		this.options.write();
	}

	@Override
	protected void updateMessage() {
		this.setMessage(this.option.getDisplayString(this.options));
	}
}
