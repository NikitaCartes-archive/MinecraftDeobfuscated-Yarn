/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountExtraChanceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountExtraChanceDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("count")).forGetter(countExtraChanceDecoratorConfig -> countExtraChanceDecoratorConfig.count), ((MapCodec)Codec.FLOAT.fieldOf("extra_chance")).forGetter(countExtraChanceDecoratorConfig -> Float.valueOf(countExtraChanceDecoratorConfig.extraChance)), ((MapCodec)Codec.INT.fieldOf("extra_count")).forGetter(countExtraChanceDecoratorConfig -> countExtraChanceDecoratorConfig.extraCount)).apply((Applicative<CountExtraChanceDecoratorConfig, ?>)instance, CountExtraChanceDecoratorConfig::new));
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public CountExtraChanceDecoratorConfig(int count, float extraChance, int extraCount) {
        this.count = count;
        this.extraChance = extraChance;
        this.extraCount = extraCount;
    }
}

