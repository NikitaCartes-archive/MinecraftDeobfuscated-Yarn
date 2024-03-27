package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class LeavesVineTreeDecorator extends TreeDecorator {
	public static final MapCodec<LeavesVineTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.xmap(LeavesVineTreeDecorator::new, treeDecorator -> treeDecorator.probability);
	private final float probability;

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.LEAVE_VINE;
	}

	public LeavesVineTreeDecorator(float probability) {
		this.probability = probability;
	}

	@Override
	public void generate(TreeDecorator.Generator generator) {
		Random random = generator.getRandom();
		generator.getLeavesPositions().forEach(pos -> {
			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.west();
				if (generator.isAir(blockPos)) {
					placeVines(blockPos, VineBlock.EAST, generator);
				}
			}

			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.east();
				if (generator.isAir(blockPos)) {
					placeVines(blockPos, VineBlock.WEST, generator);
				}
			}

			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.north();
				if (generator.isAir(blockPos)) {
					placeVines(blockPos, VineBlock.SOUTH, generator);
				}
			}

			if (random.nextFloat() < this.probability) {
				BlockPos blockPos = pos.south();
				if (generator.isAir(blockPos)) {
					placeVines(blockPos, VineBlock.NORTH, generator);
				}
			}
		});
	}

	/**
	 * Places a vine at a given position and then up to 4 more vines going downwards.
	 */
	private static void placeVines(BlockPos pos, BooleanProperty faceProperty, TreeDecorator.Generator generator) {
		generator.replaceWithVine(pos, faceProperty);
		int i = 4;

		for (BlockPos var4 = pos.down(); generator.isAir(var4) && i > 0; i--) {
			generator.replaceWithVine(var4, faceProperty);
			var4 = var4.down();
		}
	}
}
