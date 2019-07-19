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

	public CyclingOption(String key, BiConsumer<GameOptions, Integer> setter, BiFunction<GameOptions, CyclingOption, String> messageProvider) {
		super(key);
		this.setter = setter;
		this.messageProvider = messageProvider;
	}

	public void cycle(GameOptions options, int amount) {
		this.setter.accept(options, amount);
		options.write();
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
		return new OptionButtonWidget(x, y, width, 20, this, this.getMessage(options), buttonWidget -> {
			this.cycle(options, 1);
			buttonWidget.setMessage(this.getMessage(options));
		});
	}

	public String getMessage(GameOptions options) {
		return (String)this.messageProvider.apply(options, this);
	}
}
