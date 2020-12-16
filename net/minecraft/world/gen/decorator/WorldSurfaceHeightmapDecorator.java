/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.HeightmapDecorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class WorldSurfaceHeightmapDecorator
extends HeightmapDecorator<NopeDecoratorConfig> {
    public WorldSurfaceHeightmapDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type getHeightmapType(NopeDecoratorConfig nopeDecoratorConfig) {
        return Heightmap.Type.WORLD_SURFACE_WG;
    }
}

