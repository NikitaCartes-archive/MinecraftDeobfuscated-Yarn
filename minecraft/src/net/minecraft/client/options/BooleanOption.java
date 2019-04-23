package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class BooleanOption extends Option {
	private final Predicate<GameOptions> getter;
	private final BiConsumer<GameOptions, Boolean> setter;

	public BooleanOption(String string, Predicate<GameOptions> predicate, BiConsumer<GameOptions, Boolean> biConsumer) {
		super(string);
		this.getter = predicate;
		this.setter = biConsumer;
	}

	public void set(GameOptions gameOptions, String string) {
		this.set(gameOptions, "true".equals(string));
	}

	public void set(GameOptions gameOptions) {
		this.set(gameOptions, !this.get(gameOptions));
		gameOptions.write();
	}

	private void set(GameOptions gameOptions, boolean bl) {
		this.setter.accept(gameOptions, bl);
	}

	public boolean get(GameOptions gameOptions) {
		return this.getter.test(gameOptions);
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions gameOptions, int i, int j, int k) {
		return new OptionButtonWidget(i, j, k, 20, this, this.getDisplayString(gameOptions), buttonWidget -> {
			this.set(gameOptions);
			buttonWidget.setMessage(this.getDisplayString(gameOptions));
		});
	}

	public String getDisplayString(GameOptions gameOptions) {
		return this.getDisplayPrefix() + I18n.translate(this.get(gameOptions) ? "options.on" : "options.off");
	}
}
