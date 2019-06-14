package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;

@Environment(EnvType.CLIENT)
public class CyclingOption extends Option {
	private final BiConsumer<GameOptions, Integer> setter;
	private final BiFunction<GameOptions, CyclingOption, String> messageProvider;

	public CyclingOption(String string, BiConsumer<GameOptions, Integer> biConsumer, BiFunction<GameOptions, CyclingOption, String> biFunction) {
		super(string);
		this.setter = biConsumer;
		this.messageProvider = biFunction;
	}

	public void method_18500(GameOptions gameOptions, int i) {
		this.setter.accept(gameOptions, i);
		gameOptions.write();
	}

	@Override
	public AbstractButtonWidget method_18520(GameOptions gameOptions, int i, int j, int k) {
		return new OptionButtonWidget(i, j, k, 20, this, this.method_18501(gameOptions), buttonWidget -> {
			this.method_18500(gameOptions, 1);
			buttonWidget.setMessage(this.method_18501(gameOptions));
		});
	}

	public String method_18501(GameOptions gameOptions) {
		return (String)this.messageProvider.apply(gameOptions, this);
	}
}
