package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class PillagerOutpostFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, Function<Random, ? extends DefaultFeatureConfig> function2) {
		super(function, function2);
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
	public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ, Biome biome) {
		ChunkPos chunkPos = this.getStart(chunkGenerator, random, chunkX, chunkZ, 0, 0);
		if (chunkX == chunkPos.x && chunkZ == chunkPos.z) {
			int i = chunkX >> 4;
			int j = chunkZ >> 4;
			random.setSeed((long)(i ^ j << 4) ^ chunkGenerator.getSeed());
			random.nextInt();
			if (random.nextInt(5) != 0) {
				return false;
			}

			if (chunkGenerator.hasStructure(biome, this)) {
				for (int k = chunkX - 10; k <= chunkX + 10; k++) {
					for (int l = chunkZ - 10; l <= chunkZ + 10; l++) {
						if (Feature.VILLAGE.shouldStartAt(biomeAccess, chunkGenerator, random, k, l, biomeAccess.getBiome(new BlockPos((k << 4) + 9, 0, (l << 4) + 9)))) {
							return false;
						}
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return PillagerOutpostFeature.Start::new;
	}

	@Override
	protected int getSeedModifier() {
		return 165745296;
	}

	public static class Start extends VillageStructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			BlockPos blockPos = new BlockPos(x * 16, 90, z * 16);
			PillagerOutpostGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random);
			this.setBoundingBoxFromChildren();
		}
	}
}
