package net.minecraft.client.options;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DoubleOption extends Option {
	protected final float step;
	protected final double min;
	protected double max;
	private final Function<GameOptions, Double> getter;
	private final BiConsumer<GameOptions, Double> setter;
	private final BiFunction<GameOptions, DoubleOption, Text> displayStringGetter;
	private final Function<MinecraftClient, List<OrderedText>> field_27958;

	public DoubleOption(
		String key,
		double min,
		double max,
		float step,
		Function<GameOptions, Double> getter,
		BiConsumer<GameOptions, Double> setter,
		BiFunction<GameOptions, DoubleOption, Text> displayStringGetter,
		Function<MinecraftClient, List<OrderedText>> function
	) {
		super(key);
		this.min = min;
		this.max = max;
		this.step = step;
		this.getter = getter;
		this.setter = setter;
		this.displayStringGetter = displayStringGetter;
		this.field_27958 = function;
	}

	public DoubleOption(
		String key,
		double min,
		double max,
		float step,
		Function<GameOptions, Double> getter,
		BiConsumer<GameOptions, Double> setter,
		BiFunction<GameOptions, DoubleOption, Text> displayStringGetter
	) {
		this(key, min, max, step, getter, setter, displayStringGetter, client -> ImmutableList.of());
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
		List<OrderedText> list = (List<OrderedText>)this.field_27958.apply(MinecraftClient.getInstance());
		return new DoubleOptionSliderWidget(options, x, y, width, 20, this, list);
	}

	public double getRatio(double value) {
		return MathHelper.clamp((this.adjust(value) - this.min) / (this.max - this.min), 0.0, 1.0);
	}

	public double getValue(double ratio) {
		return this.adjust(MathHelper.lerp(MathHelper.clamp(ratio, 0.0, 1.0), this.min, this.max));
	}

	private double adjust(double value) {
		if (this.step > 0.0F) {
			value = (double)(this.step * (float)Math.round(value / (double)this.step));
		}

		return MathHelper.clamp(value, this.min, this.max);
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

	public Text getDisplayString(GameOptions options) {
		return (Text)this.displayStringGetter.apply(options, this);
	}
}
