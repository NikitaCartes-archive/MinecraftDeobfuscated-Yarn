package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.Option;

@Environment(EnvType.CLIENT)
public class OptionButtonWidget extends ButtonWidget {
	private final Option option;

	public OptionButtonWidget(int i, int j, int k, int l, Option option, String string, ButtonWidget.PressAction pressAction) {
		super(i, j, k, l, string, pressAction);
		this.option = option;
	}
}
