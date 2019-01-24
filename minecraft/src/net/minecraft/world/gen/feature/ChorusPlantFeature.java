package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class ChorusPlantFeature extends Feature<DefaultFeatureConfig> {
	public ChorusPlantFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12843(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (iWorld.isAir(blockPos.up()) && iWorld.getBlockState(blockPos).getBlock() == Blocks.field_10471) {
			ChorusFlowerBlock.generate(iWorld, blockPos.up(), random, 8);
			return true;
		} else {
			return false;
		}
	}
}
