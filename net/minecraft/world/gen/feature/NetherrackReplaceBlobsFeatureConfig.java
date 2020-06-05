/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NetherrackReplaceBlobsFeatureConfig
implements FeatureConfig {
    public static final Codec<NetherrackReplaceBlobsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("target")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.target), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.state), ((MapCodec)Vec3i.field_25123.fieldOf("minimum_reach")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.minReachPos), ((MapCodec)Vec3i.field_25123.fieldOf("maximum_reach")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.maxReachPos)).apply((Applicative<NetherrackReplaceBlobsFeatureConfig, ?>)instance, NetherrackReplaceBlobsFeatureConfig::new));
    public final BlockState target;
    public final BlockState state;
    public final Vec3i minReachPos;
    public final Vec3i maxReachPos;

    public NetherrackReplaceBlobsFeatureConfig(BlockState target, BlockState state, Vec3i minReachPos, Vec3i maxReachPos) {
        this.target = target;
        this.state = state;
        this.minReachPos = minReachPos;
        this.maxReachPos = maxReachPos;
    }

    public static class Builder {
        private BlockState target = Blocks.AIR.getDefaultState();
        private BlockState state = Blocks.AIR.getDefaultState();
        private Vec3i minReachPos = Vec3i.ZERO;
        private Vec3i maxReachPos = Vec3i.ZERO;

        public Builder target(BlockState target) {
            this.target = target;
            return this;
        }

        public Builder state(BlockState state) {
            this.state = state;
            return this;
        }

        public Builder minReachPos(Vec3i minReachPos) {
            this.minReachPos = minReachPos;
            return this;
        }

        public Builder maxReachPos(Vec3i maxReachPos) {
            this.maxReachPos = maxReachPos;
            return this;
        }

        public NetherrackReplaceBlobsFeatureConfig build() {
            if (this.minReachPos.getX() < 0 || this.minReachPos.getY() < 0 || this.minReachPos.getZ() < 0) {
                throw new IllegalArgumentException("Minimum reach cannot be less than zero");
            }
            if (this.minReachPos.getX() > this.maxReachPos.getX() || this.minReachPos.getY() > this.maxReachPos.getY() || this.minReachPos.getZ() > this.maxReachPos.getZ()) {
                throw new IllegalArgumentException("Maximum reach must be greater than minimum reach for each axis");
            }
            return new NetherrackReplaceBlobsFeatureConfig(this.target, this.state, this.minReachPos, this.maxReachPos);
        }
    }
}

