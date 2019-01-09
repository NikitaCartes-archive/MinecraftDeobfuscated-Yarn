package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;

public class class_3776 extends class_3784 {
	private final class_2975<?> field_16661;
	private final class_2487 field_16662;

	public class_3776(class_2975<?> arg) {
		this.field_16661 = arg;
		this.field_16662 = new class_2487();
		this.field_16662.method_10582("target_pool", "minecraft:empty");
		this.field_16662.method_10582("attachement_type", "minecraft:bottom");
		this.field_16662.method_10582("final_state", "minecraft:air");
	}

	public <T> class_3776(Dynamic<T> dynamic) {
		this(class_2975.method_12861(dynamic.get("feature").orElseEmptyMap()));
	}

	public class_2338 method_16601(class_3485 arg, class_2470 arg2) {
		return new class_2338(0, 0, 0);
	}

	@Override
	public List<class_3499.class_3501> method_16627(class_3485 arg, class_2338 arg2, class_2470 arg3, Random random) {
		List<class_3499.class_3501> list = Lists.<class_3499.class_3501>newArrayList();
		list.add(new class_3499.class_3501(arg2, class_2246.field_16540.method_9564().method_11657(class_3748.field_10927, class_2350.field_11033), this.field_16662));
		return list;
	}

	@Override
	public class_3341 method_16628(class_3485 arg, class_2338 arg2, class_2470 arg3) {
		class_2338 lv = this.method_16601(arg, arg3);
		return new class_3341(
			arg2.method_10263(),
			arg2.method_10264(),
			arg2.method_10260(),
			arg2.method_10263() + lv.method_10263(),
			arg2.method_10264() + lv.method_10264(),
			arg2.method_10260() + lv.method_10260()
		);
	}

	@Override
	public boolean method_16626(class_1936 arg, class_2338 arg2, class_2470 arg3, class_3341 arg4, Random random) {
		class_2794<?> lv = arg.method_8398().method_12129();
		return this.field_16661.method_12862(arg, (class_2794<? extends class_2888>)lv, random, arg2);
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("feature"), this.field_16661.method_16584(dynamicOps).getValue()))
		);
	}

	@Override
	public class_3816 method_16757() {
		return class_3816.field_16971;
	}
}
