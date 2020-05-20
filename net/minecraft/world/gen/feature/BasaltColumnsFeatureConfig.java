/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BasaltColumnsFeatureConfig
implements FeatureConfig {
    public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("minimum_reach")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.minReach), ((MapCodec)Codec.INT.fieldOf("maximum_reach")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.maxReach), ((MapCodec)Codec.INT.fieldOf("minimum_height")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.minHeight), ((MapCodec)Codec.INT.fieldOf("maximum_height")).forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.maxHeight)).apply((Applicative<BasaltColumnsFeatureConfig, ?>)instance, BasaltColumnsFeatureConfig::new));
    public final int minReach;
    public final int maxReach;
    public final int minHeight;
    public final int maxHeight;

    public BasaltColumnsFeatureConfig(int minReach, int maxReach, int minHeight, int maxHeight) {
        this.minReach = minReach;
        this.maxReach = maxReach;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public static class Builder {
        private int minReach;
        private int maxReach;
        private int minHeight;
        private int maxHeight;

        public Builder reach(int reach) {
            this.minReach = reach;
            this.maxReach = reach;
            return this;
        }

        public Builder reach(int min, int max) {
            this.minReach = min;
            this.maxReach = max;
            return this;
        }

        public Builder height(int min, int max) {
            this.minHeight = min;
            this.maxHeight = max;
            return this;
        }

        public BasaltColumnsFeatureConfig build() {
            if (this.minHeight < 1) {
                throw new IllegalArgumentException("Minimum height cannot be less than 1");
            }
            if (this.minReach < 0) {
                throw new IllegalArgumentException("Minimum reach cannot be negative");
            }
            if (this.minReach > this.maxReach || this.minHeight > this.maxHeight) {
                throw new IllegalArgumentException("Minimum reach/height cannot be greater than maximum width/height");
            }
            return new BasaltColumnsFeatureConfig(this.minReach, this.maxReach, this.minHeight, this.maxHeight);
        }
    }
}

