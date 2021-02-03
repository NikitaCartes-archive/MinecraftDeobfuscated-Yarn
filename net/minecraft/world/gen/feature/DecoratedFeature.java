/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_5821;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature
extends Feature<DecoratedFeatureConfig> {
    public DecoratedFeature(Codec<DecoratedFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<DecoratedFeatureConfig> arg) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        DecoratedFeatureConfig decoratedFeatureConfig = arg.method_33656();
        ChunkGenerator chunkGenerator = arg.method_33653();
        Random random = arg.method_33654();
        BlockPos blockPos2 = arg.method_33655();
        ConfiguredFeature<?, ?> configuredFeature = decoratedFeatureConfig.feature.get();
        decoratedFeatureConfig.decorator.getPositions(new DecoratorContext(structureWorldAccess, chunkGenerator), random, blockPos2).forEach(blockPos -> {
            if (configuredFeature.generate(structureWorldAccess, chunkGenerator, random, (BlockPos)blockPos)) {
                mutableBoolean.setTrue();
            }
        });
        return mutableBoolean.isTrue();
    }

    public String toString() {
        return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
    }
}

