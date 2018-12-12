package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_3789;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureFeature extends StructureFeature<BuriedTreasureFeatureConfig> {
	public BuriedTreasureFeature(Function<Dynamic<?>, ? extends BuriedTreasureFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
		if (chunkGenerator.hasStructure(biome, Feature.BURIED_TREASURE)) {
			((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), i, j, 10387320);
			BuriedTreasureFeatureConfig buriedTreasureFeatureConfig = chunkGenerator.method_12105(biome, Feature.BURIED_TREASURE);
			return random.nextFloat() < buriedTreasureFeatureConfig.probability;
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return BuriedTreasureFeature.class_2957::new;
	}

	@Override
	public String getName() {
		return "Buried_Treasure";
	}

	@Override
	public int method_14021() {
		return 1;
	}

	public static class class_2957 extends StructureStart {
		public class_2957(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k + 9, 90, l + 9);
			this.children.add(new class_3789.class_3339(blockPos));
			this.method_14969();
		}

		@Override
		public BlockPos method_14962() {
			return new BlockPos((this.method_14967() << 4) + 9, 0, (this.method_14966() << 4) + 9);
		}
	}
}
