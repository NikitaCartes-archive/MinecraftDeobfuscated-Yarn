/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountChanceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountChanceDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("count")).forGetter(countChanceDecoratorConfig -> countChanceDecoratorConfig.count), ((MapCodec)Codec.FLOAT.fieldOf("chance")).forGetter(countChanceDecoratorConfig -> Float.valueOf(countChanceDecoratorConfig.chance))).apply((Applicative<CountChanceDecoratorConfig, ?>)instance, CountChanceDecoratorConfig::new));
    public final int count;
    public final float chance;

    public CountChanceDecoratorConfig(int count, float chance) {
        this.count = count;
        this.chance = chance;
    }
}

