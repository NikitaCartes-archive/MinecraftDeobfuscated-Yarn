package net.minecraft;

import java.util.function.Consumer;

public class class_2419 implements Consumer<Consumer<class_161>> {
	public void method_10348(Consumer<class_161> consumer) {
		class_161 lv = class_161.class_162.method_707()
			.method_697(
				class_2246.field_10471,
				new class_2588("advancements.end.root.title"),
				new class_2588("advancements.end.root.description"),
				new class_2960("textures/gui/advancements/backgrounds/end.png"),
				class_189.field_1254,
				false,
				false,
				false
			)
			.method_709("entered_end", class_1999.class_2001.method_8799(class_2874.field_13078))
			.method_694(consumer, "end/root");
		class_161 lv2 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_2246.field_10337,
				new class_2588("advancements.end.kill_dragon.title"),
				new class_2588("advancements.end.kill_dragon.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("killed_dragon", class_2080.class_2083.method_8997(class_2048.class_2049.method_8916().method_8921(class_1299.field_6116)))
			.method_694(consumer, "end/kill_dragon");
		class_161 lv3 = class_161.class_162.method_707()
			.method_701(lv2)
			.method_697(
				class_1802.field_8634,
				new class_2588("advancements.end.enter_end_gateway.title"),
				new class_2588("advancements.end.enter_end_gateway.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("entered_end_gateway", class_2037.class_2039.method_8890(class_2246.field_10613))
			.method_694(consumer, "end/enter_end_gateway");
		class_161 lv4 = class_161.class_162.method_707()
			.method_701(lv2)
			.method_697(
				class_1802.field_8301,
				new class_2588("advancements.end.respawn_dragon.title"),
				new class_2588("advancements.end.respawn_dragon.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("summoned_dragon", class_2128.class_2130.method_9129(class_2048.class_2049.method_8916().method_8921(class_1299.field_6116)))
			.method_694(consumer, "end/respawn_dragon");
		class_161 lv5 = class_161.class_162.method_707()
			.method_701(lv3)
			.method_697(
				class_2246.field_10286,
				new class_2588("advancements.end.find_end_city.title"),
				new class_2588("advancements.end.find_end_city.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("in_city", class_2092.class_2094.method_9034(class_2090.method_9017(class_3031.field_13553)))
			.method_694(consumer, "end/find_end_city");
		class_161 lv6 = class_161.class_162.method_707()
			.method_701(lv2)
			.method_697(
				class_1802.field_8613,
				new class_2588("advancements.end.dragon_breath.title"),
				new class_2588("advancements.end.dragon_breath.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("dragon_breath", class_2066.class_2068.method_8959(class_1802.field_8613))
			.method_694(consumer, "end/dragon_breath");
		class_161 lv7 = class_161.class_162.method_707()
			.method_701(lv5)
			.method_697(
				class_1802.field_8815,
				new class_2588("advancements.end.levitate.title"),
				new class_2588("advancements.end.levitate.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(50))
			.method_709("levitated", class_2085.class_2087.method_9013(class_2025.method_8856(class_2096.class_2099.method_9050(50.0F))))
			.method_694(consumer, "end/levitate");
		class_161 lv8 = class_161.class_162.method_707()
			.method_701(lv5)
			.method_697(
				class_1802.field_8833,
				new class_2588("advancements.end.elytra.title"),
				new class_2588("advancements.end.elytra.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("elytra", class_2066.class_2068.method_8959(class_1802.field_8833))
			.method_694(consumer, "end/elytra");
		class_161 lv9 = class_161.class_162.method_707()
			.method_701(lv2)
			.method_697(
				class_2246.field_10081,
				new class_2588("advancements.end.dragon_egg.title"),
				new class_2588("advancements.end.dragon_egg.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("dragon_egg", class_2066.class_2068.method_8959(class_2246.field_10081))
			.method_694(consumer, "end/dragon_egg");
	}
}
