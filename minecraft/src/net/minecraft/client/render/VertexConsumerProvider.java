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
	static VertexConsumerProvider.Immediate immediate(BufferBuilder buffer) {
		return immediate(ImmutableMap.of(), buffer);
	}

	static VertexConsumerProvider.Immediate immediate(Map<RenderLayer, BufferBuilder> layerBuffers, BufferBuilder fallbackBuffer) {
		return new VertexConsumerProvider.Immediate(fallbackBuffer, layerBuffers);
	}

	VertexConsumer getBuffer(RenderLayer layer);

	@Environment(EnvType.CLIENT)
	public static class Immediate implements VertexConsumerProvider {
		protected final BufferBuilder fallbackBuffer;
		protected final Map<RenderLayer, BufferBuilder> layerBuffers;
		protected Optional<RenderLayer> currentLayer = Optional.empty();
		protected final Set<BufferBuilder> activeConsumers = Sets.<BufferBuilder>newHashSet();

		protected Immediate(BufferBuilder fallbackBuffer, Map<RenderLayer, BufferBuilder> layerBuffers) {
			this.fallbackBuffer = fallbackBuffer;
			this.layerBuffers = layerBuffers;
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer renderLayer) {
			Optional<RenderLayer> optional = renderLayer.asOptional();
			BufferBuilder bufferBuilder = this.getBufferInternal(renderLayer);
			if (!Objects.equals(this.currentLayer, optional)) {
				if (this.currentLayer.isPresent()) {
					RenderLayer renderLayer2 = (RenderLayer)this.currentLayer.get();
					if (!this.layerBuffers.containsKey(renderLayer2)) {
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

		private BufferBuilder getBufferInternal(RenderLayer layer) {
			return (BufferBuilder)this.layerBuffers.getOrDefault(layer, this.fallbackBuffer);
		}

		public void draw() {
			this.currentLayer.ifPresent(renderLayerx -> {
				VertexConsumer vertexConsumer = this.getBuffer(renderLayerx);
				if (vertexConsumer == this.fallbackBuffer) {
					this.draw(renderLayerx);
				}
			});

			for (RenderLayer renderLayer : this.layerBuffers.keySet()) {
				this.draw(renderLayer);
			}
		}

		public void draw(RenderLayer layer) {
			BufferBuilder bufferBuilder = this.getBufferInternal(layer);
			boolean bl = Objects.equals(this.currentLayer, layer.asOptional());
			if (bl || bufferBuilder != this.fallbackBuffer) {
				if (this.activeConsumers.remove(bufferBuilder)) {
					layer.draw(bufferBuilder, 0, 0, 0);
					if (bl) {
						this.currentLayer = Optional.empty();
					}
				}
			}
		}
	}
}
