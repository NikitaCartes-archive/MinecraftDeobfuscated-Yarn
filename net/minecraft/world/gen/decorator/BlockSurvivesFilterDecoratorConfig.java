/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public record BlockSurvivesFilterDecoratorConfig(BlockState state) implements DecoratorConfig
{
    public static final Codec<BlockSurvivesFilterDecoratorConfig> CODEC = ((MapCodec)BlockState.CODEC.fieldOf("state")).xmap(BlockSurvivesFilterDecoratorConfig::new, BlockSurvivesFilterDecoratorConfig::state).codec();
}

