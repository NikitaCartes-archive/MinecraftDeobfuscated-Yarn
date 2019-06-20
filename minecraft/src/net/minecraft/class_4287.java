package net.minecraft;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4287 extends class_4067 {
	public class_4287(
		String string,
		double d,
		double e,
		float f,
		Function<class_315, Double> function,
		BiConsumer<class_315, Double> biConsumer,
		BiFunction<class_315, class_4067, String> biFunction
	) {
		super(string, d, e, f, function, biConsumer, biFunction);
	}

	@Override
	public double method_18611(double d) {
		return Math.log(d / this.field_18205) / Math.log(this.field_18206 / this.field_18205);
	}

	@Override
	public double method_18616(double d) {
		return this.field_18205 * Math.pow(Math.E, Math.log(this.field_18206 / this.field_18205) * d);
	}
}
