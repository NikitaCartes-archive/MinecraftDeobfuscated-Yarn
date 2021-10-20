/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class ScatterDecoratorConfig
implements DecoratorConfig {
    public static final Codec<ScatterDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)IntProvider.createValidatingCodec(-16, 16).fieldOf("xz_spread")).forGetter(scatterDecoratorConfig -> scatterDecoratorConfig.xzSpread), ((MapCodec)IntProvider.createValidatingCodec(-16, 16).fieldOf("y_spread")).forGetter(scatterDecoratorConfig -> scatterDecoratorConfig.ySpread)).apply((Applicative<ScatterDecoratorConfig, ?>)instance, ScatterDecoratorConfig::new));
    public final IntProvider xzSpread;
    public final IntProvider ySpread;

    public ScatterDecoratorConfig(IntProvider xzSpread, IntProvider ySpread) {
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
    }
}

