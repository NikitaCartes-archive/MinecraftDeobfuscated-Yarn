package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;

public class class_3018 implements class_3037 {
	private final Optional<class_2338> field_17735;
	private final boolean field_13475;

	private class_3018(Optional<class_2338> optional, boolean bl) {
		this.field_17735 = optional;
		this.field_13475 = bl;
	}

	public static class_3018 method_18034(class_2338 arg, boolean bl) {
		return new class_3018(Optional.of(arg), bl);
	}

	public static class_3018 method_18030() {
		return new class_3018(Optional.empty(), false);
	}

	public Optional<class_2338> method_18036() {
		return this.field_17735;
	}

	public boolean method_13026() {
		return this.field_13475;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			(T)this.field_17735
				.map(
					arg -> dynamicOps.createMap(
							ImmutableMap.of(
								dynamicOps.createString("exit_x"),
								dynamicOps.createInt(arg.method_10263()),
								dynamicOps.createString("exit_y"),
								dynamicOps.createInt(arg.method_10264()),
								dynamicOps.createString("exit_z"),
								dynamicOps.createInt(arg.method_10260()),
								dynamicOps.createString("exact"),
								dynamicOps.createBoolean(this.field_13475)
							)
						)
				)
				.orElse(dynamicOps.emptyMap())
		);
	}

	public static <T> class_3018 method_13027(Dynamic<T> dynamic) {
		Optional<class_2338> optional = dynamic.get("exit_x")
			.asNumber()
			.flatMap(
				number -> dynamic.get("exit_y")
						.asNumber()
						.flatMap(number2 -> dynamic.get("exit_z").asNumber().map(number3 -> new class_2338(number.intValue(), number2.intValue(), number3.intValue())))
			);
		boolean bl = dynamic.get("exact").asBoolean(false);
		return new class_3018(optional, bl);
	}
}
