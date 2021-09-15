package net.minecraft.util.math;

import com.google.common.math.Quantiles.ScaleAndIndexes;
import it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.util.Util;

public class Quantiles {
	public static final ScaleAndIndexes QUANTILE_POINTS = com.google.common.math.Quantiles.scale(100).indexes(50, 75, 90, 99);

	private Quantiles() {
	}

	public static Map<Integer, Double> create(long[] values) {
		return values.length == 0 ? Map.of() : reverseMap(QUANTILE_POINTS.compute(values));
	}

	public static Map<Integer, Double> create(double[] values) {
		return values.length == 0 ? Map.of() : reverseMap(QUANTILE_POINTS.compute(values));
	}

	private static Map<Integer, Double> reverseMap(Map<Integer, Double> map) {
		Int2DoubleSortedMap int2DoubleSortedMap = Util.make(new Int2DoubleRBTreeMap(Comparator.reverseOrder()), reversedMap -> reversedMap.putAll(map));
		return Int2DoubleSortedMaps.unmodifiable(int2DoubleSortedMap);
	}
}
