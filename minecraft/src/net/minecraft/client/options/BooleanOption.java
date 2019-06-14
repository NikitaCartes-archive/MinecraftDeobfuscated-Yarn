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

	public void method_18492(GameOptions gameOptions, String string) {
		this.method_18493(gameOptions, "true".equals(string));
	}

	public void method_18491(GameOptions gameOptions) {
		this.method_18493(gameOptions, !this.method_18494(gameOptions));
		gameOptions.write();
	}

	private void method_18493(GameOptions gameOptions, boolean bl) {
		this.setter.accept(gameOptions, bl);
	}

	public boolean method_18494(GameOptions gameOptions) {
		return this.getter.test(gameOptions);
	}

	@Override
	public AbstractButtonWidget method_18520(GameOptions gameOptions, int i, int j, int k) {
		return new OptionButtonWidget(i, j, k, 20, this, this.method_18495(gameOptions), buttonWidget -> {
			this.method_18491(gameOptions);
			buttonWidget.setMessage(this.method_18495(gameOptions));
		});
	}

	public String method_18495(GameOptions gameOptions) {
		return this.getDisplayPrefix() + I18n.translate(this.method_18494(gameOptions) ? "options.on" : "options.off");
	}
}
