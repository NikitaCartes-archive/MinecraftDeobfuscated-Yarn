/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;

public class FluidStateImpl
extends AbstractPropertyContainer<Fluid, FluidState>
implements FluidState {
    public FluidStateImpl(Fluid fluid, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        super(fluid, immutableMap);
    }

    @Override
    public Fluid getFluid() {
        return (Fluid)this.owner;
    }
}

