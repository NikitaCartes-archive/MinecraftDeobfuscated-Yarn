/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CaveSurfaceDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CaveSurfaceDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)VerticalSurfaceType.CODEC.fieldOf("surface")).forGetter(caveSurfaceDecoratorConfig -> caveSurfaceDecoratorConfig.surface), ((MapCodec)Codec.INT.fieldOf("floor_to_ceiling_search_range")).forGetter(caveSurfaceDecoratorConfig -> caveSurfaceDecoratorConfig.searchRange)).apply((Applicative<CaveSurfaceDecoratorConfig, ?>)instance, CaveSurfaceDecoratorConfig::new));
    public final VerticalSurfaceType surface;
    public final int searchRange;

    public CaveSurfaceDecoratorConfig(VerticalSurfaceType surface, int searchRange) {
        this.surface = surface;
        this.searchRange = searchRange;
    }
}

