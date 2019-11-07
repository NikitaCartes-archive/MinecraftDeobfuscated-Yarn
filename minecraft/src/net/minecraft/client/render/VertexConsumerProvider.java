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
public interface VertexConsumerProvider {
	static VertexConsumerProvider.Immediate immediate(BufferBuilder builder) {
		return immediate(ImmutableMap.of(), builder);
	}

	static VertexConsumerProvider.Immediate immediate(Map<RenderLayer, BufferBuilder> layerBuilders, BufferBuilder fallbackBuilder) {
		return new VertexConsumerProvider.Immediate(fallbackBuilder, layerBuilders);
	}

	VertexConsumer getBuffer(RenderLayer layer);

	@Environment(EnvType.CLIENT)
	public static class Immediate implements VertexConsumerProvider {
		protected final BufferBuilder defaultBuilder;
		protected final Map<RenderLayer, BufferBuilder> layerBuilders;
		protected Optional<RenderLayer> currentLayer = Optional.empty();
		protected final Set<BufferBuilder> activeConsumers = Sets.<BufferBuilder>newHashSet();

		protected Immediate(BufferBuilder defaultBuilder, Map<RenderLayer, BufferBuilder> layerBuilders) {
			this.defaultBuilder = defaultBuilder;
			this.layerBuilders = layerBuilders;
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer renderLayer) {
			Optional<RenderLayer> optional = Optional.of(renderLayer);
			BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
			if (!Objects.equals(this.currentLayer, optional)) {
				if (this.currentLayer.isPresent()) {
					RenderLayer renderLayer2 = (RenderLayer)this.currentLayer.get();
					if (!this.layerBuilders.containsKey(renderLayer2)) {
						this.draw(renderLayer2);
					}
				}

				if (this.activeConsumers.add(bufferBuilder)) {
					bufferBuilder.begin(renderLayer.getDrawMode(), renderLayer.getVertexFormat());
				}

				this.currentLayer = optional;
			}

			return bufferBuilder;
		}

		private BufferBuilder getConsumer(RenderLayer layer) {
			return (BufferBuilder)this.layerBuilders.getOrDefault(layer, this.defaultBuilder);
		}

		public void draw() {
			this.method_23796(0, 0, 0);
		}

		public void method_23796(int i, int j, int k) {
			this.currentLayer.ifPresent(renderLayerx -> {
				VertexConsumer vertexConsumer = this.getBuffer(renderLayerx);
				if (vertexConsumer == this.defaultBuilder) {
					this.method_23797(renderLayerx, i, j, k);
				}
			});

			for (RenderLayer renderLayer : this.layerBuilders.keySet()) {
				this.method_23797(renderLayer, i, j, k);
			}
		}

		public void draw(RenderLayer layer) {
			this.method_23797(layer, 0, 0, 0);
		}

		public void method_23797(RenderLayer renderLayer, int i, int j, int k) {
			BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
			boolean bl = Objects.equals(this.currentLayer, Optional.of(renderLayer));
			if (bl || bufferBuilder != this.defaultBuilder) {
				if (this.activeConsumers.remove(bufferBuilder)) {
					renderLayer.draw(bufferBuilder, i, j, k);
					if (bl) {
						this.currentLayer = Optional.empty();
					}
				}
			}
		}
	}
}
