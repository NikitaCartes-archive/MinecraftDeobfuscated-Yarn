package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3850 {
	private static final int[] field_18540 = new int[]{0, 10, 50, 100, 150};
	private final class_3854 field_17048;
	private final class_3852 field_17049;
	private final int field_17050;

	public class_3850(class_3854 arg, class_3852 arg2, int i) {
		this.field_17048 = arg;
		this.field_17049 = arg2;
		this.field_17050 = Math.max(1, i);
	}

	public class_3850(Dynamic<?> dynamic) {
		this(
			class_2378.field_17166.method_10223(class_2960.method_12829(dynamic.get("type").asString(""))),
			class_2378.field_17167.method_10223(class_2960.method_12829(dynamic.get("profession").asString(""))),
			dynamic.get("level").asInt(1)
		);
	}

	public class_3854 method_16919() {
		return this.field_17048;
	}

	public class_3852 method_16924() {
		return this.field_17049;
	}

	public int method_16925() {
		return this.field_17050;
	}

	public class_3850 method_16922(class_3854 arg) {
		return new class_3850(arg, this.field_17049, this.field_17050);
	}

	public class_3850 method_16921(class_3852 arg) {
		return new class_3850(this.field_17048, arg, this.field_17050);
	}

	public class_3850 method_16920(int i) {
		return new class_3850(this.field_17048, this.field_17049, i);
	}

	public <T> T method_16923(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("type"),
				dynamicOps.createString(class_2378.field_17166.method_10221(this.field_17048).toString()),
				dynamicOps.createString("profession"),
				dynamicOps.createString(class_2378.field_17167.method_10221(this.field_17049).toString()),
				dynamicOps.createString("level"),
				dynamicOps.createInt(this.field_17050)
			)
		);
	}

	@Environment(EnvType.CLIENT)
	public static int method_19194(int i) {
		return method_19196(i) ? field_18540[i - 1] : 0;
	}

	public static int method_19195(int i) {
		return method_19196(i) ? field_18540[i] : 0;
	}

	public static boolean method_19196(int i) {
		return i >= 1 && i < 5;
	}
}
