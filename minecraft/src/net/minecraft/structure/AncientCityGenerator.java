package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

public class AncientCityGenerator {
	public static final RegistryEntry<StructurePool> CITY_CENTER = StructurePools.register(
		new StructurePool(
			new Identifier("ancient_city/city_center"),
			new Identifier("empty"),
			ImmutableList.of(
				Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_1", StructureProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1),
				Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_2", StructureProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1),
				Pair.of(StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_3", StructureProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1)
			),
			StructurePool.Projection.RIGID
		)
	);

	public static void init() {
		AncientCityOutskirtsGenerator.init();
	}
}
