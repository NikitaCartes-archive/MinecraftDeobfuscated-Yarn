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
		String key,
		double min,
		double max,
		float step,
		Function<GameOptions, Double> getter,
		BiConsumer<GameOptions, Double> setter,
		BiFunction<GameOptions, DoubleOption, String> displayStringGetter
	) {
		super(key);
		this.min = min;
		this.max = max;
		this.step = step;
		this.getter = getter;
		this.setter = setter;
		this.displayStringGetter = displayStringGetter;
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
		return new GameOptionSliderWidget(options, x, y, width, 20, this);
	}

	public double method_18611(double d) {
		return MathHelper.clamp((this.method_18618(d) - this.min) / (this.max - this.min), 0.0, 1.0);
	}

	public double method_18616(double d) {
		return this.method_18618(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.min, this.max));
	}

	private double method_18618(double d) {
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

	public void setMax(float max) {
		this.max = (double)max;
	}

	public void set(GameOptions options, double value) {
		this.setter.accept(options, value);
	}

	public double get(GameOptions options) {
		return (Double)this.getter.apply(options);
	}

	public String getDisplayString(GameOptions options) {
		return (String)this.displayStringGetter.apply(options, this);
	}
}
