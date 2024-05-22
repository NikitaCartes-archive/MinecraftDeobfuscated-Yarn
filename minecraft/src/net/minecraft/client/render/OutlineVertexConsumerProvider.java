package net.minecraft.client.render;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class OutlineVertexConsumerProvider implements VertexConsumerProvider {
	private final VertexConsumerProvider.Immediate parent;
	private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferAllocator(1536));
	private int red = 255;
	private int green = 255;
	private int blue = 255;
	private int alpha = 255;

	public OutlineVertexConsumerProvider(VertexConsumerProvider.Immediate parent) {
		this.parent = parent;
	}

	@Override
	public VertexConsumer getBuffer(RenderLayer renderLayer) {
		if (renderLayer.isOutline()) {
			VertexConsumer vertexConsumer = this.plainDrawer.getBuffer(renderLayer);
			return new OutlineVertexConsumerProvider.OutlineVertexConsumer(vertexConsumer, this.red, this.green, this.blue, this.alpha);
		} else {
			VertexConsumer vertexConsumer = this.parent.getBuffer(renderLayer);
			Optional<RenderLayer> optional = renderLayer.getAffectedOutline();
			if (optional.isPresent()) {
				VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer((RenderLayer)optional.get());
				OutlineVertexConsumerProvider.OutlineVertexConsumer outlineVertexConsumer = new OutlineVertexConsumerProvider.OutlineVertexConsumer(
					vertexConsumer2, this.red, this.green, this.blue, this.alpha
				);
				return VertexConsumers.union(outlineVertexConsumer, vertexConsumer);
			} else {
				return vertexConsumer;
			}
		}
	}

	public void setColor(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public void draw() {
		this.plainDrawer.draw();
	}

	@Environment(EnvType.CLIENT)
	static record OutlineVertexConsumer(VertexConsumer delegate, int color) implements VertexConsumer {
		public OutlineVertexConsumer(VertexConsumer delegate, int red, int green, int blue, int alpha) {
			this(delegate, ColorHelper.Argb.getArgb(alpha, red, green, blue));
		}

		@Override
		public VertexConsumer vertex(float x, float y, float z) {
			this.delegate.vertex(x, y, z).color(this.color);
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			this.delegate.texture(u, v);
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			return this;
		}
	}
}
