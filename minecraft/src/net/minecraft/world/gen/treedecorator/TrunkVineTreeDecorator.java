package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
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
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		AbstractRandom random,
		List<BlockPos> logPositions,
		List<BlockPos> leavesPositions,
		List<BlockPos> rootPositions
	) {
		logPositions.forEach(pos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.west();
				if (Feature.isAir(world, blockPos)) {
					placeVine(replacer, blockPos, VineBlock.EAST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.east();
				if (Feature.isAir(world, blockPos)) {
					placeVine(replacer, blockPos, VineBlock.WEST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.north();
				if (Feature.isAir(world, blockPos)) {
					placeVine(replacer, blockPos, VineBlock.SOUTH);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.south();
				if (Feature.isAir(world, blockPos)) {
					placeVine(replacer, blockPos, VineBlock.NORTH);
				}
			}
		});
	}
}
