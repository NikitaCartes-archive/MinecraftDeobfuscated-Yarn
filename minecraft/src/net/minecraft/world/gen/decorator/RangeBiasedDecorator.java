package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class RangeBiasedDecorator extends SimpleDecorator<RangeDecoratorConfig> {
	public RangeBiasedDecorator(Codec<RangeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, RangeDecoratorConfig rangeDecoratorConfig, BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		int k = random.nextInt(random.nextInt(rangeDecoratorConfig.maximum - rangeDecoratorConfig.topOffset) + rangeDecoratorConfig.bottomOffset);
		return Stream.of(new BlockPos(i, k, j));
	}
}
