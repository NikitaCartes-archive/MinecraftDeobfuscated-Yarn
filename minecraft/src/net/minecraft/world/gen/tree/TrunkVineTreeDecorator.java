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
	public void generate(StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
		logPositions.forEach(blockPos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.west();
				if (Feature.isAir(world, blockPos2)) {
					this.placeVine(world, blockPos2, VineBlock.EAST, set, box);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.east();
				if (Feature.isAir(world, blockPos2)) {
					this.placeVine(world, blockPos2, VineBlock.WEST, set, box);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.north();
				if (Feature.isAir(world, blockPos2)) {
					this.placeVine(world, blockPos2, VineBlock.SOUTH, set, box);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.south();
				if (Feature.isAir(world, blockPos2)) {
					this.placeVine(world, blockPos2, VineBlock.NORTH, set, box);
				}
			}
		});
	}
}