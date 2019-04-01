package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;

public abstract class class_3491 {
	@Nullable
	public abstract class_3499.class_3501 method_15110(class_1941 arg, class_2338 arg2, class_3499.class_3501 arg3, class_3499.class_3501 arg4, class_3492 arg5);

	protected abstract class_3828 method_16772();

	protected abstract <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16771(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_16666(dynamicOps).getValue(),
				dynamicOps.createString("processor_type"),
				dynamicOps.createString(class_2378.field_16794.method_10221(this.method_16772()).toString())
			)
		);
	}
}
