package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class class_3147 implements class_3037 {
	public final List<class_2975<?>> field_13761;
	public final int field_13762;

	public class_3147(List<class_2975<?>> list, int i) {
		this.field_13761 = list;
		this.field_13762 = i;
	}

	public class_3147(class_3031<?>[] args, class_3037[] args2, int i) {
		this((List<class_2975<?>>)IntStream.range(0, args.length).mapToObj(ix -> method_13781(args[ix], args2[ix])).collect(Collectors.toList()), i);
	}

	private static <FC extends class_3037> class_2975<?> method_13781(class_3031<FC> arg, class_3037 arg2) {
		return new class_2975<>(arg, (FC)arg2);
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("features"),
					dynamicOps.createList(this.field_13761.stream().map(arg -> arg.method_16584(dynamicOps).getValue())),
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.field_13762)
				)
			)
		);
	}

	public static <T> class_3147 method_13780(Dynamic<T> dynamic) {
		List<class_2975<?>> list = dynamic.get("features").asList(class_2975::method_12861);
		int i = dynamic.get("count").asInt(0);
		return new class_3147(list, i);
	}
}
