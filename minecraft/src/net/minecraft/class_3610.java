package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_3610 extends class_2688<class_3610> {
	class_3611 method_15772();

	default boolean method_15771() {
		return this.method_15772().method_15793(this);
	}

	default boolean method_15769() {
		return this.method_15772().method_15794();
	}

	default float method_15763() {
		return this.method_15772().method_15788(this);
	}

	default int method_15761() {
		return this.method_15772().method_15779(this);
	}

	@Environment(EnvType.CLIENT)
	default boolean method_15756(class_1922 arg, class_2338 arg2) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				class_2338 lv = arg2.method_10069(i, 0, j);
				class_3610 lv2 = arg.method_8316(lv);
				if (!lv2.method_15772().method_15780(this.method_15772()) && !arg.method_8320(lv).method_11598(arg, lv)) {
					return true;
				}
			}
		}

		return false;
	}

	default void method_15770(class_1937 arg, class_2338 arg2) {
		this.method_15772().method_15778(arg, arg2, this);
	}

	@Environment(EnvType.CLIENT)
	default void method_15768(class_1937 arg, class_2338 arg2, Random random) {
		this.method_15772().method_15776(arg, arg2, this, random);
	}

	default boolean method_15773() {
		return this.method_15772().method_15795();
	}

	default void method_15757(class_1937 arg, class_2338 arg2, Random random) {
		this.method_15772().method_15792(arg, arg2, this, random);
	}

	default class_243 method_15758(class_1922 arg, class_2338 arg2) {
		return this.method_15772().method_15782(arg, arg2, this);
	}

	default class_2680 method_15759() {
		return this.method_15772().method_15790(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	default class_2394 method_15766() {
		return this.method_15772().method_15787();
	}

	@Environment(EnvType.CLIENT)
	default class_1921 method_15762() {
		return this.method_15772().method_15786();
	}

	default boolean method_15767(class_3494<class_3611> arg) {
		return this.method_15772().method_15791(arg);
	}

	default float method_15760() {
		return this.method_15772().method_15784();
	}

	default boolean method_15764(class_3611 arg, class_2350 arg2) {
		return this.method_15772().method_15777(this, arg, arg2);
	}

	static <T> Dynamic<T> method_16458(DynamicOps<T> dynamicOps, class_3610 arg) {
		ImmutableMap<class_2769<?>, Comparable<?>> immutableMap = arg.method_11656();
		T object;
		if (immutableMap.isEmpty()) {
			object = dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(class_2378.field_11154.method_10221(arg.method_15772()).toString()))
			);
		} else {
			object = dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("Name"),
					dynamicOps.createString(class_2378.field_11154.method_10221(arg.method_15772()).toString()),
					dynamicOps.createString("Properties"),
					dynamicOps.createMap(
						(Map<T, T>)immutableMap.entrySet()
							.stream()
							.map(
								entry -> Pair.of(
										dynamicOps.createString(((class_2769)entry.getKey()).method_11899()),
										dynamicOps.createString(class_2688.method_16551((class_2769<T>)entry.getKey(), (Comparable<?>)entry.getValue()))
									)
							)
							.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
					)
				)
			);
		}

		return new Dynamic<>(dynamicOps, object);
	}

	static <T> class_3610 method_15765(Dynamic<T> dynamic) {
		class_3611 lv = class_2378.field_11154
			.method_10223(new class_2960((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:empty")));
		Map<String, String> map = dynamic.get("Properties").asMap(dynamicx -> dynamicx.asString(""), dynamicx -> dynamicx.asString(""));
		class_3610 lv2 = lv.method_15785();
		class_2689<class_3611, class_3610> lv3 = lv.method_15783();

		for (Entry<String, String> entry : map.entrySet()) {
			String string = (String)entry.getKey();
			class_2769<?> lv4 = lv3.method_11663(string);
			if (lv4 != null) {
				lv2 = class_2688.method_11655(lv2, lv4, string, dynamic.toString(), (String)entry.getValue());
			}
		}

		return lv2;
	}
}
