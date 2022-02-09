package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class PillagerOutpostFeature extends JigsawFeature {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 0, true, true, PillagerOutpostFeature::canGenerate);
	}

	private static boolean canGenerate(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
		int i = context.chunkPos().x >> 4;
		int j = context.chunkPos().z >> 4;
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setSeed((long)(i ^ j << 4) ^ context.seed());
		chunkRandom.nextInt();
		return chunkRandom.nextInt(5) != 0 ? false : !isVillageNearby(context.chunkGenerator(), context.seed(), context.chunkPos());
	}

	private static boolean isVillageNearby(ChunkGenerator chunkGenerator, long seed, ChunkPos chunkPos) {
		StructurePlacement structurePlacement = chunkGenerator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
		if (structurePlacement != null) {
			int i = chunkPos.x;
			int j = chunkPos.z;

			for (int k = i - 10; k <= i + 10; k++) {
				for (int l = j - 10; l <= j + 10; l++) {
					if (structurePlacement.isStartChunk(chunkGenerator, k, l)) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
