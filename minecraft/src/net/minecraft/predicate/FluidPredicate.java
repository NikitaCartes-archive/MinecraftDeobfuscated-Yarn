package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

public record FluidPredicate(Optional<TagKey<Fluid>> tag, Optional<RegistryEntry<Fluid>> fluid, Optional<StatePredicate> state) {
	public static final Codec<FluidPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(TagKey.unprefixedCodec(RegistryKeys.FLUID), "tag").forGetter(FluidPredicate::tag),
					Codecs.createStrictOptionalFieldCodec(Registries.FLUID.getEntryCodec(), "fluid").forGetter(FluidPredicate::fluid),
					Codecs.createStrictOptionalFieldCodec(StatePredicate.CODEC, "state").forGetter(FluidPredicate::state)
				)
				.apply(instance, FluidPredicate::new)
	);

	public boolean test(ServerWorld world, BlockPos pos) {
		if (!world.canSetBlock(pos)) {
			return false;
		} else {
			FluidState fluidState = world.getFluidState(pos);
			if (this.tag.isPresent() && !fluidState.isIn((TagKey<Fluid>)this.tag.get())) {
				return false;
			} else {
				return this.fluid.isPresent() && !fluidState.isOf((Fluid)((RegistryEntry)this.fluid.get()).value())
					? false
					: !this.state.isPresent() || ((StatePredicate)this.state.get()).test(fluidState);
			}
		}
	}

	public static class Builder {
		private Optional<RegistryEntry<Fluid>> fluid = Optional.empty();
		private Optional<TagKey<Fluid>> tag = Optional.empty();
		private Optional<StatePredicate> state = Optional.empty();

		private Builder() {
		}

		public static FluidPredicate.Builder create() {
			return new FluidPredicate.Builder();
		}

		public FluidPredicate.Builder fluid(Fluid fluid) {
			this.fluid = Optional.of(fluid.getRegistryEntry());
			return this;
		}

		public FluidPredicate.Builder tag(TagKey<Fluid> tag) {
			this.tag = Optional.of(tag);
			return this;
		}

		public FluidPredicate.Builder state(StatePredicate state) {
			this.state = Optional.of(state);
			return this;
		}

		public FluidPredicate build() {
			return new FluidPredicate(this.tag, this.fluid, this.state);
		}
	}
}
