package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4618;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilderStorage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LayeredBufferBuilderStorage {
	private final BlockLayeredBufferBuilderStorage blockBufferBuilders = new BlockLayeredBufferBuilderStorage();
	private final SortedMap<RenderLayer, BufferBuilder> bufferBuilders = SystemUtil.consume(
		new Object2ObjectLinkedOpenHashMap<>(),
		object2ObjectLinkedOpenHashMap -> {
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBufferBuilders.get(RenderLayer.getSolid()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBufferBuilders.get(RenderLayer.getCutout()));
			object2ObjectLinkedOpenHashMap.put(
				RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBufferBuilders.get(RenderLayer.getTranslucent())
			);
			object2ObjectLinkedOpenHashMap.put(
				RenderLayer.getTranslucentNoCrumbling(), new BufferBuilder(RenderLayer.getTranslucentNoCrumbling().getExpectedBufferSize())
			);
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getGlint(), new BufferBuilder(RenderLayer.getGlint().getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntityGlint(), new BufferBuilder(RenderLayer.getEntityGlint().getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getWaterMask(), new BufferBuilder(RenderLayer.getWaterMask().getExpectedBufferSize()));
		}
	);
	private final LayeredVertexConsumerStorage.class_4598 field_20958 = LayeredVertexConsumerStorage.method_22992(this.bufferBuilders, new BufferBuilder(256));
	private final LayeredVertexConsumerStorage.class_4598 field_20959 = LayeredVertexConsumerStorage.method_22991(new BufferBuilder(256));
	private final class_4618 field_20961 = new class_4618(this.field_20958);

	public BlockLayeredBufferBuilderStorage getBlockBufferBuilders() {
		return this.blockBufferBuilders;
	}

	public LayeredVertexConsumerStorage.class_4598 method_23000() {
		return this.field_20958;
	}

	public LayeredVertexConsumerStorage.class_4598 method_23001() {
		return this.field_20959;
	}

	public class_4618 method_23003() {
		return this.field_20961;
	}
}
