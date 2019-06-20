package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2195 extends class_2261 {
	public static final class_2753 field_9873 = class_2383.field_11177;
	private final class_2511 field_9875;
	private static final Map<class_2350, class_265> field_9874 = Maps.newEnumMap(
		ImmutableMap.of(
			class_2350.field_11035,
			class_2248.method_9541(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
			class_2350.field_11039,
			class_2248.method_9541(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
			class_2350.field_11043,
			class_2248.method_9541(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
			class_2350.field_11034,
			class_2248.method_9541(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
		)
	);

	protected class_2195(class_2511 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9873, class_2350.field_11043));
		this.field_9875 = arg;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return (class_265)field_9874.get(arg.method_11654(field_9873));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg3.method_11614() != this.field_9875 && arg2 == arg.method_11654(field_9873)
			? this.field_9875.method_10679().method_9564().method_11657(class_2513.field_11584, Integer.valueOf(7))
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11614() == class_2246.field_10362;
	}

	@Environment(EnvType.CLIENT)
	protected class_1792 method_9337() {
		if (this.field_9875 == class_2246.field_10261) {
			return class_1802.field_8706;
		} else {
			return this.field_9875 == class_2246.field_10545 ? class_1802.field_8188 : class_1802.field_8162;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(this.method_9337());
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_9873, arg2.method_10503(arg.method_11654(field_9873)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_9873)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_9873);
	}
}
