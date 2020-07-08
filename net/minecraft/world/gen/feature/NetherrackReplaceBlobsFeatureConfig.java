/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.class_5428;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NetherrackReplaceBlobsFeatureConfig
implements FeatureConfig {
    public static final Codec<NetherrackReplaceBlobsFeatureConfig> field_25848 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("target")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.field_25849), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.field_25850), ((MapCodec)class_5428.field_25809.fieldOf("radius")).forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.field_25851)).apply((Applicative<NetherrackReplaceBlobsFeatureConfig, ?>)instance, NetherrackReplaceBlobsFeatureConfig::new));
    public final BlockState field_25849;
    public final BlockState field_25850;
    private final class_5428 field_25851;

    public NetherrackReplaceBlobsFeatureConfig(BlockState blockState, BlockState blockState2, class_5428 arg) {
        this.field_25849 = blockState;
        this.field_25850 = blockState2;
        this.field_25851 = arg;
    }

    public class_5428 method_30405() {
        return this.field_25851;
    }
}

