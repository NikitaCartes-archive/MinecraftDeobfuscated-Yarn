/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class UnderwaterDiskFeature
extends DiskFeature {
    public UnderwaterDiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DiskFeatureConfig> context) {
        if (!context.getWorld().getFluidState(context.getOrigin()).isIn(FluidTags.WATER)) {
            return false;
        }
        return super.generate(context);
    }
}

