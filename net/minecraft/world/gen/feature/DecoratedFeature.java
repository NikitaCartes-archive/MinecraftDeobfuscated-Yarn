/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature
extends Feature<DecoratedFeatureConfig> {
    public DecoratedFeature(Codec<DecoratedFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos2, DecoratedFeatureConfig decoratedFeatureConfig) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        decoratedFeatureConfig.decorator.method_30444(new DecoratorContext(serverWorldAccess, chunkGenerator), random, blockPos2).forEach(blockPos -> {
            if (decoratedFeatureConfig.feature.get().generate(serverWorldAccess, chunkGenerator, random, (BlockPos)blockPos)) {
                mutableBoolean.setTrue();
            }
        });
        return mutableBoolean.isTrue();
    }

    public String toString() {
        return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
    }
}

