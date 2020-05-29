package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public class class_5324 {
	private static Function<Integer, DataResult<Integer>> method_29232(int i, int j) {
		return integer -> integer >= i && integer <= j
				? DataResult.success(integer)
				: DataResult.error("Value " + integer + " outside of range [" + i + ":" + j + "]", integer);
	}

	public static Codec<Integer> method_29229(int i, int j) {
		Function<Integer, DataResult<Integer>> function = method_29232(i, j);
		return Codec.INT.flatXmap(function, function);
	}

	private static Function<Double, DataResult<Double>> method_29231(double d, double e) {
		return double_ -> double_ >= d && double_ <= e
				? DataResult.success(double_)
				: DataResult.error("Value " + double_ + " outside of range [" + d + ":" + e + "]", double_);
	}

	public static Codec<Double> method_29227(double d, double e) {
		Function<Double, DataResult<Double>> function = method_29231(d, e);
		return Codec.DOUBLE.flatXmap(function, function);
	}
}
