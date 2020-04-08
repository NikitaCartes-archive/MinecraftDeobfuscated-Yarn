/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BasaltColumnsFeatureConfig
implements FeatureConfig {
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

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("minimum_reach"), ops.createInt(this.minReach), ops.createString("maximum_reach"), ops.createInt(this.maxReach), ops.createString("minimum_height"), ops.createInt(this.minHeight), ops.createString("maximum_height"), ops.createInt(this.maxHeight))));
    }

    public static <T> BasaltColumnsFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("minimum_reach").asInt(0);
        int j = dynamic.get("maximum_reach").asInt(0);
        int k = dynamic.get("minimum_height").asInt(1);
        int l = dynamic.get("maximum_height").asInt(1);
        return new BasaltColumnsFeatureConfig(i, j, k, l);
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

