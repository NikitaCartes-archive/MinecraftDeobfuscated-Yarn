package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

public class LeavesVineTreeDecorator extends TreeDecorator {
	public static final Codec<LeavesVineTreeDecorator> CODEC = Codec.unit((Supplier<LeavesVineTreeDecorator>)(() -> LeavesVineTreeDecorator.INSTANCE));
	public static final LeavesVineTreeDecorator INSTANCE = new LeavesVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.LEAVE_VINE;
	}

	@Override
	public void generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list
	) {
		list.forEach(blockPos -> {
			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.west();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVines(testableWorld, blockPos2, VineBlock.EAST, biConsumer);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.east();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVines(testableWorld, blockPos2, VineBlock.WEST, biConsumer);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.north();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVines(testableWorld, blockPos2, VineBlock.SOUTH, biConsumer);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.south();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVines(testableWorld, blockPos2, VineBlock.NORTH, biConsumer);
				}
			}
		});
	}

	/**
	 * Places a vine at a given position and then up to 4 more vines going downwards.
	 */
	private static void placeVines(TestableWorld testableWorld, BlockPos blockPos, BooleanProperty booleanProperty, BiConsumer<BlockPos, BlockState> biConsumer) {
		placeVine(biConsumer, blockPos, booleanProperty);
		int i = 4;

		for (BlockPos var5 = blockPos.down(); Feature.isAir(testableWorld, var5) && i > 0; i--) {
			placeVine(biConsumer, var5, booleanProperty);
			var5 = var5.down();
		}
	}
}
