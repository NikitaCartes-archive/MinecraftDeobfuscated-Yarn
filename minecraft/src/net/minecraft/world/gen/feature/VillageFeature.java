package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_4183;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.generator.village.VillageGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class VillageFeature extends StructureFeature<VillageFeatureConfig> {
	public VillageFeature(Function<Dynamic<?>, ? extends VillageFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_12109().getVillageDistance();
		int n = chunkGenerator.method_12109().getVillageSeparation();
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
		ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.getBiomeSource().method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
			return chunkGenerator.method_12097(biome, Feature.field_13587);
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return VillageFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Village";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	public static class Start extends class_4183 {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			VillageFeatureConfig villageFeatureConfig = chunkGenerator.method_12105(biome, Feature.field_13587);
			BlockPos blockPos = new BlockPos(i * 16, 0, j * 16);
			VillageGenerator.method_16753(chunkGenerator, structureManager, blockPos, this.children, this.random, villageFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
