/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountDepthDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountDepthDecoratorConfig> field_24982 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("count")).forGetter(countDepthDecoratorConfig -> countDepthDecoratorConfig.count), ((MapCodec)Codec.INT.fieldOf("baseline")).forGetter(countDepthDecoratorConfig -> countDepthDecoratorConfig.baseline), ((MapCodec)Codec.INT.fieldOf("spread")).forGetter(countDepthDecoratorConfig -> countDepthDecoratorConfig.spread)).apply((Applicative<CountDepthDecoratorConfig, ?>)instance, CountDepthDecoratorConfig::new));
    public final int count;
    public final int baseline;
    public final int spread;

    public CountDepthDecoratorConfig(int count, int baseline, int spread) {
        this.count = count;
        this.baseline = baseline;
        this.spread = spread;
    }
}

