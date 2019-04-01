package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3488 extends class_3491 {
	private final float field_15523;

	public class_3488(float f) {
		this.field_15523 = f;
	}

	public class_3488(Dynamic<?> dynamic) {
		this(dynamic.get("integrity").asFloat(1.0F));
	}

	@Nullable
	@Override
	public class_3499.class_3501 method_15110(class_1941 arg, class_2338 arg2, class_3499.class_3501 arg3, class_3499.class_3501 arg4, class_3492 arg5) {
		Random random = arg5.method_15115(arg4.field_15597);
		return !(this.field_15523 >= 1.0F) && !(random.nextFloat() <= this.field_15523) ? null : arg4;
	}

	@Override
	protected class_3828 method_16772() {
		return class_3828.field_16988;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("integrity"), dynamicOps.createFloat(this.field_15523))));
	}
}
