/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DeltaFeatureConfig
implements FeatureConfig {
    public static final Codec<DeltaFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("contents")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.contents), ((MapCodec)BlockState.CODEC.fieldOf("rim")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.rim), ((MapCodec)IntProvider.createValidatingCodec(0, 16).fieldOf("size")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.size), ((MapCodec)IntProvider.createValidatingCodec(0, 16).fieldOf("rim_size")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.rimSize)).apply((Applicative<DeltaFeatureConfig, ?>)instance, DeltaFeatureConfig::new));
    private final BlockState contents;
    private final BlockState rim;
    private final IntProvider size;
    private final IntProvider rimSize;

    public DeltaFeatureConfig(BlockState contents, BlockState rim, IntProvider size, IntProvider rimSize) {
        this.contents = contents;
        this.rim = rim;
        this.size = size;
        this.rimSize = rimSize;
    }

    public BlockState getContents() {
        return this.contents;
    }

    public BlockState getRim() {
        return this.rim;
    }

    public IntProvider getSize() {
        return this.size;
    }

    public IntProvider getRimSize() {
        return this.rimSize;
    }
}

