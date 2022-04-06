package net.minecraft.world.gen.placementmodifier;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public abstract class AbstractCountPlacementModifier extends PlacementModifier {
	protected abstract int getCount(AbstractRandom abstractRandom, BlockPos pos);

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, AbstractRandom abstractRandom, BlockPos pos) {
		return IntStream.range(0, this.getCount(abstractRandom, pos)).mapToObj(i -> pos);
	}
}
