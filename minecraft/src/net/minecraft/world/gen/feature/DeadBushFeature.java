package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class DeadBushFeature extends Feature<DefaultFeatureConfig> {
	private static final DeadBushBlock DEAD_BUSH = (DeadBushBlock)Blocks.field_10428;

	public DeadBushFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12869(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (BlockState blockState = iWorld.method_8320(blockPos);
			(blockState.isAir() || blockState.method_11602(BlockTags.field_15503)) && blockPos.getY() > 0;
			blockState = iWorld.method_8320(blockPos)
		) {
			blockPos = blockPos.down();
		}

		BlockState blockState2 = DEAD_BUSH.method_9564();

		for (int i = 0; i < 4; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8623(blockPos2) && blockState2.method_11591(iWorld, blockPos2)) {
				iWorld.method_8652(blockPos2, blockState2, 2);
			}
		}

		return true;
	}
}
