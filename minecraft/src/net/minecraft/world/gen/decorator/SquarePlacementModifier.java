package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class SquarePlacementModifier extends PlacementModifier {
	private static final SquarePlacementModifier INSTANCE = new SquarePlacementModifier();
	public static final Codec<SquarePlacementModifier> MODIFIER_CODEC = Codec.unit((Supplier<SquarePlacementModifier>)(() -> INSTANCE));

	public static SquarePlacementModifier of() {
		return INSTANCE;
	}

	@Override
	public Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
		int i = random.nextInt(16) + pos.getX();
		int j = random.nextInt(16) + pos.getZ();
		return Stream.of(new BlockPos(i, pos.getY(), j));
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.IN_SQUARE;
	}
}
