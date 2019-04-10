package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.DoubleGameOption;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class GameOptionSliderWidget extends SliderWidget {
	private final DoubleGameOption option;

	public GameOptionSliderWidget(GameOptions gameOptions, int i, int j, int k, int l, DoubleGameOption doubleGameOption) {
		super(gameOptions, i, j, k, l, (double)((float)doubleGameOption.method_18611(doubleGameOption.get(gameOptions))));
		this.option = doubleGameOption;
		this.updateMessage();
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.option == GameOption.FULLSCREEN_RESOLUTION) {
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
