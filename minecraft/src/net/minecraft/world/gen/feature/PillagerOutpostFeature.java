package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class PillagerOutpostFeature extends JigsawFeature {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 0, true, true);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkPos chunkPos,
		StructurePoolFeatureConfig structurePoolFeatureConfig,
		HeightLimitView heightLimitView
	) {
		int i = chunkPos.x >> 4;
		int j = chunkPos.z >> 4;
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setSeed((long)(i ^ j << 4) ^ l);
		chunkRandom.nextInt();
		return chunkRandom.nextInt(5) != 0 ? false : !this.isVillageNearby(chunkGenerator, l, chunkPos);
	}

	private boolean isVillageNearby(ChunkGenerator chunkGenerator, long seed, ChunkPos pos) {
		StructureConfig structureConfig = chunkGenerator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
		if (structureConfig == null) {
			return false;
		} else {
			int i = pos.x;
			int j = pos.z;

			for (int k = i - 10; k <= i + 10; k++) {
				for (int l = j - 10; l <= j + 10; l++) {
					ChunkPos chunkPos = StructureFeature.VILLAGE.getStartChunk(structureConfig, seed, k, l);
					if (k == chunkPos.x && l == chunkPos.z) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
