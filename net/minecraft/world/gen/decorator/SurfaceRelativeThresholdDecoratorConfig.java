/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class SurfaceRelativeThresholdDecoratorConfig
implements DecoratorConfig {
    public static final Codec<SurfaceRelativeThresholdDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Heightmap.Type.CODEC.fieldOf("heightmap")).forGetter(surfaceRelativeThresholdDecoratorConfig -> surfaceRelativeThresholdDecoratorConfig.field_34722), Codec.INT.optionalFieldOf("min_inclusive", Integer.MIN_VALUE).forGetter(surfaceRelativeThresholdDecoratorConfig -> surfaceRelativeThresholdDecoratorConfig.field_34723), Codec.INT.optionalFieldOf("max_inclusive", Integer.MAX_VALUE).forGetter(surfaceRelativeThresholdDecoratorConfig -> surfaceRelativeThresholdDecoratorConfig.field_34724)).apply((Applicative<SurfaceRelativeThresholdDecoratorConfig, ?>)instance, SurfaceRelativeThresholdDecoratorConfig::new));
    public final Heightmap.Type field_34722;
    public final int field_34723;
    public final int field_34724;

    public SurfaceRelativeThresholdDecoratorConfig(Heightmap.Type type, int i, int j) {
        this.field_34722 = type;
        this.field_34723 = i;
        this.field_34724 = j;
    }
}

