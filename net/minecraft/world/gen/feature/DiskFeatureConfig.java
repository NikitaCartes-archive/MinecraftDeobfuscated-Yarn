/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.class_5428;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DiskFeatureConfig
implements FeatureConfig {
    public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(diskFeatureConfig -> diskFeatureConfig.state), ((MapCodec)class_5428.method_30316(0, 4, 4).fieldOf("radius")).forGetter(diskFeatureConfig -> diskFeatureConfig.radius), ((MapCodec)Codec.intRange(0, 4).fieldOf("half_height")).forGetter(diskFeatureConfig -> diskFeatureConfig.ySize), ((MapCodec)BlockState.CODEC.listOf().fieldOf("targets")).forGetter(diskFeatureConfig -> diskFeatureConfig.targets)).apply((Applicative<DiskFeatureConfig, ?>)instance, DiskFeatureConfig::new));
    public final BlockState state;
    public final class_5428 radius;
    public final int ySize;
    public final List<BlockState> targets;

    public DiskFeatureConfig(BlockState state, class_5428 arg, int ySize, List<BlockState> targets) {
        this.state = state;
        this.radius = arg;
        this.ySize = ySize;
        this.targets = targets;
    }
}

