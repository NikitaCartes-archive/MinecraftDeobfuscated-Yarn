package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

public class LeavesVineTreeDecorator extends TreeDecorator {
	public static final Codec<LeavesVineTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.<LeavesVineTreeDecorator>xmap(LeavesVineTreeDecorator::new, treeDecorator -> treeDecorator.probability)
		.codec();
	private final float probability;

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.LEAVE_VINE;
	}

	public LeavesVineTreeDecorator(float probability) {
		this.probability = probability;
	}

	@Override
	public void generate(
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		AbstractRandom random,
		List<BlockPos> logPositions,
		List<BlockPos> leavesPositions,
		List<BlockPos> rootPositions
	) {
		leavesPositions.forEach(pos -> {
			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.west();
				if (Feature.isAir(world, blockPos)) {
					placeVines(world, blockPos, VineBlock.EAST, replacer);
				}
			}

			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.east();
				if (Feature.isAir(world, blockPos)) {
					placeVines(world, blockPos, VineBlock.WEST, replacer);
				}
			}

			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.north();
				if (Feature.isAir(world, blockPos)) {
					placeVines(world, blockPos, VineBlock.SOUTH, replacer);
				}
			}

			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.south();
				if (Feature.isAir(world, blockPos)) {
					placeVines(world, blockPos, VineBlock.NORTH, replacer);
				}
			}
		});
	}

	/**
	 * Places a vine at a given position and then up to 4 more vines going downwards.
	 */
	private static void placeVines(TestableWorld world, BlockPos pos, BooleanProperty facing, BiConsumer<BlockPos, BlockState> replacer) {
		placeVine(replacer, pos, facing);
		int i = 4;

		for (BlockPos var5 = pos.down(); Feature.isAir(world, var5) && i > 0; i--) {
			placeVine(replacer, var5, facing);
			var5 = var5.down();
		}
	}
}
