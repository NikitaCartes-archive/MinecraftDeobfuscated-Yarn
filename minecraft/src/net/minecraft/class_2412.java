package net.minecraft;

import java.util.function.Consumer;

public class class_2412 implements Consumer<Consumer<class_161>> {
	private static final class_1959[] field_11293 = new class_1959[]{
		class_1972.field_9421,
		class_1972.field_9438,
		class_1972.field_9471,
		class_1972.field_9424,
		class_1972.field_9459,
		class_1972.field_9429,
		class_1972.field_9454,
		class_1972.field_9415,
		class_1972.field_9409,
		class_1972.field_9419,
		class_1972.field_9452,
		class_1972.field_9428,
		class_1972.field_9444,
		class_1972.field_9410,
		class_1972.field_9449,
		class_1972.field_9451,
		class_1972.field_9463,
		class_1972.field_9477,
		class_1972.field_9478,
		class_1972.field_9432,
		class_1972.field_9474,
		class_1972.field_9407,
		class_1972.field_9472,
		class_1972.field_9466,
		class_1972.field_9417,
		class_1972.field_9434,
		class_1972.field_9430,
		class_1972.field_9425,
		class_1972.field_9433,
		class_1972.field_9475,
		class_1972.field_9420,
		class_1972.field_9412,
		class_1972.field_9462,
		class_1972.field_9460,
		class_1972.field_9408,
		class_1972.field_9441,
		class_1972.field_9467,
		class_1972.field_9439,
		class_1972.field_9470,
		class_1972.field_9418,
		class_1972.field_9440,
		class_1972.field_9468
	};
	private static final class_1299<?>[] field_11294 = new class_1299[]{
		class_1299.field_6084,
		class_1299.field_6079,
		class_1299.field_6050,
		class_1299.field_6091,
		class_1299.field_6042,
		class_1299.field_6099,
		class_1299.field_6046,
		class_1299.field_6090,
		class_1299.field_6107,
		class_1299.field_6118,
		class_1299.field_6071,
		class_1299.field_6102,
		class_1299.field_6109,
		class_1299.field_6125,
		class_1299.field_6137,
		class_1299.field_6069,
		class_1299.field_6098,
		class_1299.field_6117,
		class_1299.field_6145,
		class_1299.field_6076,
		class_1299.field_6051,
		class_1299.field_6054,
		class_1299.field_6078,
		class_1299.field_6123,
		class_1299.field_6105,
		class_1299.field_6134
	};

	public void method_10335(Consumer<class_161> consumer) {
		class_161 lv = class_161.class_162.method_707()
			.method_697(
				class_1802.field_8895,
				new class_2588("advancements.adventure.root.title"),
				new class_2588("advancements.adventure.root.description"),
				new class_2960("textures/gui/advancements/backgrounds/adventure.png"),
				class_189.field_1254,
				false,
				false,
				false
			)
			.method_704(class_193.field_1257)
			.method_709("killed_something", class_2080.class_2083.method_8999())
			.method_709("killed_by_something", class_2080.class_2083.method_8998())
			.method_694(consumer, "adventure/root");
		class_161 lv2 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_2246.field_10069,
				new class_2588("advancements.adventure.sleep_in_bed.title"),
				new class_2588("advancements.adventure.sleep_in_bed.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("slept_in_bed", class_2092.class_2094.method_9032())
			.method_694(consumer, "adventure/sleep_in_bed");
		class_161 lv3 = this.method_10337(class_161.class_162.method_707())
			.method_701(lv2)
			.method_697(
				class_1802.field_8285,
				new class_2588("advancements.adventure.adventuring_time.title"),
				new class_2588("advancements.adventure.adventuring_time.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(500))
			.method_694(consumer, "adventure/adventuring_time");
		class_161 lv4 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8687,
				new class_2588("advancements.adventure.trade.title"),
				new class_2588("advancements.adventure.trade.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("traded", class_2140.class_2142.method_9153())
			.method_694(consumer, "adventure/trade");
		class_161 lv5 = this.method_10336(class_161.class_162.method_707())
			.method_701(lv)
			.method_697(
				class_1802.field_8371,
				new class_2588("advancements.adventure.kill_a_mob.title"),
				new class_2588("advancements.adventure.kill_a_mob.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_704(class_193.field_1257)
			.method_694(consumer, "adventure/kill_a_mob");
		class_161 lv6 = this.method_10336(class_161.class_162.method_707())
			.method_701(lv5)
			.method_697(
				class_1802.field_8802,
				new class_2588("advancements.adventure.kill_all_mobs.title"),
				new class_2588("advancements.adventure.kill_all_mobs.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(100))
			.method_694(consumer, "adventure/kill_all_mobs");
		class_161 lv7 = class_161.class_162.method_707()
			.method_701(lv5)
			.method_697(
				class_1802.field_8102,
				new class_2588("advancements.adventure.shoot_arrow.title"),
				new class_2588("advancements.adventure.shoot_arrow.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709(
				"shot_arrow",
				class_2115.class_2117.method_9103(
					class_2019.class_2020.method_8844()
						.method_8842(class_2022.class_2023.method_8855().method_8852(true).method_8854(class_2048.class_2049.method_8916().method_8921(class_1299.field_6122)))
				)
			)
			.method_694(consumer, "adventure/shoot_arrow");
		class_161 lv8 = class_161.class_162.method_707()
			.method_701(lv5)
			.method_697(
				class_1802.field_8547,
				new class_2588("advancements.adventure.throw_trident.title"),
				new class_2588("advancements.adventure.throw_trident.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709(
				"shot_trident",
				class_2115.class_2117.method_9103(
					class_2019.class_2020.method_8844()
						.method_8842(class_2022.class_2023.method_8855().method_8852(true).method_8854(class_2048.class_2049.method_8916().method_8921(class_1299.field_6127)))
				)
			)
			.method_694(consumer, "adventure/throw_trident");
		class_161 lv9 = class_161.class_162.method_707()
			.method_701(lv8)
			.method_697(
				class_1802.field_8547,
				new class_2588("advancements.adventure.very_very_frightening.title"),
				new class_2588("advancements.adventure.very_very_frightening.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("struck_villager", class_2002.class_2004.method_8809(class_2048.class_2049.method_8916().method_8921(class_1299.field_6077).method_8920()))
			.method_694(consumer, "adventure/very_very_frightening");
		class_161 lv10 = class_161.class_162.method_707()
			.method_701(lv4)
			.method_697(
				class_2246.field_10147,
				new class_2588("advancements.adventure.summon_iron_golem.title"),
				new class_2588("advancements.adventure.summon_iron_golem.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("summoned_golem", class_2128.class_2130.method_9129(class_2048.class_2049.method_8916().method_8921(class_1299.field_6147)))
			.method_694(consumer, "adventure/summon_iron_golem");
		class_161 lv11 = class_161.class_162.method_707()
			.method_701(lv7)
			.method_697(
				class_1802.field_8107,
				new class_2588("advancements.adventure.sniper_duel.title"),
				new class_2588("advancements.adventure.sniper_duel.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(50))
			.method_709(
				"killed_skeleton",
				class_2080.class_2083.method_9001(
					class_2048.class_2049.method_8916().method_8921(class_1299.field_6137).method_8924(class_2025.method_8860(class_2096.class_2099.method_9050(50.0F))),
					class_2022.class_2023.method_8855().method_8852(true)
				)
			)
			.method_694(consumer, "adventure/sniper_duel");
		class_161 lv12 = class_161.class_162.method_707()
			.method_701(lv5)
			.method_697(
				class_1802.field_8288,
				new class_2588("advancements.adventure.totem_of_undying.title"),
				new class_2588("advancements.adventure.totem_of_undying.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("used_totem", class_2148.class_2150.method_9170(class_1802.field_8288))
			.method_694(consumer, "adventure/totem_of_undying");
		class_161 lv13 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8399,
				new class_2588("advancements.adventure.ol_betsy.title"),
				new class_2588("advancements.adventure.ol_betsy.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("shot_crossbow", class_2123.class_2125.method_9120(class_1802.field_8399))
			.method_694(consumer, "adventure/ol_betsy");
		class_161 lv14 = class_161.class_162.method_707()
			.method_701(lv13)
			.method_697(
				class_1802.field_8399,
				new class_2588("advancements.adventure.whos_the_pillager_now.title"),
				new class_2588("advancements.adventure.whos_the_pillager_now.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709(
				"kill_pillager",
				class_2080.class_2081.method_8992(
					class_2048.class_2049.method_8916().method_8921(class_1299.field_6105),
					class_2022.class_2023.method_8855().method_8852(true).method_8854(class_2048.class_2049.method_8916().method_8921(class_1299.field_6122))
				)
			)
			.method_694(consumer, "adventure/whos_the_pillager_now");
		class_161 lv15 = class_161.class_162.method_707()
			.method_701(lv13)
			.method_697(
				class_1802.field_8399,
				new class_2588("advancements.adventure.two_birds_one_arrow.title"),
				new class_2588("advancements.adventure.two_birds_one_arrow.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(65))
			.method_709(
				"two_birds",
				class_2076.class_2078.method_8986(
					class_2048.class_2049.method_8916().method_8921(class_1299.field_6078), class_2048.class_2049.method_8916().method_8921(class_1299.field_6078)
				)
			)
			.method_694(consumer, "adventure/two_birds_one_arrow");
		class_161 lv16 = class_161.class_162.method_707()
			.method_701(lv13)
			.method_697(
				class_1802.field_8399,
				new class_2588("advancements.adventure.arbalistic.title"),
				new class_2588("advancements.adventure.arbalistic.description"),
				null,
				class_189.field_1250,
				true,
				true,
				true
			)
			.method_703(class_170.class_171.method_750(85))
			.method_709("arbalistic", class_2076.class_2078.method_8987(class_2096.class_2100.method_9058(5)))
			.method_694(consumer, "adventure/arbalistic");
	}

	private class_161.class_162 method_10336(class_161.class_162 arg) {
		for (class_1299<?> lv : field_11294) {
			arg.method_709(class_2378.field_11145.method_10221(lv).toString(), class_2080.class_2083.method_8997(class_2048.class_2049.method_8916().method_8921(lv)));
		}

		return arg;
	}

	private class_161.class_162 method_10337(class_161.class_162 arg) {
		for (class_1959 lv : field_11293) {
			arg.method_709(class_2378.field_11153.method_10221(lv).toString(), class_2092.class_2094.method_9034(class_2090.method_9022(lv)));
		}

		return arg;
	}
}
