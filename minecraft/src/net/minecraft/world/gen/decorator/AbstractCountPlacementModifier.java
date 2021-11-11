package net.minecraft.world.gen.decorator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractCountPlacementModifier extends PlacementModifier {
	protected abstract int getCount(Random random, BlockPos pos);

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
		return IntStream.range(0, this.getCount(random, pos)).mapToObj(i -> pos);
	}
}
