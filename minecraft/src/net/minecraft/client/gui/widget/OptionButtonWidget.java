package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOption;

@Environment(EnvType.CLIENT)
public abstract class OptionButtonWidget extends ButtonWidget {
	private final GameOption option;

	protected OptionButtonWidget(int i, int j, int k, int l, GameOption gameOption, String string) {
		super(i, j, k, l, string);
		this.option = gameOption;
	}
}
