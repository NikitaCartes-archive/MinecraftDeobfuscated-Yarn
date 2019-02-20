package net.minecraft;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class class_4064 extends GameOption {
	private final BiConsumer<GameOptions, Integer> field_18169;
	private final BiFunction<GameOptions, class_4064, String> field_18170;

	public class_4064(String string, BiConsumer<GameOptions, Integer> biConsumer, BiFunction<GameOptions, class_4064, String> biFunction) {
		super(string);
		this.field_18169 = biConsumer;
		this.field_18170 = biFunction;
	}

	public void method_18500(GameOptions gameOptions, int i) {
		this.field_18169.accept(gameOptions, i);
		gameOptions.write();
	}

	@Override
	public ButtonWidget method_18520(GameOptions gameOptions, int i, int j, int k) {
		return new OptionButtonWidget(i, j, k, 20, this, this.method_18501(gameOptions)) {
			@Override
			public void onPressed(double d, double e) {
				class_4064.this.method_18500(gameOptions, 1);
				this.setText(class_4064.this.method_18501(gameOptions));
			}
		};
	}

	public String method_18501(GameOptions gameOptions) {
		return (String)this.field_18170.apply(gameOptions, this);
	}
}
