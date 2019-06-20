package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2215 extends class_2185 {
	public static final class_2758 field_9924 = class_2741.field_12532;
	private static final Map<class_1767, class_2248> field_9925 = Maps.<class_1767, class_2248>newHashMap();
	private static final class_265 field_9923 = class_2248.method_9541(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	public class_2215(class_1767 arg, class_2248.class_2251 arg2) {
		super(arg, arg2);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9924, Integer.valueOf(0)));
		field_9925.put(arg, this);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return arg2.method_8320(arg3.method_10074()).method_11620().method_15799();
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_9923;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564()
			.method_11657(field_9924, Integer.valueOf(class_3532.method_15357((double)((180.0F + arg.method_8044()) * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == class_2350.field_11033 && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_9924, Integer.valueOf(arg2.method_10502((Integer)arg.method_11654(field_9924), 16)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11657(field_9924, Integer.valueOf(arg2.method_10344((Integer)arg.method_11654(field_9924), 16)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_9924);
	}

	@Environment(EnvType.CLIENT)
	public static class_2248 method_9398(class_1767 arg) {
		return (class_2248)field_9925.getOrDefault(arg, class_2246.field_10154);
	}
}
