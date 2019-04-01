package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3826 extends class_3491 {
	private final ImmutableList<class_3821> field_16881;

	public class_3826(List<class_3821> list) {
		this.field_16881 = ImmutableList.copyOf(list);
	}

	public class_3826(Dynamic<?> dynamic) {
		this(dynamic.get("rules").asList(class_3821::method_16765));
	}

	@Nullable
	@Override
	public class_3499.class_3501 method_15110(class_1941 arg, class_2338 arg2, class_3499.class_3501 arg3, class_3499.class_3501 arg4, class_3492 arg5) {
		Random random = new Random(class_3532.method_15389(arg4.field_15597));
		class_2680 lv = arg.method_8320(arg4.field_15597);

		for (class_3821 lv2 : this.field_16881) {
			if (lv2.method_16762(arg4.field_15596, lv, random)) {
				return new class_3499.class_3501(arg4.field_15597, lv2.method_16763(), lv2.method_16760());
			}
		}

		return arg4;
	}

	@Override
	protected class_3828 method_16772() {
		return class_3828.field_16990;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("rules"), dynamicOps.createList(this.field_16881.stream().map(arg -> arg.method_16764(dynamicOps).getValue())))
			)
		);
	}
}
