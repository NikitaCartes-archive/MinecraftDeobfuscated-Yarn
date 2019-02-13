package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class GameOptionSliderWidget extends SliderWidget {
	private final GameOptions.Option option;

	public GameOptionSliderWidget(MinecraftClient minecraftClient, int i, int j, int k, GameOptions.Option option) {
		this(minecraftClient, i, j, k, 150, 20, option);
	}

	public GameOptionSliderWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, GameOptions.Option option) {
		super(i, j, k, l, m, minecraftClient, (float)option.method_1651(minecraftClient.options.getDouble(option)));
		this.option = option;
		this.updateText();
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.option == GameOptions.Option.FULLSCREEN_RESOLUTION) {
			this.updateText();
		}

		super.draw(i, j, f);
	}

	@Override
	protected void onProgressChanged() {
		GameOptions gameOptions = this.client.options;
		gameOptions.setDouble(this.option, this.option.progressToValue(this.progress));
		gameOptions.write();
	}

	@Override
	protected void updateText() {
		this.setText(this.client.options.getTranslatedName(this.option));
	}
}
