/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_6122;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.CarverDebugConfig;

public class CarverConfig
extends ProbabilityConfig {
    public static final MapCodec<CarverConfig> CONFIG_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).forGetter(carverConfig -> Float.valueOf(carverConfig.probability)), ((MapCodec)class_6122.field_31540.fieldOf("y")).forGetter(carverConfig -> carverConfig.field_31488), ((MapCodec)FloatProvider.VALUE_CODEC.fieldOf("yScale")).forGetter(carverConfig -> carverConfig.field_31489), ((MapCodec)YOffset.OFFSET_CODEC.fieldOf("lava_level")).forGetter(carverConfig -> carverConfig.field_31490), CarverDebugConfig.CODEC.optionalFieldOf("debug_settings", CarverDebugConfig.DEFAULT).forGetter(carverConfig -> carverConfig.debugConfig)).apply((Applicative<CarverConfig, ?>)instance, CarverConfig::new));
    public final class_6122 field_31488;
    public final FloatProvider field_31489;
    public final YOffset field_31490;
    public final CarverDebugConfig debugConfig;

    public CarverConfig(float f, class_6122 arg, FloatProvider floatProvider, YOffset yOffset, CarverDebugConfig carverDebugConfig) {
        super(f);
        this.field_31488 = arg;
        this.field_31489 = floatProvider;
        this.field_31490 = yOffset;
        this.debugConfig = carverDebugConfig;
    }
}

