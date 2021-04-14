/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RangeDecoratorConfig
implements DecoratorConfig {
    public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)HeightProvider.CODEC.fieldOf("height")).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.field_33519)).apply((Applicative<RangeDecoratorConfig, ?>)instance, RangeDecoratorConfig::new));
    public final HeightProvider field_33519;

    public RangeDecoratorConfig(HeightProvider heightProvider) {
        this.field_33519 = heightProvider;
    }
}

