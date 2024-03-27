package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class CocoaBeansTreeDecorator extends TreeDecorator {
	public static final MapCodec<CocoaBeansTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
		.fieldOf("probability")
		.xmap(CocoaBeansTreeDecorator::new, decorator -> decorator.probability);
	private final float probability;

	public CocoaBeansTreeDecorator(float probability) {
		this.probability = probability;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.COCOA;
	}

	@Override
	public void generate(TreeDecorator.Generator generator) {
		Random random = generator.getRandom();
		if (!(random.nextFloat() >= this.probability)) {
			List<BlockPos> list = generator.getLogPositions();
			int i = ((BlockPos)list.get(0)).getY();
			list.stream().filter(pos -> pos.getY() - i <= 2).forEach(pos -> {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					if (random.nextFloat() <= 0.25F) {
						Direction direction2 = direction.getOpposite();
						BlockPos blockPos = pos.add(direction2.getOffsetX(), 0, direction2.getOffsetZ());
						if (generator.isAir(blockPos)) {
							generator.replace(blockPos, Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, Integer.valueOf(random.nextInt(3))).with(CocoaBlock.FACING, direction));
						}
					}
				}
			});
		}
	}
}
