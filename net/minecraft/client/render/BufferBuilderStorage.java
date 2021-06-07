/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class BufferBuilderStorage {
    private final BlockBufferBuilderStorage blockBuilders = new BlockBufferBuilderStorage();
    private final SortedMap<RenderLayer, BufferBuilder> entityBuilders = Util.make(new Object2ObjectLinkedOpenHashMap(), map -> {
        map.put(TexturedRenderLayers.getEntitySolid(), this.blockBuilders.get(RenderLayer.getSolid()));
        map.put(TexturedRenderLayers.getEntityCutout(), this.blockBuilders.get(RenderLayer.getCutout()));
        map.put(TexturedRenderLayers.getBannerPatterns(), this.blockBuilders.get(RenderLayer.getCutoutMipped()));
        map.put(TexturedRenderLayers.getEntityTranslucentCull(), this.blockBuilders.get(RenderLayer.getTranslucent()));
        BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getShieldPatterns());
        BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getBeds());
        BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getShulkerBoxes());
        BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getSign());
        BufferBuilderStorage.assignBufferBuilder(map, TexturedRenderLayers.getChest());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getTranslucentNoCrumbling());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getArmorGlint());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getArmorEntityGlint());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getGlint());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getDirectGlint());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getGlintTranslucent());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getEntityGlint());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getDirectEntityGlint());
        BufferBuilderStorage.assignBufferBuilder(map, RenderLayer.getWaterMask());
        ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(layer -> BufferBuilderStorage.assignBufferBuilder(map, layer));
    });
    private final VertexConsumerProvider.Immediate entityVertexConsumers = VertexConsumerProvider.immediate(this.entityBuilders, new BufferBuilder(256));
    private final VertexConsumerProvider.Immediate effectVertexConsumers = VertexConsumerProvider.immediate(new BufferBuilder(256));
    private final OutlineVertexConsumerProvider outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);

    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> builderStorage, RenderLayer layer) {
        builderStorage.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
    }

    public BlockBufferBuilderStorage getBlockBufferBuilders() {
        return this.blockBuilders;
    }

    public VertexConsumerProvider.Immediate getEntityVertexConsumers() {
        return this.entityVertexConsumers;
    }

    public VertexConsumerProvider.Immediate getEffectVertexConsumers() {
        return this.effectVertexConsumers;
    }

    public OutlineVertexConsumerProvider getOutlineVertexConsumers() {
        return this.outlineVertexConsumers;
    }
}

