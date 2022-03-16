/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.registry.Registry;

public class FluidTagProvider
extends AbstractTagProvider<Fluid> {
    public FluidTagProvider(DataGenerator root) {
        super(root, Registry.FLUID);
    }

    @Override
    protected void configure() {
        this.getOrCreateTagBuilder(FluidTags.WATER).add((Fluid[])new Fluid[]{Fluids.WATER, Fluids.FLOWING_WATER});
        this.getOrCreateTagBuilder(FluidTags.LAVA).add((Fluid[])new Fluid[]{Fluids.LAVA, Fluids.FLOWING_LAVA});
    }
}

