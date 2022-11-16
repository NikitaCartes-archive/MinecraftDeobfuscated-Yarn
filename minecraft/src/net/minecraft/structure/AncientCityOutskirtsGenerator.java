package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.UndergroundPlacedFeatures;

public class AncientCityOutskirtsGenerator {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntry<PlacedFeature> registryEntry = registryEntryLookup.getOrThrow(UndergroundPlacedFeatures.SCULK_PATCH_ANCIENT_CITY);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry2 = registryEntryLookup2.getOrThrow(StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION);
		RegistryEntry<StructureProcessorList> registryEntry3 = registryEntryLookup2.getOrThrow(StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION);
		RegistryEntryLookup<StructurePool> registryEntryLookup3 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry4 = registryEntryLookup3.getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"ancient_city/structures",
			new StructurePool(
				registryEntry4,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofEmpty(), 7),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/barracks", registryEntry2), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/chamber_1", registryEntry2), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/chamber_2", registryEntry2), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/chamber_3", registryEntry2), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/sauna_1", registryEntry2), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/small_statue", registryEntry2), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/large_ruin_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_3", registryEntry2), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_4", registryEntry2), 2),
					Pair.of(
						StructurePoolElement.ofList(
							ImmutableList.of(
								StructurePoolElement.ofProcessedSingle("ancient_city/structures/camp_1", registryEntry2),
								StructurePoolElement.ofProcessedSingle("ancient_city/structures/camp_2", registryEntry2),
								StructurePoolElement.ofProcessedSingle("ancient_city/structures/camp_3", registryEntry2)
							)
						),
						1
					),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/medium_ruin_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/medium_ruin_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/small_ruin_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/small_ruin_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/large_pillar_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/medium_pillar_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofList(ImmutableList.of(StructurePoolElement.ofSingle("ancient_city/structures/ice_box_1"))), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"ancient_city/sculk",
			new StructurePool(
				registryEntry4,
				ImmutableList.of(Pair.of(StructurePoolElement.ofFeature(registryEntry), 6), Pair.of(StructurePoolElement.ofEmpty(), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"ancient_city/walls",
			new StructurePool(
				registryEntry4,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_corner_wall_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_intersection_wall_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_lshape_wall_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_2", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_2", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_3", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_4", registryEntry3), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_passage_1", registryEntry3), 3),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_corner_wall_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_corner_wall_2", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_1", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_2", registryEntry3), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_3", registryEntry3), 3),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_4", registryEntry3), 3)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"ancient_city/walls/no_corners",
			new StructurePool(
				registryEntry4,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_2", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_1", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_2", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_3", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_4", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_5", registryEntry3), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_bridge", registryEntry3), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"ancient_city/city_center/walls",
			new StructurePool(
				registryEntry4,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_left_corner", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_right_corner_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_right_corner_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/left", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/right", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/top", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/top_right_corner", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/top_left_corner", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"ancient_city/city/entrance",
			new StructurePool(
				registryEntry4,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_connector", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_1", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_2", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_3", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_4", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_5", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
