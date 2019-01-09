package net.minecraft;

import java.util.function.Consumer;

public class class_2416 implements Consumer<Consumer<class_161>> {
	public void method_10346(Consumer<class_161> consumer) {
		class_161 lv = class_161.class_162.method_707()
			.method_697(
				class_2246.field_9986,
				new class_2588("advancements.nether.root.title"),
				new class_2588("advancements.nether.root.description"),
				new class_2960("textures/gui/advancements/backgrounds/nether.png"),
				class_189.field_1254,
				false,
				false,
				false
			)
			.method_709("entered_nether", class_1999.class_2001.method_8799(class_2874.field_13076))
			.method_694(consumer, "nether/root");
		class_161 lv2 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8814,
				new class_2588("advancements.nether.return_to_sender.title"),
				new class_2588("advancements.nether.return_to_sender.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(50))
			.method_709(
				"killed_ghast",
				class_2080.class_2083.method_9001(
					class_2048.class_2049.method_8916().method_8921(class_1299.field_6107),
					class_2022.class_2023.method_8855().method_8852(true).method_8854(class_2048.class_2049.method_8916().method_8921(class_1299.field_6066))
				)
			)
			.method_694(consumer, "nether/return_to_sender");
		class_161 lv3 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_2246.field_10266,
				new class_2588("advancements.nether.find_fortress.title"),
				new class_2588("advancements.nether.find_fortress.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("fortress", class_2092.class_2094.method_9034(class_2090.method_9017(class_3031.field_13569)))
			.method_694(consumer, "nether/find_fortress");
		class_161 lv4 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8895,
				new class_2588("advancements.nether.fast_travel.title"),
				new class_2588("advancements.nether.fast_travel.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(100))
			.method_709("travelled", class_2108.class_2110.method_9085(class_2025.method_8860(class_2096.class_2099.method_9050(7000.0F))))
			.method_694(consumer, "nether/fast_travel");
		class_161 lv5 = class_161.class_162.method_707()
			.method_701(lv2)
			.method_697(
				class_1802.field_8070,
				new class_2588("advancements.nether.uneasy_alliance.title"),
				new class_2588("advancements.nether.uneasy_alliance.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(100))
			.method_709(
				"killed_ghast",
				class_2080.class_2083.method_8997(
					class_2048.class_2049.method_8916().method_8921(class_1299.field_6107).method_8918(class_2090.method_9016(class_2874.field_13072))
				)
			)
			.method_694(consumer, "nether/uneasy_alliance");
		class_161 lv6 = class_161.class_162.method_707()
			.method_701(lv3)
			.method_697(
				class_2246.field_10177,
				new class_2588("advancements.nether.get_wither_skull.title"),
				new class_2588("advancements.nether.get_wither_skull.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("wither_skull", class_2066.class_2068.method_8959(class_2246.field_10177))
			.method_694(consumer, "nether/get_wither_skull");
		class_161 lv7 = class_161.class_162.method_707()
			.method_701(lv6)
			.method_697(
				class_1802.field_8137,
				new class_2588("advancements.nether.summon_wither.title"),
				new class_2588("advancements.nether.summon_wither.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("summoned", class_2128.class_2130.method_9129(class_2048.class_2049.method_8916().method_8921(class_1299.field_6119)))
			.method_694(consumer, "nether/summon_wither");
		class_161 lv8 = class_161.class_162.method_707()
			.method_701(lv3)
			.method_697(
				class_1802.field_8894,
				new class_2588("advancements.nether.obtain_blaze_rod.title"),
				new class_2588("advancements.nether.obtain_blaze_rod.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("blaze_rod", class_2066.class_2068.method_8959(class_1802.field_8894))
			.method_694(consumer, "nether/obtain_blaze_rod");
		class_161 lv9 = class_161.class_162.method_707()
			.method_701(lv7)
			.method_697(
				class_2246.field_10327,
				new class_2588("advancements.nether.create_beacon.title"),
				new class_2588("advancements.nether.create_beacon.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("beacon", class_2006.class_2008.method_8818(class_2096.class_2100.method_9053(1)))
			.method_694(consumer, "nether/create_beacon");
		class_161 lv10 = class_161.class_162.method_707()
			.method_701(lv9)
			.method_697(
				class_2246.field_10327,
				new class_2588("advancements.nether.create_full_beacon.title"),
				new class_2588("advancements.nether.create_full_beacon.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("beacon", class_2006.class_2008.method_8818(class_2096.class_2100.method_9058(4)))
			.method_694(consumer, "nether/create_full_beacon");
		class_161 lv11 = class_161.class_162.method_707()
			.method_701(lv8)
			.method_697(
				class_1802.field_8574,
				new class_2588("advancements.nether.brew_potion.title"),
				new class_2588("advancements.nether.brew_potion.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("potion", class_1996.class_1998.method_8791())
			.method_694(consumer, "nether/brew_potion");
		class_161 lv12 = class_161.class_162.method_707()
			.method_701(lv11)
			.method_697(
				class_1802.field_8103,
				new class_2588("advancements.nether.all_potions.title"),
				new class_2588("advancements.nether.all_potions.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(100))
			.method_709(
				"all_effects",
				class_2027.class_2029.method_8869(
					class_2102.method_9066()
						.method_9065(class_1294.field_5904)
						.method_9065(class_1294.field_5909)
						.method_9065(class_1294.field_5910)
						.method_9065(class_1294.field_5913)
						.method_9065(class_1294.field_5924)
						.method_9065(class_1294.field_5918)
						.method_9065(class_1294.field_5923)
						.method_9065(class_1294.field_5905)
						.method_9065(class_1294.field_5925)
						.method_9065(class_1294.field_5911)
						.method_9065(class_1294.field_5899)
						.method_9065(class_1294.field_5906)
						.method_9065(class_1294.field_5907)
				)
			)
			.method_694(consumer, "nether/all_potions");
		class_161 lv13 = class_161.class_162.method_707()
			.method_701(lv12)
			.method_697(
				class_1802.field_8550,
				new class_2588("advancements.nether.all_effects.title"),
				new class_2588("advancements.nether.all_effects.description"),
				null,
				class_189.field_1250,
				true,
				true,
				true
			)
			.method_703(class_170.class_171.method_750(1000))
			.method_709(
				"all_effects",
				class_2027.class_2029.method_8869(
					class_2102.method_9066()
						.method_9065(class_1294.field_5904)
						.method_9065(class_1294.field_5909)
						.method_9065(class_1294.field_5910)
						.method_9065(class_1294.field_5913)
						.method_9065(class_1294.field_5924)
						.method_9065(class_1294.field_5918)
						.method_9065(class_1294.field_5923)
						.method_9065(class_1294.field_5905)
						.method_9065(class_1294.field_5925)
						.method_9065(class_1294.field_5911)
						.method_9065(class_1294.field_5899)
						.method_9065(class_1294.field_5920)
						.method_9065(class_1294.field_5917)
						.method_9065(class_1294.field_5901)
						.method_9065(class_1294.field_5902)
						.method_9065(class_1294.field_5912)
						.method_9065(class_1294.field_5898)
						.method_9065(class_1294.field_5903)
						.method_9065(class_1294.field_5916)
						.method_9065(class_1294.field_5907)
						.method_9065(class_1294.field_5906)
						.method_9065(class_1294.field_5927)
						.method_9065(class_1294.field_5900)
						.method_9065(class_1294.field_5922)
						.method_9065(class_1294.field_5919)
				)
			)
			.method_694(consumer, "nether/all_effects");
	}
}
