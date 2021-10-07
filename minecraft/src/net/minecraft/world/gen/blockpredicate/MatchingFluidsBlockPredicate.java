package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

class MatchingFluidsBlockPredicate extends OffsetPredicate {
	private final List<Fluid> fluids;
	public static final Codec<MatchingFluidsBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> method_39013(instance)
				.and(Registry.FLUID.listOf().fieldOf("fluids").forGetter(matchingFluidsBlockPredicate -> matchingFluidsBlockPredicate.fluids))
				.apply(instance, MatchingFluidsBlockPredicate::new)
	);

	public MatchingFluidsBlockPredicate(BlockPos offset, List<Fluid> fluids) {
		super(offset);
		this.fluids = fluids;
	}

	@Override
	protected boolean test(BlockState state) {
		return this.fluids.contains(state.getFluidState().getFluid());
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.MATCHING_FLUIDS;
	}
}
