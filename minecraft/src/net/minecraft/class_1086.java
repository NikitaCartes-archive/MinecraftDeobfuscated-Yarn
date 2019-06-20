package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_1086 implements class_3665 {
	field_5350(0, 0),
	field_5366(0, 90),
	field_5355(0, 180),
	field_5347(0, 270),
	field_5351(90, 0),
	field_5360(90, 90),
	field_5367(90, 180),
	field_5354(90, 270),
	field_5358(180, 0),
	field_5348(180, 90),
	field_5356(180, 180),
	field_5359(180, 270),
	field_5353(270, 0),
	field_5349(270, 90),
	field_5361(270, 180),
	field_5352(270, 270);

	private static final Map<Integer, class_1086> field_5357 = (Map<Integer, class_1086>)Arrays.stream(values())
		.sorted(Comparator.comparingInt(arg -> arg.field_5364))
		.collect(Collectors.toMap(arg -> arg.field_5364, arg -> arg));
	private final int field_5364;
	private final class_1158 field_5368;
	private final int field_5363;
	private final int field_5362;

	private static int method_4703(int i, int j) {
		return i * 360 + j;
	}

	private class_1086(int j, int k) {
		this.field_5364 = method_4703(j, k);
		class_1158 lv = new class_1158(new class_1160(0.0F, 1.0F, 0.0F), (float)(-k), true);
		lv.method_4925(new class_1158(new class_1160(1.0F, 0.0F, 0.0F), (float)(-j), true));
		this.field_5368 = lv;
		this.field_5363 = class_3532.method_15382(j / 90);
		this.field_5362 = class_3532.method_15382(k / 90);
	}

	@Override
	public class_1086 method_3509() {
		return this;
	}

	public class_1158 method_4704() {
		return this.field_5368;
	}

	public class_2350 method_4705(class_2350 arg) {
		class_2350 lv = arg;

		for (int i = 0; i < this.field_5363; i++) {
			lv = lv.method_10152(class_2350.class_2351.field_11048);
		}

		if (lv.method_10166() != class_2350.class_2351.field_11052) {
			for (int i = 0; i < this.field_5362; i++) {
				lv = lv.method_10152(class_2350.class_2351.field_11052);
			}
		}

		return lv;
	}

	public int method_4706(class_2350 arg, int i) {
		int j = i;
		if (arg.method_10166() == class_2350.class_2351.field_11048) {
			j = (i + this.field_5363) % 4;
		}

		class_2350 lv = arg;

		for (int k = 0; k < this.field_5363; k++) {
			lv = lv.method_10152(class_2350.class_2351.field_11048);
		}

		if (lv.method_10166() == class_2350.class_2351.field_11052) {
			j = (j + this.field_5362) % 4;
		}

		return j;
	}

	public static class_1086 method_4699(int i, int j) {
		return (class_1086)field_5357.get(method_4703(class_3532.method_15387(i, 360), class_3532.method_15387(j, 360)));
	}
}
