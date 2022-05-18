package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public class HeightmapPlacementModifier extends PlacementModifier {
	public static final Codec<HeightmapPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Heightmap.Type.CODEC.fieldOf("heightmap").forGetter(heightmapPlacementModifier -> heightmapPlacementModifier.heightmap))
				.apply(instance, HeightmapPlacementModifier::new)
	);
	private final Heightmap.Type heightmap;

	private HeightmapPlacementModifier(Heightmap.Type heightmap) {
		this.heightmap = heightmap;
	}

	public static HeightmapPlacementModifier of(Heightmap.Type heightmap) {
		return new HeightmapPlacementModifier(heightmap);
	}

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getZ();
		int k = context.getTopY(this.heightmap, i, j);
		return k > context.getBottomY() ? Stream.of(new BlockPos(i, k, j)) : Stream.of();
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.HEIGHTMAP;
	}
}
