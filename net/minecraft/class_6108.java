/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_6122;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;

public class class_6108
extends CarverConfig {
    public static final Codec<class_6108> field_31491 = RecordCodecBuilder.create(instance -> instance.group(CarverConfig.CONFIG_CODEC.forGetter(arg -> arg), ((MapCodec)FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_multiplier")).forGetter(arg -> arg.field_31492), ((MapCodec)FloatProvider.VALUE_CODEC.fieldOf("vertical_radius_multiplier")).forGetter(arg -> arg.field_31493), ((MapCodec)FloatProvider.createValidatedCodec(-1.0f, 1.0f).fieldOf("floor_level")).forGetter(arg -> arg.field_31494)).apply((Applicative<class_6108, ?>)instance, class_6108::new));
    public final FloatProvider field_31492;
    public final FloatProvider field_31493;
    final FloatProvider field_31494;

    public class_6108(float f, class_6122 arg, FloatProvider floatProvider, YOffset yOffset, CarverDebugConfig carverDebugConfig, FloatProvider floatProvider2, FloatProvider floatProvider3, FloatProvider floatProvider4) {
        super(f, arg, floatProvider, yOffset, carverDebugConfig);
        this.field_31492 = floatProvider2;
        this.field_31493 = floatProvider3;
        this.field_31494 = floatProvider4;
    }

    public class_6108(float f, class_6122 arg, FloatProvider floatProvider, YOffset yOffset, FloatProvider floatProvider2, FloatProvider floatProvider3, FloatProvider floatProvider4) {
        this(f, arg, floatProvider, yOffset, CarverDebugConfig.DEFAULT, floatProvider2, floatProvider3, floatProvider4);
    }

    public class_6108(CarverConfig carverConfig, FloatProvider floatProvider, FloatProvider floatProvider2, FloatProvider floatProvider3) {
        this(carverConfig.probability, carverConfig.field_31488, carverConfig.field_31489, carverConfig.field_31490, carverConfig.debugConfig, floatProvider, floatProvider2, floatProvider3);
    }
}

