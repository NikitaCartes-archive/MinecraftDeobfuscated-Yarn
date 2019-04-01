package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class class_2936 extends class_2925 {
	public class_2936(Function<Dynamic<?>, ? extends class_3133> function) {
		super(function, 256);
		this.field_13302 = ImmutableSet.of(
			class_2246.field_10340,
			class_2246.field_10474,
			class_2246.field_10508,
			class_2246.field_10115,
			class_2246.field_10566,
			class_2246.field_10253,
			class_2246.field_10520,
			class_2246.field_10219,
			class_2246.field_10415,
			class_2246.field_10611,
			class_2246.field_10184,
			class_2246.field_10015,
			class_2246.field_10325,
			class_2246.field_10143,
			class_2246.field_10014,
			class_2246.field_10444,
			class_2246.field_10349,
			class_2246.field_10590,
			class_2246.field_10235,
			class_2246.field_10570,
			class_2246.field_10409,
			class_2246.field_10123,
			class_2246.field_10526,
			class_2246.field_10328,
			class_2246.field_10626,
			class_2246.field_9979,
			class_2246.field_10344,
			class_2246.field_10402,
			class_2246.field_10477,
			class_2246.field_10102,
			class_2246.field_10255,
			class_2246.field_10382,
			class_2246.field_10164,
			class_2246.field_10540,
			class_2246.field_10124,
			class_2246.field_10543,
			class_2246.field_10225
		);
	}

	@Override
	protected boolean method_12711(class_2791 arg, int i, int j, int k, int l, int m, int n, int o, int p) {
		return false;
	}

	@Override
	protected boolean method_16581(
		class_2791 arg,
		BitSet bitSet,
		Random random,
		class_2338.class_2339 arg2,
		class_2338.class_2339 arg3,
		class_2338.class_2339 arg4,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		int o,
		int p,
		AtomicBoolean atomicBoolean
	) {
		return method_16138(this, arg, bitSet, random, arg2, i, j, k, l, m, n, o, p);
	}

	protected static boolean method_16138(
		class_2939<?> arg, class_2791 arg2, BitSet bitSet, Random random, class_2338.class_2339 arg3, int i, int j, int k, int l, int m, int n, int o, int p
	) {
		if (o >= i) {
			return false;
		} else {
			int q = n | p << 4 | o << 8;
			if (bitSet.get(q)) {
				return false;
			} else {
				bitSet.set(q);
				arg3.method_10103(l, o, m);
				class_2680 lv = arg2.method_8320(arg3);
				if (!arg.method_12709(lv)) {
					return false;
				} else if (o == 10) {
					float f = random.nextFloat();
					if ((double)f < 0.25) {
						arg2.method_12010(arg3, class_2246.field_10092.method_9564(), false);
						arg2.method_12013().method_8676(arg3, class_2246.field_10092, 0);
					} else {
						arg2.method_12010(arg3, class_2246.field_10540.method_9564(), false);
					}

					return true;
				} else if (o < 10) {
					arg2.method_12010(arg3, class_2246.field_10164.method_9564(), false);
					return false;
				} else {
					boolean bl = false;

					for (class_2350 lv2 : class_2350.class_2353.field_11062) {
						int r = l + lv2.method_10148();
						int s = m + lv2.method_10165();
						if (r >> 4 != j || s >> 4 != k || arg2.method_8320(arg3.method_10103(r, o, s)).method_11588()) {
							arg2.method_12010(arg3, field_13305.method_15759(), false);
							arg2.method_12014().method_8676(arg3, field_13305.method_15772(), 0);
							bl = true;
							break;
						}
					}

					arg3.method_10103(l, o, m);
					if (!bl) {
						arg2.method_12010(arg3, field_13305.method_15759(), false);
						return true;
					} else {
						return true;
					}
				}
			}
		}
	}
}
