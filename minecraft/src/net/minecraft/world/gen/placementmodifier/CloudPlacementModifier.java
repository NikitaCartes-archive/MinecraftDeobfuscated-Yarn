package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public class CloudPlacementModifier extends PlacementModifier {
	public static final Codec<CloudPlacementModifier> CODEC = Codec.unit(CloudPlacementModifier::new);

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		int i = context.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());
		return i != context.getBottomY() ? Stream.empty() : Stream.of(pos.up(random.nextBetween(105, 115)));
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.CLOUD;
	}
}
