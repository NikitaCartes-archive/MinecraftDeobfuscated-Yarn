package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class WildCropFeature extends Feature<DefaultFeatureConfig> {
	protected final BlockState crop;

	public WildCropFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, BlockState blockState) {
		super(function);
		this.crop = blockState;
	}

	public boolean method_13651(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		int i = 0;

		for (int j = 0; j < 64; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.isAir(blockPos2) && iWorld.getBlockState(blockPos2.down()).getBlock() == Blocks.field_10219) {
				iWorld.setBlockState(blockPos2, this.crop, 2);
				i++;
			}
		}

		return i > 0;
	}
}
