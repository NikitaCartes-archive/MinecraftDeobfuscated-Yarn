package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureFeature extends StructureFeature<BuriedTreasureFeatureConfig> {
	public BuriedTreasureFeature(Function<Dynamic<?>, ? extends BuriedTreasureFeatureConfig> function) {
		super(function);
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos
	) {
		chunkRandom.setRegionSeed(chunkGenerator.getSeed(), chunkX, chunkZ, 10387320);
		BuriedTreasureFeatureConfig buriedTreasureFeatureConfig = chunkGenerator.getStructureConfig(biome, this);
		return chunkRandom.nextFloat() < buriedTreasureFeatureConfig.probability;
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
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
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
