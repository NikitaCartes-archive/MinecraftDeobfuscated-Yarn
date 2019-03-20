package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class BooleanGameOption extends GameOption {
	private final Predicate<GameOptions> field_18158;
	private final BiConsumer<GameOptions, Boolean> field_18159;

	public BooleanGameOption(String string, Predicate<GameOptions> predicate, BiConsumer<GameOptions, Boolean> biConsumer) {
		super(string);
		this.field_18158 = predicate;
		this.field_18159 = biConsumer;
	}

	public void method_18492(GameOptions gameOptions, String string) {
		this.method_18493(gameOptions, "true".equals(string));
	}

	public void method_18491(GameOptions gameOptions) {
		this.method_18493(gameOptions, !this.method_18494(gameOptions));
		gameOptions.write();
	}

	private void method_18493(GameOptions gameOptions, boolean bl) {
		this.field_18159.accept(gameOptions, bl);
	}

	public boolean method_18494(GameOptions gameOptions) {
		return this.field_18158.test(gameOptions);
	}

	@Override
	public AbstractButtonWidget createOptionButton(GameOptions gameOptions, int i, int j, int k) {
		return new OptionButtonWidget(i, j, k, 20, this, this.method_18495(gameOptions)) {
			@Override
			public void onPressed() {
				BooleanGameOption.this.method_18491(gameOptions);
				this.setMessage(BooleanGameOption.this.method_18495(gameOptions));
			}
		};
	}

	public String method_18495(GameOptions gameOptions) {
		return this.method_18518() + I18n.translate(this.method_18494(gameOptions) ? "options.on" : "options.off");
	}
}
