/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public record EnvironmentScanDecoratorConfig(Direction directionOfSearch, BlockPredicate targetCondition, int maxSteps) implements DecoratorConfig
{
    public static final Codec<EnvironmentScanDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Direction.field_35088.fieldOf("direction_of_search")).forGetter(EnvironmentScanDecoratorConfig::directionOfSearch), ((MapCodec)BlockPredicate.BASE_CODEC.fieldOf("target_condition")).forGetter(EnvironmentScanDecoratorConfig::targetCondition), ((MapCodec)Codec.intRange(1, 32).fieldOf("max_steps")).forGetter(EnvironmentScanDecoratorConfig::maxSteps)).apply((Applicative<EnvironmentScanDecoratorConfig, ?>)instance, EnvironmentScanDecoratorConfig::new));
}

