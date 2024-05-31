package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public class FixedPlacementModifier extends PlacementModifier {
	public static final MapCodec<FixedPlacementModifier> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(BlockPos.CODEC.listOf().fieldOf("positions").forGetter(placementModifier -> placementModifier.positions))
				.apply(instance, FixedPlacementModifier::new)
	);
	private final List<BlockPos> positions;

	public static FixedPlacementModifier of(BlockPos... positions) {
		return new FixedPlacementModifier(List.of(positions));
	}

	private FixedPlacementModifier(List<BlockPos> positions) {
		this.positions = positions;
	}

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX());
		int j = ChunkSectionPos.getSectionCoord(pos.getZ());
		boolean bl = false;

		for (BlockPos blockPos : this.positions) {
			if (chunkSectionMatchesPos(i, j, blockPos)) {
				bl = true;
				break;
			}
		}

		return !bl ? Stream.empty() : this.positions.stream().filter(posx -> chunkSectionMatchesPos(i, j, posx));
	}

	private static boolean chunkSectionMatchesPos(int chunkSectionX, int chunkSectionZ, BlockPos pos) {
		return chunkSectionX == ChunkSectionPos.getSectionCoord(pos.getX()) && chunkSectionZ == ChunkSectionPos.getSectionCoord(pos.getZ());
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.FIXED_PLACEMENT;
	}
}
