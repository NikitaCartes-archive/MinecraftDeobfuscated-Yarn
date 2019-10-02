package net.minecraft.client.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface LayeredVertexConsumerStorage {
	static LayeredVertexConsumerStorage.class_4598 method_22991(BufferBuilder bufferBuilder) {
		return method_22992(ImmutableMap.of(), bufferBuilder);
	}

	static LayeredVertexConsumerStorage.class_4598 method_22992(Map<RenderLayer, BufferBuilder> map, BufferBuilder bufferBuilder) {
		return new LayeredVertexConsumerStorage.class_4598(bufferBuilder, map);
	}

	VertexConsumer getBuffer(RenderLayer renderLayer);

	@Environment(EnvType.CLIENT)
	public static class class_4598 implements LayeredVertexConsumerStorage {
		protected final BufferBuilder field_20952;
		protected final Map<RenderLayer, BufferBuilder> field_20953;
		protected Optional<RenderLayer> field_20954 = Optional.empty();
		protected final Set<BufferBuilder> field_20955 = Sets.<BufferBuilder>newHashSet();

		protected class_4598(BufferBuilder bufferBuilder, Map<RenderLayer, BufferBuilder> map) {
			this.field_20952 = bufferBuilder;
			this.field_20953 = map;
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer renderLayer) {
			Optional<RenderLayer> optional = Optional.of(renderLayer);
			BufferBuilder bufferBuilder = this.method_22995(renderLayer);
			if (!Objects.equals(this.field_20954, optional)) {
				if (this.field_20954.isPresent()) {
					RenderLayer renderLayer2 = (RenderLayer)this.field_20954.get();
					if (!this.field_20953.containsKey(renderLayer2)) {
						this.method_22994(renderLayer2);
					}
				}

				if (this.field_20955.add(bufferBuilder)) {
					bufferBuilder.begin(renderLayer.getDrawMode(), renderLayer.getVertexFormat());
				}

				this.field_20954 = optional;
			}

			return bufferBuilder;
		}

		private BufferBuilder method_22995(RenderLayer renderLayer) {
			return (BufferBuilder)this.field_20953.getOrDefault(renderLayer, this.field_20952);
		}

		public void method_22993() {
			this.field_20954.ifPresent(renderLayerx -> {
				VertexConsumer vertexConsumer = this.getBuffer(renderLayerx);
				if (vertexConsumer == this.field_20952) {
					this.method_22994(renderLayerx);
				}
			});

			for (RenderLayer renderLayer : this.field_20953.keySet()) {
				this.method_22994(renderLayer);
			}
		}

		public void method_22994(RenderLayer renderLayer) {
			BufferBuilder bufferBuilder = this.method_22995(renderLayer);
			if (this.field_20955.remove(bufferBuilder)) {
				renderLayer.method_23012(bufferBuilder);
				if (Objects.equals(this.field_20954, Optional.of(renderLayer))) {
					this.field_20954 = Optional.empty();
				}
			}
		}
	}
}
