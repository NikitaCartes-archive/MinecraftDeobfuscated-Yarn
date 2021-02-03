/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_5821;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EmeraldOreFeature
extends Feature<EmeraldOreFeatureConfig> {
    public EmeraldOreFeature(Codec<EmeraldOreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<EmeraldOreFeatureConfig> arg) {
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        BlockPos blockPos = arg.method_33655();
        EmeraldOreFeatureConfig emeraldOreFeatureConfig = arg.method_33656();
        if (structureWorldAccess.getBlockState(blockPos).isOf(emeraldOreFeatureConfig.target.getBlock())) {
            structureWorldAccess.setBlockState(blockPos, emeraldOreFeatureConfig.state, 2);
        }
        return true;
    }
}

