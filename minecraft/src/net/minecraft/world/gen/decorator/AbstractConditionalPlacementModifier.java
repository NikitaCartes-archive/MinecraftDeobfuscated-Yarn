package net.minecraft.world.gen.decorator;

import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractConditionalPlacementModifier extends PlacementModifier {
	@Override
	public final Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
		return this.shouldPlace(context, random, pos) ? Stream.of(pos) : Stream.of();
	}

	protected abstract boolean shouldPlace(DecoratorContext context, Random random, BlockPos pos);
}
