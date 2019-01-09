package net.minecraft;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_1740 implements class_1741 {
	field_7897("leather", 5, new int[]{1, 2, 3, 1}, 15, class_3417.field_14581, 0.0F, () -> class_1856.method_8091(class_1802.field_8745)),
	field_7887("chainmail", 15, new int[]{1, 4, 5, 2}, 12, class_3417.field_15191, 0.0F, () -> class_1856.method_8091(class_1802.field_8620)),
	field_7892("iron", 15, new int[]{2, 5, 6, 2}, 9, class_3417.field_14862, 0.0F, () -> class_1856.method_8091(class_1802.field_8620)),
	field_7895("gold", 7, new int[]{1, 3, 5, 2}, 25, class_3417.field_14761, 0.0F, () -> class_1856.method_8091(class_1802.field_8695)),
	field_7889("diamond", 33, new int[]{3, 6, 8, 3}, 10, class_3417.field_15103, 2.0F, () -> class_1856.method_8091(class_1802.field_8477)),
	field_7890("turtle", 25, new int[]{2, 5, 6, 2}, 9, class_3417.field_14684, 0.0F, () -> class_1856.method_8091(class_1802.field_8161));

	private static final int[] field_7891 = new int[]{13, 15, 16, 11};
	private final String field_7884;
	private final int field_7883;
	private final int[] field_7893;
	private final int field_7896;
	private final class_3414 field_7886;
	private final float field_7894;
	private final class_3528<class_1856> field_7885;

	private class_1740(String string2, int j, int[] is, int k, class_3414 arg, float f, Supplier<class_1856> supplier) {
		this.field_7884 = string2;
		this.field_7883 = j;
		this.field_7893 = is;
		this.field_7896 = k;
		this.field_7886 = arg;
		this.field_7894 = f;
		this.field_7885 = new class_3528<>(supplier);
	}

	@Override
	public int method_7696(class_1304 arg) {
		return field_7891[arg.method_5927()] * this.field_7883;
	}

	@Override
	public int method_7697(class_1304 arg) {
		return this.field_7893[arg.method_5927()];
	}

	@Override
	public int method_7699() {
		return this.field_7896;
	}

	@Override
	public class_3414 method_7698() {
		return this.field_7886;
	}

	@Override
	public class_1856 method_7695() {
		return this.field_7885.method_15332();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_7694() {
		return this.field_7884;
	}

	@Override
	public float method_7700() {
		return this.field_7894;
	}
}
