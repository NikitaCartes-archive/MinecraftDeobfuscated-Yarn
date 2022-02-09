package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;

class MatchingFluidsBlockPredicate extends OffsetPredicate {
	private final RegistryEntryList<Fluid> fluids;
	public static final Codec<MatchingFluidsBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> registerOffsetField(instance)
				.and(RegistryCodecs.entryList(Registry.FLUID_KEY).fieldOf("fluids").forGetter(predicate -> predicate.fluids))
				.apply(instance, MatchingFluidsBlockPredicate::new)
	);

	public MatchingFluidsBlockPredicate(Vec3i offset, RegistryEntryList<Fluid> registryEntryList) {
		super(offset);
		this.fluids = registryEntryList;
	}

	@Override
	protected boolean test(BlockState state) {
		return state.getFluidState().isIn(this.fluids);
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.MATCHING_FLUIDS;
	}
}
