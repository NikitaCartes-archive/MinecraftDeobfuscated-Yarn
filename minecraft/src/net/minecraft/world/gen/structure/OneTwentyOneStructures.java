package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.structure.TrialChamberData;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class OneTwentyOneStructures {
	public static void bootstrap(Registerable<Structure> structureRegisterable) {
		RegistryEntryLookup<Biome> registryEntryLookup = structureRegisterable.getRegistryLookup(RegistryKeys.BIOME);
		RegistryEntryLookup<StructurePool> registryEntryLookup2 = structureRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		structureRegisterable.register(
			StructureKeys.TRIAL_CHAMBERS,
			new JigsawStructure(
				Structures.createConfig(
					registryEntryLookup.getOrThrow(BiomeTags.TRIAL_CHAMBERS_HAS_STRUCTURE),
					(Map<SpawnGroup, StructureSpawns>)Arrays.stream(SpawnGroup.values())
						.collect(Collectors.toMap(group -> group, group -> new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.empty()))),
					GenerationStep.Feature.UNDERGROUND_STRUCTURES,
					StructureTerrainAdaptation.ENCAPSULATE
				),
				registryEntryLookup2.getOrThrow(TrialChamberData.CHAMBER_END_POOL_KEY),
				Optional.empty(),
				20,
				UniformHeightProvider.create(YOffset.fixed(-40), YOffset.fixed(-20)),
				false,
				Optional.empty(),
				116,
				TrialChamberData.ALIAS_BINDINGS
			)
		);
	}
}
