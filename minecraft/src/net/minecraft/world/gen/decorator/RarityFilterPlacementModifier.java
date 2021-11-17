package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

public class RarityFilterPlacementModifier extends AbstractConditionalPlacementModifier {
	public static final Codec<RarityFilterPlacementModifier> MODIFIER_CODEC = Codecs.POSITIVE_INT
		.fieldOf("chance")
		.<RarityFilterPlacementModifier>xmap(RarityFilterPlacementModifier::new, rarityFilterPlacementModifier -> rarityFilterPlacementModifier.chance)
		.codec();
	private final int chance;

	private RarityFilterPlacementModifier(int chance) {
		this.chance = chance;
	}

	public static RarityFilterPlacementModifier of(int chance) {
		return new RarityFilterPlacementModifier(chance);
	}

	@Override
	protected boolean shouldPlace(DecoratorContext context, Random random, BlockPos pos) {
		return random.nextFloat() < 1.0F / (float)this.chance;
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.RARITY_FILTER;
	}
}