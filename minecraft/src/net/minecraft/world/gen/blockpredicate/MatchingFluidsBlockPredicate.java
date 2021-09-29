package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;

class MatchingFluidsBlockPredicate implements BlockPredicate {
	private final List<Fluid> fluids;
	private final BlockPos pos;
	public static final Codec<MatchingFluidsBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.FLUID.listOf().fieldOf("fluids").forGetter(matchingFluidsBlockPredicate -> matchingFluidsBlockPredicate.fluids),
					BlockPos.CODEC.fieldOf("offset").forGetter(matchingFluidsBlockPredicate -> matchingFluidsBlockPredicate.pos)
				)
				.apply(instance, MatchingFluidsBlockPredicate::new)
	);

	public MatchingFluidsBlockPredicate(List<Fluid> fluids, BlockPos pos) {
		this.fluids = fluids;
		this.pos = pos;
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return this.fluids.contains(structureWorldAccess.getFluidState(blockPos.add(this.pos)).getFluid());
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.MATCHING_FLUIDS;
	}
}
