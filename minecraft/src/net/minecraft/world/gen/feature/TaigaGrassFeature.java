package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class TaigaGrassFeature extends Feature<DefaultFeatureConfig> {
	public TaigaGrassFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public BlockState getGrass(Random random) {
		return random.nextInt(5) > 0 ? Blocks.field_10112.getDefaultState() : Blocks.field_10479.getDefaultState();
	}

	public boolean method_14061(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockState blockState = this.getGrass(random);

		for (BlockState blockState2 = iWorld.getBlockState(blockPos);
			(blockState2.isAir() || blockState2.matches(BlockTags.field_15503)) && blockPos.getY() > 0;
			blockState2 = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		int i = 0;

		for (int j = 0; j < 128; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.isAir(blockPos2) && blockState.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
