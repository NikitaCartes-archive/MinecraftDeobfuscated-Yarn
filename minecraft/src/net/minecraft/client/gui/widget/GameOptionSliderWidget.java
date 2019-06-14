package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;

@Environment(EnvType.CLIENT)
public class GameOptionSliderWidget extends SliderWidget {
	private final DoubleOption option;

	public GameOptionSliderWidget(GameOptions gameOptions, int i, int j, int k, int l, DoubleOption doubleOption) {
		super(gameOptions, i, j, k, l, (double)((float)doubleOption.method_18611(doubleOption.get(gameOptions))));
		this.option = doubleOption;
		this.updateMessage();
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.option == Option.field_1931) {
			this.updateMessage();
		}

		super.renderButton(i, j, f);
	}

	@Override
	protected void applyValue() {
		this.option.set(this.options, this.option.method_18616(this.value));
		this.options.write();
	}

	@Override
	protected void updateMessage() {
		this.setMessage(this.option.getDisplayString(this.options));
	}
}
