package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class PillagerOutpostFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
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
		DefaultFeatureConfig defaultFeatureConfig
	) {
		int k = i >> 4;
		int m = j >> 4;
		chunkRandom.setSeed((long)(k ^ m << 4) ^ l);
		chunkRandom.nextInt();
		if (chunkRandom.nextInt(5) != 0) {
			return false;
		} else {
			for (int n = i - 10; n <= i + 10; n++) {
				for (int o = j - 10; o <= j + 10; o++) {
					ChunkPos chunkPos2 = StructureFeature.VILLAGE.method_27218(chunkGenerator.getConfig().method_28600(StructureFeature.VILLAGE), l, chunkRandom, n, o);
					if (n == chunkPos2.x && o == chunkPos2.z) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return PillagerOutpostFeature.Start::new;
	}

	public static class Start extends VillageStructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, DefaultFeatureConfig defaultFeatureConfig) {
			BlockPos blockPos = new BlockPos(i * 16, 0, j * 16);
			PillagerOutpostGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random);
			this.setBoundingBoxFromChildren();
		}
	}
}
