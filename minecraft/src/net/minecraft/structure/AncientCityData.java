package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

public class AncientCityData {
	public static final StructurePool CITY_CENTER = StructurePools.register(
		new StructurePool(
			new Identifier("ancient_city/city_center"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center", StructureProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1)
			),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
		AncientCityOutskirtsData.init();
	}
}
