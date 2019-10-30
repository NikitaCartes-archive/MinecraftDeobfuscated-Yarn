package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class BufferBuilderStorage {
	private final BlockBufferBuilderStorage blockBuilders = new BlockBufferBuilderStorage();
	private final SortedMap<RenderLayer, BufferBuilder> entityBuilders = Util.create(
		new Object2ObjectLinkedOpenHashMap<>(),
		object2ObjectLinkedOpenHashMap -> {
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBuilders.get(RenderLayer.getSolid()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBuilders.get(RenderLayer.getCutout()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntityNoOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBuilders.get(RenderLayer.getCutoutMipped()));
			object2ObjectLinkedOpenHashMap.put(
				RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBuilders.get(RenderLayer.getTranslucent())
			);
			object2ObjectLinkedOpenHashMap.put(
				RenderLayer.getTranslucentNoCrumbling(), new BufferBuilder(RenderLayer.getTranslucentNoCrumbling().getExpectedBufferSize())
			);
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getGlint(), new BufferBuilder(RenderLayer.getGlint().getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntityGlint(), new BufferBuilder(RenderLayer.getEntityGlint().getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getWaterMask(), new BufferBuilder(RenderLayer.getWaterMask().getExpectedBufferSize()));
		}
	);
	private final VertexConsumerProvider.Immediate entityVertexConsumers = VertexConsumerProvider.immediate(this.entityBuilders, new BufferBuilder(256));
	private final VertexConsumerProvider.Immediate effectVertexConsumers = VertexConsumerProvider.immediate(new BufferBuilder(256));
	private final OutlineVertexConsumerProvider outlineVertexConsumers = new OutlineVertexConsumerProvider(this.entityVertexConsumers);

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
