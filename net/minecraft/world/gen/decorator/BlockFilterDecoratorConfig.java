/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public record BlockFilterDecoratorConfig(List<Block> allowed, List<Block> disallowed, BlockPos offset) implements DecoratorConfig
{
    public static final Codec<BlockFilterDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(Registry.BLOCK.listOf().optionalFieldOf("allowed", List.of()).forGetter(BlockFilterDecoratorConfig::allowed), Registry.BLOCK.listOf().optionalFieldOf("disallowed", List.of()).forGetter(BlockFilterDecoratorConfig::disallowed), ((MapCodec)BlockPos.CODEC.fieldOf("offset")).forGetter(BlockFilterDecoratorConfig::offset)).apply((Applicative<BlockFilterDecoratorConfig, ?>)instance, BlockFilterDecoratorConfig::new));
}

