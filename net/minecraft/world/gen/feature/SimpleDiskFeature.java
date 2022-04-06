/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleDiskFeature
extends DiskFeature {
    public SimpleDiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DiskFeatureConfig> context) {
        if (!context.getWorld().getBlockState(context.getOrigin()).isIn(context.getConfig().canOriginReplace())) {
            return false;
        }
        return super.generate(context);
    }
}

