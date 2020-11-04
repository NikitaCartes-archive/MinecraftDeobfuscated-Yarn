package net.minecraft.world.gen.tree;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
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
		StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> placedStates, BlockBox box
	) {
		logPositions.forEach(pos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.west();
				if (Feature.isAir(world, blockPos)) {
					this.placeVine(world, blockPos, VineBlock.EAST, placedStates, box);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.east();
				if (Feature.isAir(world, blockPos)) {
					this.placeVine(world, blockPos, VineBlock.WEST, placedStates, box);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.north();
				if (Feature.isAir(world, blockPos)) {
					this.placeVine(world, blockPos, VineBlock.SOUTH, placedStates, box);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.south();
				if (Feature.isAir(world, blockPos)) {
					this.placeVine(world, blockPos, VineBlock.NORTH, placedStates, box);
				}
			}
		});
	}
}
