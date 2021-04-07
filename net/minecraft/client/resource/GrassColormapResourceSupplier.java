/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class GrassColormapResourceSupplier
extends SinglePreparationResourceReloader<int[]> {
    private static final Identifier GRASS_COLORMAP_LOC = new Identifier("textures/colormap/grass.png");

    protected int[] tryLoad(ResourceManager resourceManager, Profiler profiler) {
        try {
            return RawTextureDataLoader.loadRawTextureData(resourceManager, GRASS_COLORMAP_LOC);
        } catch (IOException iOException) {
            throw new IllegalStateException("Failed to load grass color texture", iOException);
        }
    }

    @Override
    protected void apply(int[] is, ResourceManager resourceManager, Profiler profiler) {
        GrassColors.setColorMap(is);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.tryLoad(manager, profiler);
    }
}

