package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;

@Environment(EnvType.CLIENT)
public class StringGameOption extends GameOption {
	private final BiConsumer<GameOptions, Integer> field_18169;
	private final BiFunction<GameOptions, StringGameOption, String> field_18170;

	public StringGameOption(String string, BiConsumer<GameOptions, Integer> biConsumer, BiFunction<GameOptions, StringGameOption, String> biFunction) {
		super(string);
		this.field_18169 = biConsumer;
		this.field_18170 = biFunction;
	}

	public void method_18500(GameOptions gameOptions, int i) {
		this.field_18169.accept(gameOptions, i);
		gameOptions.write();
	}

	@Override
	public ButtonWidget createOptionButton(GameOptions gameOptions, int i, int j, int k) {
		return new OptionButtonWidget(i, j, k, 20, this, this.method_18501(gameOptions)) {
			@Override
			public void onPressed(double d, double e) {
				StringGameOption.this.method_18500(gameOptions, 1);
				this.setText(StringGameOption.this.method_18501(gameOptions));
			}
		};
	}

	public String method_18501(GameOptions gameOptions) {
		return (String)this.field_18170.apply(gameOptions, this);
	}
}
