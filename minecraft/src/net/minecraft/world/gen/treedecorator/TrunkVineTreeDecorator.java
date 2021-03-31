package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

public class TrunkVineTreeDecorator extends TreeDecorator {
	public static final Codec<TrunkVineTreeDecorator> CODEC = Codec.unit((Supplier<TrunkVineTreeDecorator>)(() -> TrunkVineTreeDecorator.INSTANCE));
	public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.TRUNK_VINE;
	}

	@Override
	public void generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list
	) {
		leavesPositions.forEach(blockPos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.west();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVine(biConsumer, blockPos2, VineBlock.EAST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.east();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVine(biConsumer, blockPos2, VineBlock.WEST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.north();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVine(biConsumer, blockPos2, VineBlock.SOUTH);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.south();
				if (Feature.isAir(testableWorld, blockPos2)) {
					placeVine(biConsumer, blockPos2, VineBlock.NORTH);
				}
			}
		});
	}
}
