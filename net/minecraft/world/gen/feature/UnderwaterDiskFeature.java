/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_5821;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;

public class UnderwaterDiskFeature
extends DiskFeature {
    public UnderwaterDiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<DiskFeatureConfig> arg) {
        if (!arg.method_33652().getFluidState(arg.method_33655()).isIn(FluidTags.WATER)) {
            return false;
        }
        return super.generate(arg);
    }
}

