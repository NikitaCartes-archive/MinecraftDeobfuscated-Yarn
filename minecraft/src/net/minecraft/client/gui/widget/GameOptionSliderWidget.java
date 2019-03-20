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
		super(gameOptions, i, j, k, l, (double)((float)doubleGameOption.method_18611(doubleGameOption.method_18613(gameOptions))));
		this.option = doubleGameOption;
		this.updateText();
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.option == GameOption.FULLSCREEN_RESOLUTION) {
			this.updateText();
		}

		super.renderButton(i, j, f);
	}

	@Override
	protected void onProgressChanged() {
		this.option.method_18614(this.gameOptions, this.option.method_18616(this.progress));
		this.gameOptions.write();
	}

	@Override
	protected void updateText() {
		this.setMessage(this.option.method_18619(this.gameOptions));
	}
}
