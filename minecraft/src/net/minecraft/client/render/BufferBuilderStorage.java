package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.BlockBufferBuilderPool;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class BufferBuilderStorage {
	private final BlockBufferBuilderStorage blockBufferBuilders = new BlockBufferBuilderStorage();
	private final BlockBufferBuilderPool blockBufferBuildersPool;
	private final VertexConsumerProvider.Immediate entityVertexConsumers;
	private final VertexConsumerProvider.Immediate effectVertexConsumers;
	private final OutlineVertexConsumerProvider outlineVertexConsumers;

	public BufferBuilderStorage(int maxBlockBuildersPoolSize) {
		this.blockBufferBuildersPool = BlockBufferBuilderPool.allocate(maxBlockBuildersPoolSize);
		SortedMap<RenderLayer, BufferBuilder> sortedMap = Util.make(new Object2ObjectLinkedOpenHashMap<>(), map -> {
			map.put(TexturedRenderLayers.getEntitySolid(), this.blockBufferBuilders.get(RenderLayer.getSolid()));
			map.put(TexturedRenderLayers.getEntityCutout(), this.blockBufferBuilders.get(RenderLayer.getCutout()));
			map.put(TexturedRenderLayers.getBannerPatterns(), this.blockBufferBuilders.get(RenderLayer.getCutoutMipped()));
			map.put(TexturedRenderLayers.getEntityTranslucentCull(), this.blockBufferBuilders.get(RenderLayer.getTranslucent()));
			assignBufferBuilder(map, TexturedRenderLayers.getShieldPatterns());
			assignBufferBuilder(map, TexturedRenderLayers.getBeds());
			assignBufferBuilder(map, TexturedRenderLayers.getShulkerBoxes());
			assignBufferBuilder(map, TexturedRenderLayers.getSign());
			assignBufferBuilder(map, TexturedRenderLayers.getHangingSign());
			map.put(TexturedRenderLayers.getChest(), new BufferBuilder(786432));
			assignBufferBuilder(map, RenderLayer.getArmorGlint());
			assignBufferBuilder(map, RenderLayer.getArmorEntityGlint());
			assignBufferBuilder(map, RenderLayer.getGlint());
			assignBufferBuilder(map, RenderLayer.getDirectGlint());
			assignBufferBuilder(map, RenderLayer.getGlintTranslucent());
			assignBufferBuilder(map, RenderLayer.getEntityGlint());
			assignBufferBuilder(map, RenderLayer.getDirectEntityGlint());
			assignBufferBuilder(map, RenderLayer.getWaterMask());
			ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(renderLayer -> assignBufferBuilder(map, renderLayer));
		});
		this.effectVertexConsumers = VertexConsumerProvider.immediate(new BufferBuilder(1536));
		this.entityVertexConsumers = VertexConsumerProvider.immediate(sortedMap, new BufferBuilder(786432));
		this.outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);
	}

	private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> builderStorage, RenderLayer layer) {
		builderStorage.put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
	}

	public BlockBufferBuilderStorage getBlockBufferBuilders() {
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
