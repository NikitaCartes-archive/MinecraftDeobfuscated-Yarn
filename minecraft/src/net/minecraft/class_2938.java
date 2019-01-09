package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class class_2938 extends class_2918 {
	public class_2938(Function<Dynamic<?>, ? extends class_3133> function) {
		super(function);
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
			class_2246.field_10543
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
		return class_2936.method_16138(this, arg, bitSet, random, arg2, i, j, k, l, m, n, o, p);
	}
}
