package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

public class class_2551 extends class_2478 {
	public static final class_2753 field_11726 = class_2383.field_11177;
	private static final Map<class_2350, class_265> field_11727 = Maps.newEnumMap(
		ImmutableMap.of(
			class_2350.field_11043,
			class_2248.method_9541(0.0, 4.5, 14.0, 16.0, 12.5, 16.0),
			class_2350.field_11035,
			class_2248.method_9541(0.0, 4.5, 0.0, 16.0, 12.5, 2.0),
			class_2350.field_11034,
			class_2248.method_9541(0.0, 4.5, 0.0, 2.0, 12.5, 16.0),
			class_2350.field_11039,
			class_2248.method_9541(14.0, 4.5, 0.0, 16.0, 12.5, 16.0)
		)
	);

	public class_2551(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11726, class_2350.field_11043).method_11657(field_11491, Boolean.valueOf(false)));
	}

	@Override
	public String method_9539() {
		return this.method_8389().method_7876();
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return (class_265)field_11727.get(arg.method_11654(field_11726));
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return arg2.method_8320(arg3.method_10093(((class_2350)arg.method_11654(field_11726)).method_10153())).method_11620().method_15799();
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = this.method_9564();
		class_3610 lv2 = arg.method_8045().method_8316(arg.method_8037());
		class_1941 lv3 = arg.method_8045();
		class_2338 lv4 = arg.method_8037();
		class_2350[] lvs = arg.method_7718();

		for (class_2350 lv5 : lvs) {
			if (lv5.method_10166().method_10179()) {
				class_2350 lv6 = lv5.method_10153();
				lv = lv.method_11657(field_11726, lv6);
				if (lv.method_11591(lv3, lv4)) {
					return lv.method_11657(field_11491, Boolean.valueOf(lv2.method_15772() == class_3612.field_15910));
				}
			}
		}

		return null;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2.method_10153() == arg.method_11654(field_11726) && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11726, arg2.method_10503(arg.method_11654(field_11726)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11726)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11726, field_11491);
	}
}
