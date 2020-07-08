package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class PlainsVillageData {
	public static final StructurePool field_26253 = TemplatePools.register(
		new StructurePool(
			new Identifier("village/plains/town_centers"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.method_30426("village/plains/town_centers/plains_fountain_01", ProcessorLists.MOSSIFY_20_PERCENT), 50),
				Pair.of(StructurePoolElement.method_30426("village/plains/town_centers/plains_meeting_point_1", ProcessorLists.MOSSIFY_20_PERCENT), 50),
				Pair.of(StructurePoolElement.method_30425("village/plains/town_centers/plains_meeting_point_2"), 50),
				Pair.of(StructurePoolElement.method_30426("village/plains/town_centers/plains_meeting_point_3", ProcessorLists.MOSSIFY_70_PERCENT), 50),
				Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_fountain_01", ProcessorLists.ZOMBIE_PLAINS), 1),
				Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_meeting_point_1", ProcessorLists.ZOMBIE_PLAINS), 1),
				Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_meeting_point_2", ProcessorLists.ZOMBIE_PLAINS), 1),
				Pair.of(StructurePoolElement.method_30426("village/plains/zombie/town_centers/plains_meeting_point_3", ProcessorLists.ZOMBIE_PLAINS), 1)
			),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
	}

	static {
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/streets"),
				new Identifier("village/plains/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/corner_01", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/corner_02", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/corner_03", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_01", ProcessorLists.STREET_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_02", ProcessorLists.STREET_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_03", ProcessorLists.STREET_PLAINS), 7),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_04", ProcessorLists.STREET_PLAINS), 7),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_05", ProcessorLists.STREET_PLAINS), 3),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/straight_06", ProcessorLists.STREET_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_01", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_02", ProcessorLists.STREET_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_03", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_04", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_05", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/crossroad_06", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/streets/turn_01", ProcessorLists.STREET_PLAINS), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/zombie/streets"),
				new Identifier("village/plains/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/corner_01", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/corner_02", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/corner_03", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_01", ProcessorLists.STREET_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_02", ProcessorLists.STREET_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_03", ProcessorLists.STREET_PLAINS), 7),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_04", ProcessorLists.STREET_PLAINS), 7),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_05", ProcessorLists.STREET_PLAINS), 3),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/straight_06", ProcessorLists.STREET_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_01", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_02", ProcessorLists.STREET_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_03", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_04", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_05", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/crossroad_06", ProcessorLists.STREET_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/streets/turn_01", ProcessorLists.STREET_PLAINS), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/houses"),
				new Identifier("village/plains/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_2", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_3", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_4", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_5", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_6", ProcessorLists.MOSSIFY_10_PERCENT), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_7", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_house_8", ProcessorLists.MOSSIFY_10_PERCENT), 3),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_medium_house_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_medium_house_2", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_big_house_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_butcher_shop_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_butcher_shop_2", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tool_smith_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_fletcher_house_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_shepherds_house_1"), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_armorer_house_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_fisher_cottage_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tannery_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_cartographer_1", ProcessorLists.MOSSIFY_10_PERCENT), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_1", ProcessorLists.MOSSIFY_10_PERCENT), 5),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_2", ProcessorLists.MOSSIFY_10_PERCENT), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_masons_house_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_weaponsmith_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_3", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_4", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_stable_1", ProcessorLists.MOSSIFY_10_PERCENT), 2),
					Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_stable_2"), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_large_farm_1", ProcessorLists.FARM_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_farm_1", ProcessorLists.FARM_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_animal_pen_1"), 1),
					Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_animal_pen_2"), 1),
					Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_animal_pen_3"), 5),
					Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_accessory_1"), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_meeting_point_4", ProcessorLists.MOSSIFY_70_PERCENT), 3),
					Pair.of(StructurePoolElement.method_30425("village/plains/houses/plains_meeting_point_5"), 1),
					Pair.of(StructurePoolElement.method_30438(), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/zombie/houses"),
				new Identifier("village/plains/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_2", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_3", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_4", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_5", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_6", ProcessorLists.ZOMBIE_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_7", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_small_house_8", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_medium_house_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_medium_house_2", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_big_house_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_butcher_shop_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_butcher_shop_2", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tool_smith_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_fletcher_house_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_shepherds_house_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_armorer_house_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_fisher_cottage_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_tannery_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_cartographer_1", ProcessorLists.ZOMBIE_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_1", ProcessorLists.ZOMBIE_PLAINS), 3),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_library_2", ProcessorLists.ZOMBIE_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_masons_house_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_weaponsmith_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_3", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_temple_4", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_stable_1", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_stable_2", ProcessorLists.ZOMBIE_PLAINS), 2),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_large_farm_1", ProcessorLists.ZOMBIE_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_small_farm_1", ProcessorLists.ZOMBIE_PLAINS), 4),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_animal_pen_1", ProcessorLists.ZOMBIE_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/houses/plains_animal_pen_2", ProcessorLists.ZOMBIE_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_animal_pen_3", ProcessorLists.ZOMBIE_PLAINS), 5),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_meeting_point_4", ProcessorLists.ZOMBIE_PLAINS), 3),
					Pair.of(StructurePoolElement.method_30426("village/plains/zombie/houses/plains_meeting_point_5", ProcessorLists.ZOMBIE_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30438(), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_01", ProcessorLists.STREET_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_02", ProcessorLists.STREET_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_03", ProcessorLists.STREET_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_04", ProcessorLists.STREET_PLAINS), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/trees"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/plains/plains_lamp_1"), 2),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), 1),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.FLOWER_PLAIN), 1),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), 1),
					Pair.of(StructurePoolElement.method_30438(), 2)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/zombie/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/plains/plains_lamp_1", ProcessorLists.ZOMBIE_PLAINS), 1),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), 1),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.FLOWER_PLAIN), 1),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), 1),
					Pair.of(StructurePoolElement.method_30438(), 2)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/plains/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/plains/villagers/baby"), 1),
					Pair.of(StructurePoolElement.method_30425("village/plains/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/plains/zombie/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/plains/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/plains/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/common/animals"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cows_1"), 7),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/pigs_1"), 7),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_1"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_2"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_3"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_4"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/horses_5"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_1"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_2"), 1),
					Pair.of(StructurePoolElement.method_30438(), 5)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/common/sheep"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_1"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_2"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/common/cats"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_black"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_british"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_calico"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_persian"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_ragdoll"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_red"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_siamese"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_tabby"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_white"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cat_jellie"), 1),
					Pair.of(StructurePoolElement.method_30438(), 3)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/common/butcher_animals"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/common/animals/cows_1"), 3),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/pigs_1"), 3),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_1"), 1),
					Pair.of(StructurePoolElement.method_30425("village/common/animals/sheep_2"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/common/iron_golem"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30425("village/common/iron_golem"), 1)),
				StructurePool.Projection.RIGID
			)
		);
		TemplatePools.register(
			new StructurePool(
				new Identifier("village/common/well_bottoms"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.method_30425("village/common/well_bottom"), 1)),
				StructurePool.Projection.RIGID
			)
		);
	}
}
