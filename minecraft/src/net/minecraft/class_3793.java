package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import javax.annotation.Nullable;

public class class_3793 extends class_3491 {
	public static final class_3793 field_16718 = new class_3793(ImmutableList.of(class_2246.field_10465));
	public static final class_3793 field_16719 = new class_3793(ImmutableList.of(class_2246.field_10124));
	public static final class_3793 field_16721 = new class_3793(ImmutableList.of(class_2246.field_10124, class_2246.field_10465));
	private final ImmutableList<class_2248> field_16720;

	public class_3793(List<class_2248> list) {
		this.field_16720 = ImmutableList.copyOf(list);
	}

	public class_3793(Dynamic<?> dynamic) {
		this(dynamic.get("blocks").asList(dynamicx -> class_2680.method_11633(dynamicx).method_11614()));
	}

	@Nullable
	@Override
	public class_3499.class_3501 method_15110(class_1941 arg, class_2338 arg2, class_3499.class_3501 arg3, class_3499.class_3501 arg4, class_3492 arg5) {
		return this.field_16720.contains(arg4.field_15596.method_11614()) ? null : arg4;
	}

	@Override
	protected class_3828 method_16772() {
		return class_3828.field_16986;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("blocks"),
					dynamicOps.createList(this.field_16720.stream().map(arg -> class_2680.method_16550(dynamicOps, arg.method_9564()).getValue()))
				)
			)
		);
	}
}
