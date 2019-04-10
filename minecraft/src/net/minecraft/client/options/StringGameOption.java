package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;

@Environment(EnvType.CLIENT)
public class StringGameOption extends GameOption {
	private final BiConsumer<GameOptions, Integer> getter;
	private final BiFunction<GameOptions, StringGameOption, String> setter;

	public StringGameOption(String string, BiConsumer<GameOptions, Integer> biConsumer, BiFunction<GameOptions, StringGameOption, String> biFunction) {
		super(string);
		this.getter = biConsumer;
		this.setter = biFunction;
	}

	public void method_18500(GameOptions gameOptions, int i) {
		this.getter.accept(gameOptions, i);
		gameOptions.write();
	}

	@Override
	public AbstractButtonWidget createOptionButton(GameOptions gameOptions, int i, int j, int k) {
		return new OptionButtonWidget(i, j, k, 20, this, this.get(gameOptions), buttonWidget -> {
			this.method_18500(gameOptions, 1);
			buttonWidget.setMessage(this.get(gameOptions));
		});
	}

	public String get(GameOptions gameOptions) {
		return (String)this.setter.apply(gameOptions, this);
	}
}
