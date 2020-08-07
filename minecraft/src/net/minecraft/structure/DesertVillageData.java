package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class DesertVillageData {
	public static final StructurePool field_25948 = StructurePools.register(
		new StructurePool(
			new Identifier("village/desert/town_centers"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.method_30425("village/desert/town_centers/desert_meeting_point_1"), 98),
				Pair.of(StructurePoolElement.method_30425("village/desert/town_centers/desert_meeting_point_2"), 98),
				Pair.of(StructurePoolElement.method_30425("village/desert/town_centers/desert_meeting_point_3"), 49),
				Pair.of(StructurePoolElement.method_30426("village/desert/zombie/town_centers/desert_meeting_point_1", StructureProcessorLists.field_26263), 2),
				Pair.of(StructurePoolElement.method_30426("village/desert/zombie/town_centers/desert_meeting_point_2", StructureProcessorLists.field_26263), 2),
				Pair.of(StructurePoolElement.method_30426("village/desert/zombie/town_centers/desert_meeting_point_3", StructureProcessorLists.field_26263), 1)
			),
			StructurePool.Projection.field_16687
		)
	);

	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/streets"),
				new Identifier("village/desert/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/corner_01"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/corner_02"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/straight_01"), 4),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/straight_02"), 4),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/straight_03"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/crossroad_01"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/crossroad_02"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/crossroad_03"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/square_01"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/square_02"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/streets/turn_01"), 3)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/zombie/streets"),
				new Identifier("village/desert/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/corner_01"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/corner_02"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/straight_01"), 4),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/straight_02"), 4),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/straight_03"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/crossroad_01"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/crossroad_02"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/crossroad_03"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/square_01"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/square_02"), 3),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/streets/turn_01"), 3)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/houses"),
				new Identifier("village/desert/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_3"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_4"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_5"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_6"), 1),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_7"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_small_house_8"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_medium_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_medium_house_2"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_butcher_shop_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_tool_smith_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_fletcher_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_shepherd_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_armorer_1"), 1),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_fisher_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_tannery_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_cartographer_house_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_library_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_mason_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_weaponsmith_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_temple_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_temple_2"), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_large_farm_1", StructureProcessorLists.field_26274), 11),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_farm_1", StructureProcessorLists.field_26274), 4),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_farm_2", StructureProcessorLists.field_26274), 4),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_animal_pen_1"), 2),
					Pair.of(StructurePoolElement.method_30425("village/desert/houses/desert_animal_pen_2"), 2),
					Pair.of(StructurePoolElement.method_30438(), 5)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/zombie/houses"),
				new Identifier("village/desert/zombie/terminators"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_2", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_3", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_4", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_5", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_6", StructureProcessorLists.field_26263), 1),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_7", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_small_house_8", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_medium_house_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/zombie/houses/desert_medium_house_2", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_butcher_shop_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_tool_smith_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_fletcher_house_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_shepherd_house_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_armorer_1", StructureProcessorLists.field_26263), 1),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_fisher_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_tannery_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_cartographer_house_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_library_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_mason_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_weaponsmith_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_temple_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_temple_2", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_large_farm_1", StructureProcessorLists.field_26263), 7),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_farm_1", StructureProcessorLists.field_26263), 4),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_farm_2", StructureProcessorLists.field_26263), 4),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_animal_pen_1", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30426("village/desert/houses/desert_animal_pen_2", StructureProcessorLists.field_26263), 2),
					Pair.of(StructurePoolElement.method_30438(), 5)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/terminators/terminator_01"), 1),
					Pair.of(StructurePoolElement.method_30425("village/desert/terminators/terminator_02"), 1)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/zombie/terminators"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/terminators/terminator_01"), 1),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/terminators/terminator_02"), 1)
				),
				StructurePool.Projection.field_16686
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/desert_lamp_1"), 10),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_25989), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26009), 4),
					Pair.of(StructurePoolElement.method_30438(), 10)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/zombie/decor"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30426("village/desert/desert_lamp_1", StructureProcessorLists.field_26263), 10),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_25989), 4),
					Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.field_26009), 4),
					Pair.of(StructurePoolElement.method_30438(), 10)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/desert/villagers/baby"), 1),
					Pair.of(StructurePoolElement.method_30425("village/desert/villagers/unemployed"), 10)
				),
				StructurePool.Projection.field_16687
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("village/desert/zombie/villagers"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/villagers/nitwit"), 1),
					Pair.of(StructurePoolElement.method_30425("village/desert/zombie/villagers/unemployed"), 10)
				),
				StructurePool.Projection.field_16687
			)
		);
	}
}
