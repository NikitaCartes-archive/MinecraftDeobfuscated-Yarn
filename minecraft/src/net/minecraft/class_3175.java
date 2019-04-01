package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class class_3175 implements class_3037 {
	protected final class_2680 field_13807;
	protected final List<class_2680> field_13808;
	protected final List<class_2680> field_13805;
	protected final List<class_2680> field_13806;

	public class_3175(class_2680 arg, List<class_2680> list, List<class_2680> list2, List<class_2680> list3) {
		this.field_13807 = arg;
		this.field_13808 = list;
		this.field_13805 = list2;
		this.field_13806 = list3;
	}

	public class_3175(class_2680 arg, class_2680[] args, class_2680[] args2, class_2680[] args3) {
		this(arg, Lists.<class_2680>newArrayList(args), Lists.<class_2680>newArrayList(args2), Lists.<class_2680>newArrayList(args3));
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		T object = class_2680.method_16550(dynamicOps, this.field_13807).getValue();
		T object2 = dynamicOps.createList(this.field_13808.stream().map(arg -> class_2680.method_16550(dynamicOps, arg).getValue()));
		T object3 = dynamicOps.createList(this.field_13805.stream().map(arg -> class_2680.method_16550(dynamicOps, arg).getValue()));
		T object4 = dynamicOps.createList(this.field_13806.stream().map(arg -> class_2680.method_16550(dynamicOps, arg).getValue()));
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("to_place"),
					object,
					dynamicOps.createString("place_on"),
					object2,
					dynamicOps.createString("place_in"),
					object3,
					dynamicOps.createString("place_under"),
					object4
				)
			)
		);
	}

	public static <T> class_3175 method_13939(Dynamic<T> dynamic) {
		class_2680 lv = (class_2680)dynamic.get("to_place").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		List<class_2680> list = dynamic.get("place_on").asList(class_2680::method_11633);
		List<class_2680> list2 = dynamic.get("place_in").asList(class_2680::method_11633);
		List<class_2680> list3 = dynamic.get("place_under").asList(class_2680::method_11633);
		return new class_3175(lv, list, list2, list3);
	}
}
