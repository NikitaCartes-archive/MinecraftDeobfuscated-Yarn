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
	static LayeredVertexConsumerStorage.Drawer makeDrawer(BufferBuilder bufferBuilder) {
		return makeDrawer(ImmutableMap.of(), bufferBuilder);
	}

	static LayeredVertexConsumerStorage.Drawer makeDrawer(Map<RenderLayer, BufferBuilder> map, BufferBuilder bufferBuilder) {
		return new LayeredVertexConsumerStorage.Drawer(bufferBuilder, map);
	}

	VertexConsumer getBuffer(RenderLayer renderLayer);

	@Environment(EnvType.CLIENT)
	public static class Drawer implements LayeredVertexConsumerStorage {
		protected final BufferBuilder sharedConsumer;
		protected final Map<RenderLayer, BufferBuilder> layerSpecificConsumers;
		protected Optional<RenderLayer> currentLayer = Optional.empty();
		protected final Set<BufferBuilder> activeConsumers = Sets.<BufferBuilder>newHashSet();

		protected Drawer(BufferBuilder bufferBuilder, Map<RenderLayer, BufferBuilder> map) {
			this.sharedConsumer = bufferBuilder;
			this.layerSpecificConsumers = map;
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer renderLayer) {
			Optional<RenderLayer> optional = Optional.of(renderLayer);
			BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
			if (!Objects.equals(this.currentLayer, optional)) {
				if (this.currentLayer.isPresent()) {
					RenderLayer renderLayer2 = (RenderLayer)this.currentLayer.get();
					if (!this.layerSpecificConsumers.containsKey(renderLayer2)) {
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

		private BufferBuilder getConsumer(RenderLayer renderLayer) {
			return (BufferBuilder)this.layerSpecificConsumers.getOrDefault(renderLayer, this.sharedConsumer);
		}

		public void draw() {
			this.currentLayer.ifPresent(renderLayerx -> {
				VertexConsumer vertexConsumer = this.getBuffer(renderLayerx);
				if (vertexConsumer == this.sharedConsumer) {
					this.draw(renderLayerx);
				}
			});

			for (RenderLayer renderLayer : this.layerSpecificConsumers.keySet()) {
				this.draw(renderLayer);
			}
		}

		public void draw(RenderLayer renderLayer) {
			BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
			boolean bl = Objects.equals(this.currentLayer, Optional.of(renderLayer));
			if (bl || bufferBuilder != this.sharedConsumer) {
				if (this.activeConsumers.remove(bufferBuilder)) {
					renderLayer.draw(bufferBuilder);
					if (bl) {
						this.currentLayer = Optional.empty();
					}
				}
			}
		}
	}
}
