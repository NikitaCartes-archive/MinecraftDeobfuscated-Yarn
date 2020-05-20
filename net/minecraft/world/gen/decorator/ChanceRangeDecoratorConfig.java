/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class ChanceRangeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<ChanceRangeDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("chance")).forGetter(chanceRangeDecoratorConfig -> Float.valueOf(chanceRangeDecoratorConfig.chance)), ((MapCodec)Codec.INT.fieldOf("bottom_offset")).withDefault(0).forGetter(chanceRangeDecoratorConfig -> chanceRangeDecoratorConfig.bottomOffset), ((MapCodec)Codec.INT.fieldOf("top_offset")).withDefault(0).forGetter(chanceRangeDecoratorConfig -> chanceRangeDecoratorConfig.topOffset), ((MapCodec)Codec.INT.fieldOf("top")).withDefault(0).forGetter(chanceRangeDecoratorConfig -> chanceRangeDecoratorConfig.top)).apply((Applicative<ChanceRangeDecoratorConfig, ?>)instance, ChanceRangeDecoratorConfig::new));
    public final float chance;
    public final int bottomOffset;
    public final int topOffset;
    public final int top;

    public ChanceRangeDecoratorConfig(float chance, int bottomOffset, int topOffset, int top) {
        this.chance = chance;
        this.bottomOffset = bottomOffset;
        this.topOffset = topOffset;
        this.top = top;
    }
}

