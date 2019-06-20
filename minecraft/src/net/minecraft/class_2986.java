package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_2986 implements class_3037 {
	public final class_2975<?> field_13399;
	public final class_3243<?> field_13398;

	public class_2986(class_2975<?> arg, class_3243<?> arg2) {
		this.field_13399 = arg;
		this.field_13398 = arg2;
	}

	public <F extends class_3037, D extends class_2998> class_2986(class_3031<F> arg, F arg2, class_3284<D> arg3, D arg4) {
		this(new class_2975<>(arg, arg2), new class_3243<>(arg3, arg4));
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("feature"),
					this.field_13399.method_16584(dynamicOps).getValue(),
					dynamicOps.createString("decorator"),
					this.field_13398.method_16641(dynamicOps).getValue()
				)
			)
		);
	}

	public String toString() {
		return String.format(
			"< %s [%s | %s] >",
			this.getClass().getSimpleName(),
			class_2378.field_11138.method_10221(this.field_13399.field_13376),
			class_2378.field_11148.method_10221(this.field_13398.field_14115)
		);
	}

	public static <T> class_2986 method_12891(Dynamic<T> dynamic) {
		class_2975<?> lv = class_2975.method_12861(dynamic.get("feature").orElseEmptyMap());
		class_3243<?> lv2 = class_3243.method_14359(dynamic.get("decorator").orElseEmptyMap());
		return new class_2986(lv, lv2);
	}
}
