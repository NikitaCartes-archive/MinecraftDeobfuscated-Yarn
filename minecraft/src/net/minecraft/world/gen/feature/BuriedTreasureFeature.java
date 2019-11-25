package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureFeature extends StructureFeature<BuriedTreasureFeatureConfig> {
	public BuriedTreasureFeature(Function<Dynamic<?>, ? extends BuriedTreasureFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkZ, int i, Biome biome) {
		if (chunkGenerator.hasStructure(biome, this)) {
			((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), chunkZ, i, 10387320);
			BuriedTreasureFeatureConfig buriedTreasureFeatureConfig = chunkGenerator.getStructureConfig(biome, this);
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
		public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
			super(structureFeature, chunkX, chunkZ, blockBox, i, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			int i = x * 16;
			int j = z * 16;
			BlockPos blockPos = new BlockPos(i + 9, 90, j + 9);
			this.children.add(new BuriedTreasureGenerator.Piece(blockPos));
			this.setBoundingBoxFromChildren();
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
		}
	}
}
