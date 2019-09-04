package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_4543;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class PillagerOutpostFeature extends AbstractTempleFeature<PillagerOutpostFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Function<Dynamic<?>, ? extends PillagerOutpostFeatureConfig> function) {
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
	public boolean shouldStartAt(class_4543 arg, ChunkGenerator<?> chunkGenerator, Random random, int i, int j, Biome biome) {
		ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			int k = i >> 4;
			int l = j >> 4;
			random.setSeed((long)(k ^ l << 4) ^ chunkGenerator.getSeed());
			random.nextInt();
			if (random.nextInt(5) != 0) {
				return false;
			}

			if (chunkGenerator.hasStructure(biome, Feature.PILLAGER_OUTPOST)) {
				for (int m = i - 10; m <= i + 10; m++) {
					for (int n = j - 10; n <= j + 10; n++) {
						if (Feature.VILLAGE.shouldStartAt(arg, chunkGenerator, random, m, n, arg.method_22393(new BlockPos((m << 4) + 9, 0, (n << 4) + 9)))) {
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
		public Start(StructureFeature<?> structureFeature, int i, int j, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			BlockPos blockPos = new BlockPos(i * 16, 90, j * 16);
			PillagerOutpostGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random);
			this.setBoundingBoxFromChildren();
		}
	}
}
