package net.minecraft.world.gen.placementmodifier;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public abstract class AbstractConditionalPlacementModifier extends PlacementModifier {
	@Override
	public final Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		return this.shouldPlace(context, random, pos) ? Stream.of(pos) : Stream.of();
	}

	protected abstract boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos);
}
