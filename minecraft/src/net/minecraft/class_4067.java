package net.minecraft;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4067 extends class_316 {
	private final float field_18204;
	private final double field_18205;
	private double field_18206;
	private final Function<class_315, Double> field_18207;
	private final BiConsumer<class_315, Double> field_18208;
	private final BiFunction<class_315, class_4067, String> field_18209;

	public class_4067(
		String string,
		double d,
		double e,
		float f,
		Function<class_315, Double> function,
		BiConsumer<class_315, Double> biConsumer,
		BiFunction<class_315, class_4067, String> biFunction
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
	public class_339 method_18520(class_315 arg, int i, int j, int k) {
		return new class_4040(arg, i, j, k, 20, this);
	}

	public double method_18611(double d) {
		return class_3532.method_15350((this.method_18618(d) - this.field_18205) / (this.field_18206 - this.field_18205), 0.0, 1.0);
	}

	public double method_18616(double d) {
		return this.method_18618(class_3532.method_16436(class_3532.method_15350(d, 0.0, 1.0), this.field_18205, this.field_18206));
	}

	private double method_18618(double d) {
		if (this.field_18204 > 0.0F) {
			d = (double)(this.field_18204 * (float)Math.round(d / (double)this.field_18204));
		}

		return class_3532.method_15350(d, this.field_18205, this.field_18206);
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

	public void method_18614(class_315 arg, double d) {
		this.field_18208.accept(arg, d);
	}

	public double method_18613(class_315 arg) {
		return (Double)this.field_18207.apply(arg);
	}

	public String method_18619(class_315 arg) {
		return (String)this.field_18209.apply(arg, this);
	}
}
