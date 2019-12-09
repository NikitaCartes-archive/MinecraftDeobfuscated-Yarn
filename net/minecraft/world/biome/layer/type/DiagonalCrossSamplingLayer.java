/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public interface DiagonalCrossSamplingLayer
extends ParentedLayer,
NorthWestCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2, int var3, int var4, int var5, int var6);

    @Override
    default public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        return this.sample(context, parent.sample(this.transformX(x + 0), this.transformZ(z + 2)), parent.sample(this.transformX(x + 2), this.transformZ(z + 2)), parent.sample(this.transformX(x + 2), this.transformZ(z + 0)), parent.sample(this.transformX(x + 0), this.transformZ(z + 0)), parent.sample(this.transformX(x + 1), this.transformZ(z + 1)));
    }
}

