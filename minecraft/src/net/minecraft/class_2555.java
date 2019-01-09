package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2555 extends class_2527 {
	public static final class_2753 field_11731 = class_2383.field_11177;
	private static final Map<class_2350, class_265> field_11732 = Maps.newEnumMap(
		ImmutableMap.of(
			class_2350.field_11043,
			class_2248.method_9541(5.5, 3.0, 11.0, 10.5, 13.0, 16.0),
			class_2350.field_11035,
			class_2248.method_9541(5.5, 3.0, 0.0, 10.5, 13.0, 5.0),
			class_2350.field_11039,
			class_2248.method_9541(11.0, 3.0, 5.5, 16.0, 13.0, 10.5),
			class_2350.field_11034,
			class_2248.method_9541(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)
		)
	);

	protected class_2555(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11731, class_2350.field_11043));
	}

	@Override
	public String method_9539() {
		return this.method_8389().method_7876();
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return method_10841(arg);
	}

	public static class_265 method_10841(class_2680 arg) {
		return (class_265)field_11732.get(arg.method_11654(field_11731));
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2350 lv = arg.method_11654(field_11731);
		class_2338 lv2 = arg3.method_10093(lv.method_10153());
		class_2680 lv3 = arg2.method_8320(lv2);
		return class_2248.method_9501(lv3.method_11628(arg2, lv2), lv) && !method_9581(lv3.method_11614());
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = this.method_9564();
		class_1941 lv2 = arg.method_8045();
		class_2338 lv3 = arg.method_8037();
		class_2350[] lvs = arg.method_7718();

		for (class_2350 lv4 : lvs) {
			if (lv4.method_10166().method_10179()) {
				class_2350 lv5 = lv4.method_10153();
				lv = lv.method_11657(field_11731, lv5);
				if (lv.method_11591(lv2, lv3)) {
					return lv;
				}
			}
		}

		return null;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2.method_10153() == arg.method_11654(field_11731) && !arg.method_11591(arg4, arg5) ? class_2246.field_10124.method_9564() : arg;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2350 lv = arg.method_11654(field_11731);
		double d = (double)arg3.method_10263() + 0.5;
		double e = (double)arg3.method_10264() + 0.7;
		double f = (double)arg3.method_10260() + 0.5;
		double g = 0.22;
		double h = 0.27;
		class_2350 lv2 = lv.method_10153();
		arg2.method_8406(class_2398.field_11251, d + 0.27 * (double)lv2.method_10148(), e + 0.22, f + 0.27 * (double)lv2.method_10165(), 0.0, 0.0, 0.0);
		arg2.method_8406(class_2398.field_11240, d + 0.27 * (double)lv2.method_10148(), e + 0.22, f + 0.27 * (double)lv2.method_10165(), 0.0, 0.0, 0.0);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11731, arg2.method_10503(arg.method_11654(field_11731)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11731)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11731);
	}
}
