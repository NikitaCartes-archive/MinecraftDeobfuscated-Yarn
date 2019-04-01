package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_324 {
	private final class_2361<class_322> field_1995 = new class_2361<>(32);

	public static class_324 method_1689() {
		class_324 lv = new class_324();
		lv.method_1690(
			(arg, arg2, arg3, i) -> arg2 != null && arg3 != null
					? class_1163.method_4962(arg2, arg.method_11654(class_2475.field_11484) == class_2756.field_12609 ? arg3.method_10074() : arg3)
					: -1,
			class_2246.field_10313,
			class_2246.field_10214
		);
		lv.method_1690(
			(arg, arg2, arg3, i) -> arg2 != null && arg3 != null ? class_1163.method_4962(arg2, arg3) : class_1933.method_8377(0.5, 1.0),
			class_2246.field_10219,
			class_2246.field_10112,
			class_2246.field_10479,
			class_2246.field_10128
		);
		lv.method_1690((arg, arg2, arg3, i) -> class_1926.method_8342(), class_2246.field_9988);
		lv.method_1690((arg, arg2, arg3, i) -> class_1926.method_8343(), class_2246.field_10539);
		lv.method_1690(
			(arg, arg2, arg3, i) -> arg2 != null && arg3 != null ? class_1163.method_4966(arg2, arg3) : class_1926.method_8341(),
			class_2246.field_10503,
			class_2246.field_10335,
			class_2246.field_10098,
			class_2246.field_10035,
			class_2246.field_10597
		);
		lv.method_1690(
			(arg, arg2, arg3, i) -> arg2 != null && arg3 != null ? class_1163.method_4961(arg2, arg3) : -1,
			class_2246.field_10382,
			class_2246.field_10422,
			class_2246.field_10593
		);
		lv.method_1690((arg, arg2, arg3, i) -> class_2457.method_10487((Integer)arg.method_11654(class_2457.field_11432)), class_2246.field_10091);
		lv.method_1690((arg, arg2, arg3, i) -> arg2 != null && arg3 != null ? class_1163.method_4962(arg2, arg3) : -1, class_2246.field_10424);
		lv.method_1690((arg, arg2, arg3, i) -> 14731036, class_2246.field_10150, class_2246.field_10331);
		lv.method_1690((arg, arg2, arg3, i) -> {
			int j = (Integer)arg.method_11654(class_2513.field_11584);
			int k = j * 32;
			int l = 255 - j * 8;
			int m = j * 4;
			return k << 16 | l << 8 | m;
		}, class_2246.field_10168, class_2246.field_9984);
		lv.method_1690((arg, arg2, arg3, i) -> arg2 != null && arg3 != null ? 2129968 : 7455580, class_2246.field_10588);
		return lv;
	}

	public int method_1691(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		class_322 lv = this.field_1995.method_10200(class_2378.field_11146.method_10249(arg.method_11614()));
		if (lv != null) {
			return lv.getColor(arg, null, null, 0);
		} else {
			class_3620 lv2 = arg.method_11625(arg2, arg3);
			return lv2 != null ? lv2.field_16011 : -1;
		}
	}

	public int method_1697(class_2680 arg, @Nullable class_1920 arg2, @Nullable class_2338 arg3, int i) {
		class_322 lv = this.field_1995.method_10200(class_2378.field_11146.method_10249(arg.method_11614()));
		return lv == null ? -1 : lv.getColor(arg, arg2, arg3, i);
	}

	public void method_1690(class_322 arg, class_2248... args) {
		for (class_2248 lv : args) {
			this.field_1995.method_10203(arg, class_2378.field_11146.method_10249(lv));
		}
	}
}
