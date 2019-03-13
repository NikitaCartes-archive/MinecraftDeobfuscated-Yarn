package net.minecraft.block;

import java.util.Random;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class SpreadableBlock extends SnowyBlock {
	protected SpreadableBlock(Block.Settings settings) {
		super(settings);
	}

	private static boolean method_10614(ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return viewableWorld.method_8602(blockPos2) >= 4
			|| viewableWorld.method_8320(blockPos2).method_11581(viewableWorld, blockPos2) < viewableWorld.getMaxLightLevel();
	}

	private static boolean method_10613(ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return viewableWorld.method_8602(blockPos2) >= 4
			&& viewableWorld.method_8320(blockPos2).method_11581(viewableWorld, blockPos2) < viewableWorld.getMaxLightLevel()
			&& !viewableWorld.method_8316(blockPos2).method_15767(FluidTags.field_15517);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if (!method_10614(world, blockPos)) {
				world.method_8501(blockPos, Blocks.field_10566.method_9564());
			} else {
				if (world.method_8602(blockPos.up()) >= 9) {
					for (int i = 0; i < 4; i++) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						if (world.method_8320(blockPos2).getBlock() == Blocks.field_10566 && method_10613(world, blockPos2)) {
							world.method_8501(blockPos2, this.method_9564());
						}
					}
				}
			}
		}
	}
}
