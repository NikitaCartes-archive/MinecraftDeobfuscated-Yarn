package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOption;

@Environment(EnvType.CLIENT)
public class OptionButtonWidget extends ButtonWidget {
	private final GameOption field_18970;

	public OptionButtonWidget(int i, int j, int k, int l, GameOption gameOption, String string, ButtonWidget.PressAction pressAction) {
		super(i, j, k, l, string, pressAction);
		this.field_18970 = gameOption;
	}
}
