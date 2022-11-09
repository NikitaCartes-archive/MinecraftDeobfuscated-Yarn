package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FluidTags;

public class FluidTagProvider extends ValueLookupTagProvider<Fluid> {
	public FluidTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.FLUID, registryLookupFuture, fluid -> fluid.getRegistryEntry().registryKey());
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
		this.getOrCreateTagBuilder(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
	}
}
