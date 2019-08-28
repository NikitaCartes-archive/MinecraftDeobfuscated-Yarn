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
public class DoubleOption extends Option {
	protected final float step;
	protected final double min;
	protected double max;
	private final Function<GameOptions, Double> getter;
	private final BiConsumer<GameOptions, Double> setter;
	private final BiFunction<GameOptions, DoubleOption, String> displayStringGetter;

	public DoubleOption(
		String string,
		double d,
		double e,
		float f,
		Function<GameOptions, Double> function,
		BiConsumer<GameOptions, Double> biConsumer,
		BiFunction<GameOptions, DoubleOption, String> biFunction
	) {
		super(string);
		this.min = d;
		this.max = e;
		this.step = f;
		this.getter = function;
		this.setter = biConsumer;
		this.displayStringGetter = biFunction;
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions gameOptions, int i, int j, int k) {
		return new GameOptionSliderWidget(gameOptions, i, j, k, 20, this);
	}

	public double getRatio(double d) {
		return MathHelper.clamp((this.adjust(d) - this.min) / (this.max - this.min), 0.0, 1.0);
	}

	public double getValue(double d) {
		return this.adjust(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.min, this.max));
	}

	private double adjust(double d) {
		if (this.step > 0.0F) {
			d = (double)(this.step * (float)Math.round(d / (double)this.step));
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
