package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class class_3141 implements class_3037 {
	public final List<class_3226<?>> field_13744;
	public final class_2975<?> field_13745;

	public class_3141(List<class_3226<?>> list, class_2975<?> arg) {
		this.field_13744 = list;
		this.field_13745 = arg;
	}

	public class_3141(class_3031<?>[] args, class_3037[] args2, float[] fs, class_3031<?> arg, class_3037 arg2) {
		this(
			(List<class_3226<?>>)IntStream.range(0, args.length).mapToObj(i -> method_13710(args[i], args2[i], fs[i])).collect(Collectors.toList()),
			method_13708(arg, arg2)
		);
	}

	private static <FC extends class_3037> class_3226<FC> method_13710(class_3031<FC> arg, class_3037 arg2, float f) {
		return new class_3226<>(arg, (FC)arg2, Float.valueOf(f));
	}

	private static <FC extends class_3037> class_2975<FC> method_13708(class_3031<FC> arg, class_3037 arg2) {
		return new class_2975<>(arg, (FC)arg2);
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(this.field_13744.stream().map(arg -> arg.method_16599(dynamicOps).getValue()));
		T object2 = this.field_13745.method_16584(dynamicOps).getValue();
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("features"), object, dynamicOps.createString("default"), object2))
		);
	}

	public static <T> class_3141 method_13709(Dynamic<T> dynamic) {
		List<class_3226<?>> list = dynamic.get("features").asList(class_3226::method_14270);
		class_2975<?> lv = class_2975.method_12861(dynamic.get("default").orElseEmptyMap());
		return new class_3141(list, lv);
	}
}
