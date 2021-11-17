package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class PlacementModifier {
	public static final Codec<PlacementModifier> CODEC = Registry.PLACEMENT_MODIFIER_TYPE
		.getCodec()
		.dispatch(PlacementModifier::getType, PlacementModifierType::codec);

	public abstract Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos);

	public abstract PlacementModifierType<?> getType();
}
