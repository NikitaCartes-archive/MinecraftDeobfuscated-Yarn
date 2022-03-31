package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.UndergroundPlacedFeatures;

public class AncientCityOutskirtsGenerator {
	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("ancient_city/structures"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofEmpty(), 7),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/barracks", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/chamber_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/chamber_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/chamber_3", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/sauna_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/small_statue", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 4),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/large_ruin_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_3", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/tall_ruin_4", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 2),
					Pair.of(
						StructurePoolElement.ofList(
							ImmutableList.of(
								StructurePoolElement.ofProcessedSingle("ancient_city/structures/camp_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION),
								StructurePoolElement.ofProcessedSingle("ancient_city/structures/camp_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION),
								StructurePoolElement.ofProcessedSingle("ancient_city/structures/camp_3", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION)
							)
						),
						1
					),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/medium_ruin_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/medium_ruin_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/small_ruin_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/small_ruin_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/large_pillar_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/structures/medium_pillar_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofList(ImmutableList.of(StructurePoolElement.ofSingle("ancient_city/structures/ice_box_1"))), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("ancient_city/sculk"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofFeature(UndergroundPlacedFeatures.field_38478), 6), Pair.of(StructurePoolElement.ofEmpty(), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("ancient_city/walls"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_corner_wall_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_intersection_wall_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_lshape_wall_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_2", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_2", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_3", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_4", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 4
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_passage_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 3
					),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_corner_wall_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_corner_wall_2", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 2
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_2", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 2
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_3", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 3
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/ruined_horizontal_wall_stairs_4", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 3
					)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("ancient_city/walls/no_corners"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_2", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_1", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_2", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_3", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_4", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_stairs_5", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/walls/intact_horizontal_wall_bridge", StructureProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION), 1
					)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("ancient_city/city_center/walls"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_left_corner", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_right_corner_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION),
						1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/bottom_right_corner_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION),
						1
					),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/left", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/right", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/top", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/top_right_corner", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1
					),
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/city_center/walls/top_left_corner", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1
					)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("ancient_city/city/entrance"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(
						StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_connector", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1
					),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_1", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_2", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_3", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_4", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city/entrance/entrance_path_5", StructureProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
