/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryWrapper;

public class FluidTagProvider
extends ValueLookupTagProvider<Fluid> {
    public FluidTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, Registry.FLUID_KEY, registryLookupFuture, fluid -> fluid.getRegistryEntry().registryKey());
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)FluidTags.WATER)).add(Fluids.WATER, Fluids.FLOWING_WATER);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)FluidTags.LAVA)).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
    }
}

