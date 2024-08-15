package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.BlockBufferBuilderPool;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class BufferBuilderStorage {
	private final BlockBufferAllocatorStorage blockBufferBuilders = new BlockBufferAllocatorStorage();
	private final BlockBufferBuilderPool blockBufferBuildersPool;
	private final VertexConsumerProvider.Immediate entityVertexConsumers;
	private final VertexConsumerProvider.Immediate effectVertexConsumers;
	private final OutlineVertexConsumerProvider outlineVertexConsumers;

	public BufferBuilderStorage(int maxBlockBuildersPoolSize) {
		this.blockBufferBuildersPool = BlockBufferBuilderPool.allocate(maxBlockBuildersPoolSize);
		SequencedMap<RenderLayer, BufferAllocator> sequencedMap = Util.make(new Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>(), map -> {
			map.put(TexturedRenderLayers.getEntitySolid(), this.blockBufferBuilders.get(RenderLayer.getSolid()));
			map.put(TexturedRenderLayers.getEntityCutout(), this.blockBufferBuilders.get(RenderLayer.getCutout()));
			map.put(TexturedRenderLayers.getBannerPatterns(), this.blockBufferBuilders.get(RenderLayer.getCutoutMipped()));
			map.put(TexturedRenderLayers.getEntityTranslucentCull(), this.blockBufferBuilders.get(RenderLayer.getTranslucent()));
			assignBufferBuilder(map, TexturedRenderLayers.getShieldPatterns());
			assignBufferBuilder(map, TexturedRenderLayers.getBeds());
			assignBufferBuilder(map, TexturedRenderLayers.getShulkerBoxes());
			assignBufferBuilder(map, TexturedRenderLayers.getSign());
			assignBufferBuilder(map, TexturedRenderLayers.getHangingSign());
			map.put(TexturedRenderLayers.getChest(), new BufferAllocator(786432));
			assignBufferBuilder(map, RenderLayer.getArmorEntityGlint());
			assignBufferBuilder(map, RenderLayer.getGlint());
			assignBufferBuilder(map, RenderLayer.getGlintTranslucent());
			assignBufferBuilder(map, RenderLayer.getEntityGlint());
			assignBufferBuilder(map, RenderLayer.getDirectEntityGlint());
			assignBufferBuilder(map, RenderLayer.getWaterMask());
		});
		this.entityVertexConsumers = VertexConsumerProvider.immediate(sequencedMap, new BufferAllocator(786432));
		this.outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);
		SequencedMap<RenderLayer, BufferAllocator> sequencedMap2 = Util.make(
			new Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator>(),
			object2ObjectLinkedOpenHashMap -> ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS
					.forEach(renderLayer -> assignBufferBuilder(object2ObjectLinkedOpenHashMap, renderLayer))
		);
		this.effectVertexConsumers = VertexConsumerProvider.immediate(sequencedMap2, new BufferAllocator(0));
	}

	private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage, RenderLayer layer) {
		builderStorage.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
	}

	public BlockBufferAllocatorStorage getBlockBufferBuilders() {
		return this.blockBufferBuilders;
	}

	public BlockBufferBuilderPool getBlockBufferBuildersPool() {
		return this.blockBufferBuildersPool;
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
