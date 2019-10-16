/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;

@Environment(value=EnvType.CLIENT)
public class BlockLayeredBufferBuilderStorage {
    private final Map<RenderLayer, BufferBuilder> layerBuilders = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new BufferBuilder(renderLayer.getExpectedBufferSize())));

    public BufferBuilder get(RenderLayer renderLayer) {
        return this.layerBuilders.get(renderLayer);
    }

    public void clear() {
        this.layerBuilders.values().forEach(BufferBuilder::clear);
    }

    public void reset() {
        this.layerBuilders.values().forEach(BufferBuilder::reset);
    }
}

