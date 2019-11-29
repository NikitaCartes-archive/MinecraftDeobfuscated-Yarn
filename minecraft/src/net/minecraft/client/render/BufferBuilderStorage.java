package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class BufferBuilderStorage {
	private final BlockBufferBuilderStorage blockBuilders = new BlockBufferBuilderStorage();
	private final SortedMap<RenderLayer, BufferBuilder> entityBuilders = Util.create(new Object2ObjectLinkedOpenHashMap<>(), object2ObjectLinkedOpenHashMap -> {
		object2ObjectLinkedOpenHashMap.put(TexturedRenderLayers.getEntitySolid(), this.blockBuilders.get(RenderLayer.getSolid()));
		object2ObjectLinkedOpenHashMap.put(TexturedRenderLayers.getEntityCutout(), this.blockBuilders.get(RenderLayer.getCutout()));
		object2ObjectLinkedOpenHashMap.put(TexturedRenderLayers.getBannerPatterns(), this.blockBuilders.get(RenderLayer.getCutoutMipped()));
		object2ObjectLinkedOpenHashMap.put(TexturedRenderLayers.getEntityTranslucent(), this.blockBuilders.get(RenderLayer.getTranslucent()));
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getShieldPatterns());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getBeds());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getShulkerBoxes());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getSign());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, TexturedRenderLayers.getChest());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, RenderLayer.getTranslucentNoCrumbling());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, RenderLayer.getGlint());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, RenderLayer.getEntityGlint());
		assignBufferBuilder(object2ObjectLinkedOpenHashMap, RenderLayer.getWaterMask());
		ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(renderLayer -> assignBufferBuilder(object2ObjectLinkedOpenHashMap, renderLayer));
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
