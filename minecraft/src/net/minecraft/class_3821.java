package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3821 {
	private final class_3825 field_16872;
	private final class_3825 field_16873;
	private final class_2680 field_16874;
	@Nullable
	private final class_2487 field_16875;

	public class_3821(class_3825 arg, class_3825 arg2, class_2680 arg3) {
		this(arg, arg2, arg3, null);
	}

	public class_3821(class_3825 arg, class_3825 arg2, class_2680 arg3, @Nullable class_2487 arg4) {
		this.field_16872 = arg;
		this.field_16873 = arg2;
		this.field_16874 = arg3;
		this.field_16875 = arg4;
	}

	public boolean method_16762(class_2680 arg, class_2680 arg2, Random random) {
		return this.field_16872.method_16768(arg, random) && this.field_16873.method_16768(arg2, random);
	}

	public class_2680 method_16763() {
		return this.field_16874;
	}

	@Nullable
	public class_2487 method_16760() {
		return this.field_16875;
	}

	public <T> Dynamic<T> method_16764(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("input_predicate"),
				this.field_16872.method_16767(dynamicOps).getValue(),
				dynamicOps.createString("location_predicate"),
				this.field_16873.method_16767(dynamicOps).getValue(),
				dynamicOps.createString("output_state"),
				class_2680.method_16550(dynamicOps, this.field_16874).getValue()
			)
		);
		return this.field_16875 == null
			? new Dynamic<>(dynamicOps, object)
			: new Dynamic<>(
				dynamicOps,
				dynamicOps.mergeInto(object, dynamicOps.createString("output_nbt"), new Dynamic<>(class_2509.field_11560, this.field_16875).convert(dynamicOps).getValue())
			);
	}

	public static <T> class_3821 method_16765(Dynamic<T> dynamic) {
		Dynamic<T> dynamic2 = dynamic.get("input_predicate").orElseEmptyMap();
		Dynamic<T> dynamic3 = dynamic.get("location_predicate").orElseEmptyMap();
		class_3825 lv = class_3817.method_16758(dynamic2, class_2378.field_16792, "predicate_type", class_3818.field_16868);
		class_3825 lv2 = class_3817.method_16758(dynamic3, class_2378.field_16792, "predicate_type", class_3818.field_16868);
		class_2680 lv3 = class_2680.method_11633(dynamic.get("output_state").orElseEmptyMap());
		class_2487 lv4 = (class_2487)dynamic.get("output_nbt").map(dynamicx -> dynamicx.convert(class_2509.field_11560).getValue()).orElse(null);
		return new class_3821(lv, lv2, lv3, lv4);
	}
}
