package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;

public class PillagerOutpostFeature extends JigsawFeature {
	private static final List<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = ImmutableList.of(new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> codec) {
		super(codec, 0, true, true);
	}

	@Override
	public List<SpawnSettings.SpawnEntry> getMonsterSpawns() {
		return MONSTER_SPAWNS;
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		int i,
		int j,
		Biome biome,
		ChunkPos chunkPos,
		StructurePoolFeatureConfig structurePoolFeatureConfig
	) {
		int k = i >> 4;
		int m = j >> 4;
		chunkRandom.setSeed((long)(k ^ m << 4) ^ l);
		chunkRandom.nextInt();
		return chunkRandom.nextInt(5) != 0 ? false : !this.method_30845(chunkGenerator, l, chunkRandom, i, j);
	}

	private boolean method_30845(ChunkGenerator chunkGenerator, long l, ChunkRandom chunkRandom, int i, int j) {
		StructureConfig structureConfig = chunkGenerator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
		if (structureConfig == null) {
			return false;
		} else {
			for (int k = i - 10; k <= i + 10; k++) {
				for (int m = j - 10; m <= j + 10; m++) {
					ChunkPos chunkPos = StructureFeature.VILLAGE.getStartChunk(structureConfig, l, chunkRandom, k, m);
					if (k == chunkPos.x && m == chunkPos.z) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
