package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.Feature;

public class CocoaBeansTreeDecorator extends TreeDecorator {
	public static final Codec<CocoaBeansTreeDecorator> CODEC = Codec.FLOAT
		.fieldOf("probability")
		.<CocoaBeansTreeDecorator>xmap(CocoaBeansTreeDecorator::new, cocoaBeansTreeDecorator -> cocoaBeansTreeDecorator.field_21318)
		.codec();
	private final float field_21318;

	public CocoaBeansTreeDecorator(float f) {
		this.field_21318 = f;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.COCOA;
	}

	@Override
	public void generate(
		ServerWorldAccess serverWorldAccess, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box
	) {
		if (!(random.nextFloat() >= this.field_21318)) {
			int i = ((BlockPos)logPositions.get(0)).getY();
			logPositions.stream().filter(blockPos -> blockPos.getY() - i <= 2).forEach(blockPos -> {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					if (random.nextFloat() <= 0.25F) {
						Direction direction2 = direction.getOpposite();
						BlockPos blockPos2 = blockPos.add(direction2.getOffsetX(), 0, direction2.getOffsetZ());
						if (Feature.isAir(serverWorldAccess, blockPos2)) {
							BlockState blockState = Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, Integer.valueOf(random.nextInt(3))).with(CocoaBlock.FACING, direction);
							this.setBlockStateAndEncompassPosition(serverWorldAccess, blockPos2, blockState, set, box);
						}
					}
				}
			});
		}
	}
}
