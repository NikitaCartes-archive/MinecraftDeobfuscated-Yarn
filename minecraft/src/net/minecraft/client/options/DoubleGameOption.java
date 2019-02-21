package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GameOptionSliderWidget;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DoubleGameOption extends GameOption {
	private final float field_18204;
	private final double field_18205;
	private double field_18206;
	private final Function<GameOptions, Double> field_18207;
	private final BiConsumer<GameOptions, Double> field_18208;
	private final BiFunction<GameOptions, DoubleGameOption, String> field_18209;

	public DoubleGameOption(
		String string,
		double d,
		double e,
		float f,
		Function<GameOptions, Double> function,
		BiConsumer<GameOptions, Double> biConsumer,
		BiFunction<GameOptions, DoubleGameOption, String> biFunction
	) {
		super(string);
		this.field_18205 = d;
		this.field_18206 = e;
		this.field_18204 = f;
		this.field_18207 = function;
		this.field_18208 = biConsumer;
		this.field_18209 = biFunction;
	}

	@Override
	public ButtonWidget createOptionButton(GameOptions gameOptions, int i, int j, int k) {
		return new GameOptionSliderWidget(gameOptions, i, j, k, 20, this);
	}

	public double method_18611(double d) {
		return MathHelper.clamp((this.method_18618(d) - this.field_18205) / (this.field_18206 - this.field_18205), 0.0, 1.0);
	}

	public double method_18616(double d) {
		return this.method_18618(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.field_18205, this.field_18206));
	}

	private double method_18618(double d) {
		if (this.field_18204 > 0.0F) {
			d = (double)(this.field_18204 * (float)Math.round(d / (double)this.field_18204));
		}

		return MathHelper.clamp(d, this.field_18205, this.field_18206);
	}

	public double method_18615() {
		return this.field_18205;
	}

	public double method_18617() {
		return this.field_18206;
	}

	public void method_18612(float f) {
		this.field_18206 = (double)f;
	}

	public void method_18614(GameOptions gameOptions, double d) {
		this.field_18208.accept(gameOptions, d);
	}

	public double method_18613(GameOptions gameOptions) {
		return (Double)this.field_18207.apply(gameOptions);
	}

	public String method_18619(GameOptions gameOptions) {
		return (String)this.field_18209.apply(gameOptions, this);
	}
}
