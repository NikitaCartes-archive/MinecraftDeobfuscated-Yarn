/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4722;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class BufferBuilderStorage {
    private final BlockBufferBuilderStorage blockBuilders = new BlockBufferBuilderStorage();
    private final SortedMap<RenderLayer, BufferBuilder> entityBuilders = Util.create(new Object2ObjectLinkedOpenHashMap(), object2ObjectLinkedOpenHashMap -> {
        object2ObjectLinkedOpenHashMap.put(class_4722.method_24073(), this.blockBuilders.get(RenderLayer.getSolid()));
        object2ObjectLinkedOpenHashMap.put(class_4722.method_24074(), this.blockBuilders.get(RenderLayer.getCutout()));
        object2ObjectLinkedOpenHashMap.put(class_4722.method_24059(), this.blockBuilders.get(RenderLayer.getCutoutMipped()));
        object2ObjectLinkedOpenHashMap.put(class_4722.method_24075(), this.blockBuilders.get(RenderLayer.getTranslucent()));
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, class_4722.method_24067());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, class_4722.method_24069());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, class_4722.method_24070());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, class_4722.method_24071());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, class_4722.method_24072());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, RenderLayer.getTranslucentNoCrumbling());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, RenderLayer.getGlint());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, RenderLayer.getEntityGlint());
        BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, RenderLayer.getWaterMask());
        ModelLoader.field_21772.forEach(renderLayer -> BufferBuilderStorage.method_23798(object2ObjectLinkedOpenHashMap, renderLayer));
    });
    private final VertexConsumerProvider.Immediate entityVertexConsumers = VertexConsumerProvider.immediate(this.entityBuilders, new BufferBuilder(256));
    private final VertexConsumerProvider.Immediate effectVertexConsumers = VertexConsumerProvider.immediate(new BufferBuilder(256));
    private final OutlineVertexConsumerProvider outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);

    private static void method_23798(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> object2ObjectLinkedOpenHashMap, RenderLayer renderLayer) {
        object2ObjectLinkedOpenHashMap.put(renderLayer, new BufferBuilder(renderLayer.getExpectedBufferSize()));
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

