package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class PillagerOutpostFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Pillager_Outpost";
	}

	@Override
	public int getRadius() {
		return 3;
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return MONSTER_SPAWNS;
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator chunkGenerator, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos
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
					Biome biome2 = biomeAccess.getBiome(new BlockPos((n << 4) + 9, 0, (o << 4) + 9));
					if (Feature.VILLAGE.method_27217(biomeAccess, chunkGenerator, l, chunkRandom, n, o, biome2)) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return PillagerOutpostFeature.Start::new;
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 165745296;
	}

	public static class Start extends VillageStructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
			PillagerOutpostGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random);
			this.setBoundingBoxFromChildren();
		}
	}
}
