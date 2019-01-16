package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public abstract class OptionButtonWidget extends ButtonWidget {
	@Nullable
	private final GameOptions.Option option;

	public OptionButtonWidget(int i, int j, int k, String string) {
		this(i, j, k, null, string);
	}

	public OptionButtonWidget(int i, int j, int k, @Nullable GameOptions.Option option, String string) {
		this(i, j, k, 150, 20, option, string);
	}

	public OptionButtonWidget(int i, int j, int k, int l, int m, @Nullable GameOptions.Option option, String string) {
		super(i, j, k, l, m, string);
		this.option = option;
	}

	@Nullable
	public GameOptions.Option getOption() {
		return this.option;
	}
}
