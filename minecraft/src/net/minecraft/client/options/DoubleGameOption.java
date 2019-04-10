package net.minecraft.client.options;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.GameOptionSliderWidget;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DoubleGameOption extends GameOption {
	private final float interval;
	private final double min;
	private double max;
	private final Function<GameOptions, Double> getter;
	private final BiConsumer<GameOptions, Double> setter;
	private final BiFunction<GameOptions, DoubleGameOption, String> displayStringGetter;

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
		this.min = d;
		this.max = e;
		this.interval = f;
		this.getter = function;
		this.setter = biConsumer;
		this.displayStringGetter = biFunction;
	}

	@Override
	public AbstractButtonWidget createOptionButton(GameOptions gameOptions, int i, int j, int k) {
		return new GameOptionSliderWidget(gameOptions, i, j, k, 20, this);
	}

	public double method_18611(double d) {
		return MathHelper.clamp((this.method_18618(d) - this.min) / (this.max - this.min), 0.0, 1.0);
	}

	public double method_18616(double d) {
		return this.method_18618(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.min, this.max));
	}

	private double method_18618(double d) {
		if (this.interval > 0.0F) {
			d = (double)(this.interval * (float)Math.round(d / (double)this.interval));
		}

		return MathHelper.clamp(d, this.min, this.max);
	}

	public double getMin() {
		return this.min;
	}

	public double getMax() {
		return this.max;
	}

	public void setMax(float f) {
		this.max = (double)f;
	}

	public void set(GameOptions gameOptions, double d) {
		this.setter.accept(gameOptions, d);
	}

	public double get(GameOptions gameOptions) {
		return (Double)this.getter.apply(gameOptions);
	}

	public String getDisplayString(GameOptions gameOptions) {
		return (String)this.displayStringGetter.apply(gameOptions, this);
	}
}
