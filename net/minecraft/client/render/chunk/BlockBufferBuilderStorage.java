/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;

@Environment(value=EnvType.CLIENT)
public class BlockBufferBuilderStorage {
    private final BufferBuilder[] layerBuilders = new BufferBuilder[RenderLayer.values().length];

    public BlockBufferBuilderStorage() {
        this.layerBuilders[RenderLayer.SOLID.ordinal()] = new BufferBuilder(0x200000);
        this.layerBuilders[RenderLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
        this.layerBuilders[RenderLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072);
        this.layerBuilders[RenderLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
    }

    public BufferBuilder get(RenderLayer renderLayer) {
        return this.layerBuilders[renderLayer.ordinal()];
    }

    public BufferBuilder get(int i) {
        return this.layerBuilders[i];
    }
}

