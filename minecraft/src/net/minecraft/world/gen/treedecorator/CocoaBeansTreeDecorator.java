package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

public class CocoaBeansTreeDecorator extends TreeDecorator {
	public static final Codec<CocoaBeansTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.<CocoaBeansTreeDecorator>xmap(CocoaBeansTreeDecorator::new, cocoaBeansTreeDecorator -> cocoaBeansTreeDecorator.probability)
		.codec();
	private final float probability;

	public CocoaBeansTreeDecorator(float probability) {
		this.probability = probability;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.COCOA;
	}

	@Override
	public void generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list
	) {
		if (!(random.nextFloat() >= this.probability)) {
			int i = ((BlockPos)leavesPositions.get(0)).getY();
			leavesPositions.stream().filter(pos -> pos.getY() - i <= 2).forEach(blockPos -> {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					if (random.nextFloat() <= 0.25F) {
						Direction direction2 = direction.getOpposite();
						BlockPos blockPos2 = blockPos.add(direction2.getOffsetX(), 0, direction2.getOffsetZ());
						if (Feature.isAir(testableWorld, blockPos2)) {
							biConsumer.accept(blockPos2, Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, Integer.valueOf(random.nextInt(3))).with(CocoaBlock.FACING, direction));
						}
					}
				}
			});
		}
	}
}
