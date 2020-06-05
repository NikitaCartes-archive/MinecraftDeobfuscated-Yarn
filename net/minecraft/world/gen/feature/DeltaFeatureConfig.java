/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DeltaFeatureConfig
implements FeatureConfig {
    public static final Codec<DeltaFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("contents")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.contents), ((MapCodec)BlockState.CODEC.fieldOf("rim")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.rim), ((MapCodec)Codec.INT.fieldOf("minimum_radius")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.minRadius), ((MapCodec)Codec.INT.fieldOf("maximum_radius")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.maxRadius), ((MapCodec)Codec.INT.fieldOf("maximum_rim")).forGetter(deltaFeatureConfig -> deltaFeatureConfig.maxRim)).apply((Applicative<DeltaFeatureConfig, ?>)instance, DeltaFeatureConfig::new));
    public final BlockState contents;
    public final BlockState rim;
    public final int minRadius;
    public final int maxRadius;
    public final int maxRim;

    public DeltaFeatureConfig(BlockState contents, BlockState rim, int minRadius, int maxRadius, int maxRim) {
        this.contents = contents;
        this.rim = rim;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.maxRim = maxRim;
    }

    public static class Builder {
        Optional<BlockState> contents = Optional.empty();
        Optional<BlockState> rim = Optional.empty();
        int minRadius;
        int maxRadius;
        int maxRim;

        public Builder radius(int min, int max) {
            this.minRadius = min;
            this.maxRadius = max;
            return this;
        }

        public Builder contents(BlockState contents) {
            this.contents = Optional.of(contents);
            return this;
        }

        public Builder rim(BlockState rim, int maxRim) {
            this.rim = Optional.of(rim);
            this.maxRim = maxRim;
            return this;
        }

        public DeltaFeatureConfig build() {
            if (!this.contents.isPresent()) {
                throw new IllegalArgumentException("Missing contents");
            }
            if (!this.rim.isPresent()) {
                throw new IllegalArgumentException("Missing rim");
            }
            if (this.minRadius > this.maxRadius) {
                throw new IllegalArgumentException("Minimum radius cannot be greater than maximum radius");
            }
            return new DeltaFeatureConfig(this.contents.get(), this.rim.get(), this.minRadius, this.maxRadius, this.maxRim);
        }
    }
}

