package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.VillagePlacedFeatures;

public class SnowyVillageData {
	public static final RegistryEntry<StructurePool> STRUCTURE_POOLS = StructurePools.register(
		new StructurePool(
			new Identifier("village/snowy/town_centers"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/town_centers/snowy_meeting_point_1"), 100),
				Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/town_centers/snowy_meeting_point_2"), 50),
				Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/town_centers/snowy_meeting_point_3"), 150),
				Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/town_centers/snowy_meeting_point_1"), 2),
				Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/town_centers/snowy_meeting_point_2"), 1),
				Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/town_centers/snowy_meeting_point_3"), 3)
			),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/streets"),
				new Identifier("village/snowy/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/corner_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/corner_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/corner_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/square_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/straight_08", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/crossroad_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/streets/turn_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/zombie/streets"),
				new Identifier("village/snowy/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/corner_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/corner_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/corner_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/square_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 7),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/straight_08", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 4),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_05", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/crossroad_06", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/streets/turn_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 3)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/houses"),
				new Identifier("village/snowy/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_3"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_4"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_5"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_6"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_7"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_small_house_8"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_medium_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_medium_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_medium_house_3"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_butchers_shop_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_butchers_shop_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_tool_smith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_fletcher_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_shepherds_house_1"), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_armorer_house_1"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_armorer_house_2"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_fisher_cottage"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_tannery_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_cartographer_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_library_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_masons_house_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_masons_house_2"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_weapon_smith_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_temple_1"), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_1", StructureProcessorLists.FARM_SNOWY), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_2", StructureProcessorLists.FARM_SNOWY), 3),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/houses/snowy_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/zombie/houses"),
				new Identifier("village/snowy/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_2", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_3", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_4", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_5", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_6", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_7", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_small_house_8", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_medium_house_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_medium_house_2", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/zombie/houses/snowy_medium_house_3", StructureProcessorLists.ZOMBIE_SNOWY), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_butchers_shop_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_butchers_shop_2", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_tool_smith_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_fletcher_house_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_shepherds_house_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_armorer_house_1", StructureProcessorLists.ZOMBIE_SNOWY), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_armorer_house_2", StructureProcessorLists.ZOMBIE_SNOWY), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_fisher_cottage", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_tannery_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_cartographer_house_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_library_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_masons_house_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_masons_house_2", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_weapon_smith_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_temple_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_1", StructureProcessorLists.ZOMBIE_SNOWY), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_farm_2", StructureProcessorLists.ZOMBIE_SNOWY), 3),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_animal_pen_1", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/houses/snowy_animal_pen_2", StructureProcessorLists.ZOMBIE_SNOWY), 2),
					Pair.of(StructurePoolElement.ofEmpty(), 6)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_01", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_02", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_03", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/plains/terminators/terminator_04", StructureProcessorLists.STREET_SNOWY_OR_TAIGA), 1)
				),
				StructurePool.Projection.TERRAIN_MATCHING
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/trees"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.SPRUCE), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/snowy_lamp_post_01"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/snowy_lamp_post_02"), 4),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/snowy_lamp_post_03"), 1),
					Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.SPRUCE), 4),
					Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PILE_SNOW), 4),
					Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PILE_ICE), 1),
					Pair.of(StructurePoolElement.ofEmpty(), 9)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/zombie/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/snowy_lamp_post_01", StructureProcessorLists.ZOMBIE_SNOWY), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/snowy_lamp_post_02", StructureProcessorLists.ZOMBIE_SNOWY), 1),
					Pair.of(StructurePoolElement.ofProcessedLegacySingle("village/snowy/snowy_lamp_post_03", StructureProcessorLists.ZOMBIE_SNOWY), 1),
					Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.SPRUCE), 4),
					Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PILE_SNOW), 4),
					Pair.of(StructurePoolElement.ofFeature(VillagePlacedFeatures.PILE_ICE), 4),
					Pair.of(StructurePoolElement.ofEmpty(), 7)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/villagers/baby"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/snowy/zombie/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.ofLegacySingle("village/snowy/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
