/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class RangeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("bottom_offset")).orElse(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.bottomOffset), ((MapCodec)Codec.INT.fieldOf("top_offset")).orElse(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.topOffset), ((MapCodec)Codec.INT.fieldOf("maximum")).orElse(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.maximum)).apply((Applicative<RangeDecoratorConfig, ?>)instance, RangeDecoratorConfig::new));
    public final int bottomOffset;
    public final int topOffset;
    public final int maximum;

    public RangeDecoratorConfig(int bottomOffset, int topOffset, int maximum) {
        this.bottomOffset = bottomOffset;
        this.topOffset = topOffset;
        this.maximum = maximum;
    }
}

