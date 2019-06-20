package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class class_2934 extends class_2925 {
	public class_2934(Function<Dynamic<?>, ? extends class_3133> function) {
		super(function, 128);
		this.field_13302 = ImmutableSet.of(
			class_2246.field_10340,
			class_2246.field_10474,
			class_2246.field_10508,
			class_2246.field_10115,
			class_2246.field_10566,
			class_2246.field_10253,
			class_2246.field_10520,
			class_2246.field_10219,
			class_2246.field_10515
		);
		this.field_13298 = ImmutableSet.of(class_3612.field_15908, class_3612.field_15910);
	}

	@Override
	protected int method_16577() {
		return 10;
	}

	@Override
	protected float method_16576(Random random) {
		return (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
	}

	@Override
	protected double method_16578() {
		return 5.0;
	}

	@Override
	protected int method_16579(Random random) {
		return random.nextInt(this.field_16653);
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
		int q = n | p << 4 | o << 8;
		if (bitSet.get(q)) {
			return false;
		} else {
			bitSet.set(q);
			arg2.method_10103(l, o, m);
			if (this.method_12709(arg.method_8320(arg2))) {
				class_2680 lv;
				if (o <= 31) {
					lv = field_13296.method_15759();
				} else {
					lv = field_13294;
				}

				arg.method_12010(arg2, lv, false);
				return true;
			} else {
				return false;
			}
		}
	}
}
