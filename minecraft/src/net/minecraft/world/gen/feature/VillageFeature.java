package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.village.VillageGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class VillageFeature extends StructureFeature<NewVillageFeatureConfig> {
	public VillageFeature(Function<Dynamic<?>, ? extends NewVillageFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.getSettings().getVillageDistance();
		int n = chunkGenerator.getSettings().method_12559();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387312);
		s *= m;
		t *= m;
		s += random.nextInt(m - n);
		t += random.nextInt(m - n);
		return new ChunkPos(s, t);
	}

	@Override
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_14018(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
			return chunkGenerator.hasStructure(biome, Feature.VILLAGE);
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return VillageFeature.class_3212::new;
	}

	@Override
	public String getName() {
		return "Village";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	public static class class_3212 extends StructureStart {
		public class_3212(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			NewVillageFeatureConfig newVillageFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.VILLAGE);
			BlockPos blockPos = new BlockPos(i * 16, 0, j * 16);
			VillageGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random, newVillageFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
