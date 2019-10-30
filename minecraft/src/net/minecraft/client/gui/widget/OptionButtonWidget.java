package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.Option;

@Environment(EnvType.CLIENT)
public class OptionButtonWidget extends ButtonWidget {
	private final Option option;

	public OptionButtonWidget(int x, int y, int width, int height, Option option, String text, ButtonWidget.PressAction pressAction) {
		super(x, y, width, height, text, pressAction);
		this.option = option;
	}
}
