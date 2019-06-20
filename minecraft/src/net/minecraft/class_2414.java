package net.minecraft;

import java.util.function.Consumer;

public class class_2414 implements Consumer<Consumer<class_161>> {
	private static final class_1299<?>[] field_11296 = new class_1299[]{
		class_1299.field_6139,
		class_1299.field_6115,
		class_1299.field_6085,
		class_1299.field_6143,
		class_1299.field_6093,
		class_1299.field_6132,
		class_1299.field_6055,
		class_1299.field_6081,
		class_1299.field_6140,
		class_1299.field_6074,
		class_1299.field_6113,
		class_1299.field_16281,
		class_1299.field_6146,
		class_1299.field_17943
	};
	private static final class_1792[] field_11295 = new class_1792[]{class_1802.field_8429, class_1802.field_8846, class_1802.field_8323, class_1802.field_8209};
	private static final class_1792[] field_11297 = new class_1792[]{class_1802.field_8666, class_1802.field_8478, class_1802.field_8108, class_1802.field_8714};
	private static final class_1792[] field_11298 = new class_1792[]{
		class_1802.field_8279,
		class_1802.field_8208,
		class_1802.field_8229,
		class_1802.field_8389,
		class_1802.field_8261,
		class_1802.field_8463,
		class_1802.field_8367,
		class_1802.field_8429,
		class_1802.field_8209,
		class_1802.field_8846,
		class_1802.field_8323,
		class_1802.field_8373,
		class_1802.field_8509,
		class_1802.field_8423,
		class_1802.field_8497,
		class_1802.field_8046,
		class_1802.field_8176,
		class_1802.field_8726,
		class_1802.field_8544,
		class_1802.field_8511,
		class_1802.field_8680,
		class_1802.field_8179,
		class_1802.field_8567,
		class_1802.field_8512,
		class_1802.field_8635,
		class_1802.field_8071,
		class_1802.field_8741,
		class_1802.field_8504,
		class_1802.field_8752,
		class_1802.field_8308,
		class_1802.field_8748,
		class_1802.field_8347,
		class_1802.field_8233,
		class_1802.field_8186,
		class_1802.field_8515,
		class_1802.field_8551,
		class_1802.field_8766,
		class_1802.field_16998
	};

	public void method_10338(Consumer<class_161> consumer) {
		class_161 lv = class_161.class_162.method_707()
			.method_697(
				class_2246.field_10359,
				new class_2588("advancements.husbandry.root.title"),
				new class_2588("advancements.husbandry.root.description"),
				new class_2960("textures/gui/advancements/backgrounds/husbandry.png"),
				class_189.field_1254,
				false,
				false,
				false
			)
			.method_709("consumed_item", class_2010.class_2012.method_8827())
			.method_694(consumer, "husbandry/root");
		class_161 lv2 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8861,
				new class_2588("advancements.husbandry.plant_seed.title"),
				new class_2588("advancements.husbandry.plant_seed.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_704(class_193.field_1257)
			.method_709("wheat", class_2111.class_2113.method_9095(class_2246.field_10293))
			.method_709("pumpkin_stem", class_2111.class_2113.method_9095(class_2246.field_9984))
			.method_709("melon_stem", class_2111.class_2113.method_9095(class_2246.field_10168))
			.method_709("beetroots", class_2111.class_2113.method_9095(class_2246.field_10341))
			.method_709("nether_wart", class_2111.class_2113.method_9095(class_2246.field_9974))
			.method_694(consumer, "husbandry/plant_seed");
		class_161 lv3 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8861,
				new class_2588("advancements.husbandry.breed_an_animal.title"),
				new class_2588("advancements.husbandry.breed_an_animal.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_704(class_193.field_1257)
			.method_709("bred", class_196.class_198.method_860())
			.method_694(consumer, "husbandry/breed_an_animal");
		class_161 lv4 = this.method_10341(class_161.class_162.method_707())
			.method_701(lv2)
			.method_697(
				class_1802.field_8279,
				new class_2588("advancements.husbandry.balanced_diet.title"),
				new class_2588("advancements.husbandry.balanced_diet.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(100))
			.method_694(consumer, "husbandry/balanced_diet");
		class_161 lv5 = class_161.class_162.method_707()
			.method_701(lv2)
			.method_697(
				class_1802.field_8527,
				new class_2588("advancements.husbandry.break_diamond_hoe.title"),
				new class_2588("advancements.husbandry.break_diamond_hoe.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(100))
			.method_709(
				"broke_hoe",
				class_2069.class_2071.method_8967(
					class_2073.class_2074.method_8973().method_8977(class_1802.field_8527).method_8976(), class_2096.class_2100.method_9058(0)
				)
			)
			.method_694(consumer, "husbandry/break_diamond_hoe");
		class_161 lv6 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8719,
				new class_2588("advancements.husbandry.tame_an_animal.title"),
				new class_2588("advancements.husbandry.tame_an_animal.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("tamed_animal", class_2131.class_2133.method_9138())
			.method_694(consumer, "husbandry/tame_an_animal");
		class_161 lv7 = this.method_10342(class_161.class_162.method_707())
			.method_701(lv3)
			.method_697(
				class_1802.field_8071,
				new class_2588("advancements.husbandry.breed_all_animals.title"),
				new class_2588("advancements.husbandry.breed_all_animals.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(100))
			.method_694(consumer, "husbandry/bred_all_animals");
		class_161 lv8 = this.method_10339(class_161.class_162.method_707())
			.method_701(lv)
			.method_704(class_193.field_1257)
			.method_697(
				class_1802.field_8378,
				new class_2588("advancements.husbandry.fishy_business.title"),
				new class_2588("advancements.husbandry.fishy_business.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_694(consumer, "husbandry/fishy_business");
		class_161 lv9 = this.method_10340(class_161.class_162.method_707())
			.method_701(lv8)
			.method_704(class_193.field_1257)
			.method_697(
				class_1802.field_8108,
				new class_2588("advancements.husbandry.tactical_fishing.title"),
				new class_2588("advancements.husbandry.tactical_fishing.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_694(consumer, "husbandry/tactical_fishing");
		class_161 lv10 = this.method_16118(class_161.class_162.method_707())
			.method_701(lv6)
			.method_697(
				class_1802.field_8429,
				new class_2588("advancements.husbandry.complete_catalogue.title"),
				new class_2588("advancements.husbandry.complete_catalogue.description"),
				null,
				class_189.field_1250,
				true,
				true,
				false
			)
			.method_703(class_170.class_171.method_750(50))
			.method_694(consumer, "husbandry/complete_catalogue");
	}

	private class_161.class_162 method_10341(class_161.class_162 arg) {
		for (class_1792 lv : field_11298) {
			arg.method_709(class_2378.field_11142.method_10221(lv).method_12832(), class_2010.class_2012.method_8828(lv));
		}

		return arg;
	}

	private class_161.class_162 method_10342(class_161.class_162 arg) {
		for (class_1299<?> lv : field_11296) {
			arg.method_709(class_1299.method_5890(lv).toString(), class_196.class_198.method_861(class_2048.class_2049.method_8916().method_8921(lv)));
		}

		return arg;
	}

	private class_161.class_162 method_10340(class_161.class_162 arg) {
		for (class_1792 lv : field_11297) {
			arg.method_709(
				class_2378.field_11142.method_10221(lv).method_12832(),
				class_2054.class_2056.method_8937(class_2073.class_2074.method_8973().method_8977(lv).method_8976())
			);
		}

		return arg;
	}

	private class_161.class_162 method_10339(class_161.class_162 arg) {
		for (class_1792 lv : field_11295) {
			arg.method_709(
				class_2378.field_11142.method_10221(lv).method_12832(),
				class_2058.class_2060.method_8947(class_2073.field_9640, class_2048.field_9599, class_2073.class_2074.method_8973().method_8977(lv).method_8976())
			);
		}

		return arg;
	}

	private class_161.class_162 method_16118(class_161.class_162 arg) {
		class_1451.field_16283
			.forEach(
				(integer, arg2) -> arg.method_709(
						arg2.method_12832(), class_2131.class_2133.method_16114(class_2048.class_2049.method_8916().method_16113(arg2).method_8920())
					)
			);
		return arg;
	}
}
