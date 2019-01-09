package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;

public class class_3782 extends class_3784 {
	private final List<class_3784> field_16676;

	public class_3782(List<class_3784> list) {
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Elements are empty");
		} else {
			this.field_16676 = list;
		}
	}

	public class_3782(Dynamic<?> dynamic) {
		this(dynamic.get("elements").asList(dynamicx -> class_3817.method_16758(dynamicx, class_2378.field_16793, "element_type", class_3777.field_16663)));
	}

	@Override
	public List<class_3499.class_3501> method_16627(class_3485 arg, class_2338 arg2, class_2470 arg3, Random random) {
		return ((class_3784)this.field_16676.get(0)).method_16627(arg, arg2, arg3, random);
	}

	@Override
	public class_3341 method_16628(class_3485 arg, class_2338 arg2, class_2470 arg3) {
		class_3341 lv = class_3341.method_14665();

		for (class_3784 lv2 : this.field_16676) {
			class_3341 lv3 = lv2.method_16628(arg, arg2, arg3);
			lv.method_14668(lv3);
		}

		return lv;
	}

	@Override
	public boolean method_16626(class_1936 arg, class_2338 arg2, class_2470 arg3, class_3341 arg4, Random random) {
		for (class_3784 lv : this.field_16676) {
			if (!lv.method_16626(arg, arg2, arg3, arg4, random)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_3816 method_16757() {
		return class_3816.field_16974;
	}

	@Override
	public class_3784 method_16622(class_3785.class_3786 arg) {
		this.field_16676.forEach(arg2 -> arg2.method_16622(arg));
		return this;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(this.field_16676.stream().map(arg -> arg.method_16755(dynamicOps).getValue()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("elements"), object)));
	}
}
