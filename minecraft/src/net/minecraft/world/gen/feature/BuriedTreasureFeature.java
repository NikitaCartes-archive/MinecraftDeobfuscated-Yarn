package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.BuriedTreasureGenerator;
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
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		Biome biome = chunkGenerator.getBiomeSource().method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
		if (chunkGenerator.method_12097(biome, Feature.field_13538)) {
			((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), i, j, 10387320);
			BuriedTreasureFeatureConfig buriedTreasureFeatureConfig = chunkGenerator.method_12105(biome, Feature.field_13538);
			return random.nextFloat() < buriedTreasureFeatureConfig.probability;
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return BuriedTreasureFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Buried_Treasure";
	}

	@Override
	public int getRadius() {
		return 1;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k + 9, 90, l + 9);
			this.children.add(new BuriedTreasureGenerator.Piece(blockPos));
			this.setBoundingBoxFromChildren();
		}

		@Override
		public BlockPos method_14962() {
			return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
		}
	}
}
