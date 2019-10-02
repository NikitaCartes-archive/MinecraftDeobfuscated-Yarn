package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4618;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LayeredBufferBuilderStorage {
	private final BlockLayeredBufferBuilder blockBufferBuilders = new BlockLayeredBufferBuilder();
	private final SortedMap<RenderLayer, BufferBuilder> bufferBuilders = SystemUtil.consume(
		new Object2ObjectLinkedOpenHashMap<>(), object2ObjectLinkedOpenHashMap -> {
			for (RenderLayer renderLayer : RenderLayer.getBlockLayers()) {
				object2ObjectLinkedOpenHashMap.put(renderLayer, this.blockBufferBuilders.get(renderLayer));
			}

			object2ObjectLinkedOpenHashMap.put(RenderLayer.TRANSLUCENT_NO_CRUMBLING, new BufferBuilder(RenderLayer.TRANSLUCENT_NO_CRUMBLING.getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.GLINT, new BufferBuilder(RenderLayer.GLINT.getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.ENTITY_GLINT, new BufferBuilder(RenderLayer.ENTITY_GLINT.getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.WATER_MASK, new BufferBuilder(RenderLayer.WATER_MASK.getExpectedBufferSize()));
		}
	);
	private final LayeredVertexConsumerStorage.class_4598 field_20958 = LayeredVertexConsumerStorage.method_22992(this.bufferBuilders, new BufferBuilder(256));
	private final LayeredVertexConsumerStorage.class_4598 field_20959 = LayeredVertexConsumerStorage.method_22991(new BufferBuilder(256));
	private final class_4618 field_20961 = new class_4618(this.field_20958);

	public BlockLayeredBufferBuilder getBlockBufferBuilders() {
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
