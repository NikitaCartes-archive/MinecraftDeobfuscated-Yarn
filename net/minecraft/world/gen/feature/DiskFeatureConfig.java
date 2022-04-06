/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.FeatureConfig;

public record DiskFeatureConfig(BlockState state, IntProvider radius, int halfHeight, List<BlockState> targets, RegistryEntryList<Block> canOriginReplace) implements FeatureConfig
{
    public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(DiskFeatureConfig::state), ((MapCodec)IntProvider.createValidatingCodec(0, 8).fieldOf("radius")).forGetter(DiskFeatureConfig::radius), ((MapCodec)Codec.intRange(0, 4).fieldOf("half_height")).forGetter(DiskFeatureConfig::halfHeight), ((MapCodec)BlockState.CODEC.listOf().fieldOf("targets")).forGetter(DiskFeatureConfig::targets), ((MapCodec)RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_origin_replace")).forGetter(config -> config.canOriginReplace)).apply((Applicative<DiskFeatureConfig, ?>)instance, DiskFeatureConfig::new));
}

