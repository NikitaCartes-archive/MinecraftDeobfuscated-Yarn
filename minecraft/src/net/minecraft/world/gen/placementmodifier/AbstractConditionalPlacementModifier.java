package net.minecraft.world.gen.placementmodifier;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public abstract class AbstractConditionalPlacementModifier extends PlacementModifier {
	@Override
	public final Stream<BlockPos> getPositions(FeaturePlacementContext context, AbstractRandom abstractRandom, BlockPos pos) {
		return this.shouldPlace(context, abstractRandom, pos) ? Stream.of(pos) : Stream.of();
	}

	protected abstract boolean shouldPlace(FeaturePlacementContext context, AbstractRandom abstractRandom, BlockPos pos);
}
