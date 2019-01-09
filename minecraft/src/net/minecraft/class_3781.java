package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class class_3781 extends class_3784 {
	protected final class_2960 field_16672;
	protected final ImmutableList<class_3491> field_16674;

	public class_3781(String string, List<class_3491> list) {
		this.field_16672 = new class_2960(string);
		this.field_16674 = ImmutableList.copyOf(list);
	}

	public class_3781(String string) {
		this(string, ImmutableList.of());
	}

	public class_3781(Dynamic<?> dynamic) {
		this(
			dynamic.get("location").asString(""),
			dynamic.get("processors").asList(dynamicx -> class_3817.method_16758(dynamicx, class_2378.field_16794, "processor_type", class_3822.field_16876))
		);
	}

	public List<class_3499.class_3501> method_16614(class_3485 arg, class_2338 arg2, class_2470 arg3, boolean bl) {
		class_3499 lv = arg.method_15091(this.field_16672);
		List<class_3499.class_3501> list = lv.method_15165(arg2, new class_3492().method_15123(arg3), class_2246.field_10465, bl);
		List<class_3499.class_3501> list2 = Lists.<class_3499.class_3501>newArrayList();

		for (class_3499.class_3501 lv2 : list) {
			if (lv2.field_15595 != null) {
				class_2776 lv3 = class_2776.valueOf(lv2.field_15595.method_10558("mode"));
				if (lv3 == class_2776.field_12696) {
					list2.add(lv2);
				}
			}
		}

		return list2;
	}

	@Override
	public List<class_3499.class_3501> method_16627(class_3485 arg, class_2338 arg2, class_2470 arg3, Random random) {
		class_3499 lv = arg.method_15091(this.field_16672);
		List<class_3499.class_3501> list = lv.method_15165(arg2, new class_3492().method_15123(arg3), class_2246.field_16540, true);
		Collections.shuffle(list, random);
		return list;
	}

	@Override
	public class_3341 method_16628(class_3485 arg, class_2338 arg2, class_2470 arg3) {
		class_3499 lv = arg.method_15091(this.field_16672);
		return lv.method_16187(new class_3492().method_15123(arg3), arg2);
	}

	@Override
	public boolean method_16626(class_1936 arg, class_2338 arg2, class_2470 arg3, class_3341 arg4, Random random) {
		class_3485 lv = arg.method_8411().method_134();
		class_3499 lv2 = lv.method_15091(this.field_16672);
		class_3492 lv3 = this.method_16616(arg3, arg4);
		if (!lv2.method_15172(arg, arg2, lv3, 18)) {
			return false;
		} else {
			for (class_3499.class_3501 lv4 : class_3499.method_16446(arg, arg2, lv3, this.method_16614(lv, arg2, arg3, false))) {
				this.method_16756(arg, lv4, arg2, arg3, random, arg4);
			}

			return true;
		}
	}

	protected class_3492 method_16616(class_2470 arg, class_3341 arg2) {
		class_3492 lv = new class_3492();
		lv.method_15126(arg2);
		lv.method_15123(arg);
		lv.method_15131(true);
		lv.method_15133(false);
		lv.method_16184(class_3793.field_16721);
		lv.method_16184(class_3794.field_16871);
		this.field_16674.forEach(lv::method_16184);
		this.method_16624().method_16636().forEach(lv::method_16184);
		return lv;
	}

	@Override
	public class_3816 method_16757() {
		return class_3816.field_16973;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("location"),
					dynamicOps.createString(this.field_16672.toString()),
					dynamicOps.createString("processors"),
					dynamicOps.createList(this.field_16674.stream().map(arg -> arg.method_16771(dynamicOps).getValue()))
				)
			)
		);
	}
}
