/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class FoliageColormapResourceSupplier
extends SinglePreparationResourceReloadListener<int[]> {
    private static final Identifier FOLIAGE_COLORMAP_LOC = new Identifier("textures/colormap/foliage.png");

    protected int[] method_18660(ResourceManager resourceManager, Profiler profiler) {
        try {
            return RawTextureDataLoader.loadRawTextureData(resourceManager, FOLIAGE_COLORMAP_LOC);
        } catch (IOException iOException) {
            throw new IllegalStateException("Failed to load foliage color texture", iOException);
        }
    }

    protected void method_18659(int[] is, ResourceManager resourceManager, Profiler profiler) {
        FoliageColors.setColorMap(is);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, Profiler profiler) {
        return this.method_18660(resourceManager, profiler);
    }
}

