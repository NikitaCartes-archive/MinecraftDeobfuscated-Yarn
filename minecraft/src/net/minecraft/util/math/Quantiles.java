package net.minecraft.util.math;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.Quantiles.ScaleAndIndexes;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Quantiles {
	public static final ScaleAndIndexes QUANTILE_POINTS = com.google.common.math.Quantiles.scale(100).indexes(50, 75, 90, 99);

	private Quantiles() {
	}

	public static Map<Integer, Double> create(long[] values) {
		return (Map<Integer, Double>)(values.length == 0 ? ImmutableMap.of() : reverseMap(QUANTILE_POINTS.compute(values)));
	}

	public static Map<Integer, Double> create(double[] values) {
		return (Map<Integer, Double>)(values.length == 0 ? ImmutableMap.of() : reverseMap(QUANTILE_POINTS.compute(values)));
	}

	private static Map<Integer, Double> reverseMap(Map<Integer, Double> map) {
		TreeMap<Integer, Double> treeMap = new TreeMap(Comparator.reverseOrder());
		treeMap.putAll(map);
		return treeMap;
	}
}
