package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class HeightRangePlacementModifier extends PlacementModifier {
	public static final Codec<HeightRangePlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(HeightProvider.CODEC.fieldOf("height").forGetter(heightRangePlacementModifier -> heightRangePlacementModifier.height))
				.apply(instance, HeightRangePlacementModifier::new)
	);
	private final HeightProvider height;

	private HeightRangePlacementModifier(HeightProvider height) {
		this.height = height;
	}

	public static HeightRangePlacementModifier of(HeightProvider height) {
		return new HeightRangePlacementModifier(height);
	}

	public static HeightRangePlacementModifier uniform(YOffset minOffset, YOffset maxOffset) {
		return of(UniformHeightProvider.create(minOffset, maxOffset));
	}

	public static HeightRangePlacementModifier trapezoid(YOffset minOffset, YOffset maxOffset) {
		return of(TrapezoidHeightProvider.create(minOffset, maxOffset));
	}

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		return Stream.of(pos.withY(this.height.get(random, context)));
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.HEIGHT_RANGE;
	}
}
