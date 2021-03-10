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

public class HeightmapDecoratorConfig
implements DecoratorConfig {
    public static final Codec<HeightmapDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Heightmap.Type.CODEC.fieldOf("heightmap")).forGetter(heightmapDecoratorConfig -> heightmapDecoratorConfig.heightmap)).apply((Applicative<HeightmapDecoratorConfig, ?>)instance, HeightmapDecoratorConfig::new));
    public final Heightmap.Type heightmap;

    public HeightmapDecoratorConfig(Heightmap.Type heightmap) {
        this.heightmap = heightmap;
    }
}

