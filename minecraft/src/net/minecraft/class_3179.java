package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class class_3179 implements class_3037 {
	public final List<class_2975<?>> field_13827;

	public class_3179(List<class_2975<?>> list) {
		this.field_13827 = list;
	}

	public class_3179(class_3031<?>[] args, class_3037[] args2) {
		this((List<class_2975<?>>)IntStream.range(0, args.length).mapToObj(i -> method_13956(args[i], args2[i])).collect(Collectors.toList()));
	}

	private static <FC extends class_3037> class_2975<FC> method_13956(class_3031<FC> arg, class_3037 arg2) {
		return new class_2975<>(arg, (FC)arg2);
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("features"), dynamicOps.createList(this.field_13827.stream().map(arg -> arg.method_16584(dynamicOps).getValue())))
			)
		);
	}

	public static <T> class_3179 method_13957(Dynamic<T> dynamic) {
		List<class_2975<?>> list = dynamic.get("features").asList(class_2975::method_12861);
		return new class_3179(list);
	}
}
