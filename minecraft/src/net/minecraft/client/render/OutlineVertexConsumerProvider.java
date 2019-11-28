package net.minecraft.client.render;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OutlineVertexConsumerProvider implements VertexConsumerProvider {
	private final VertexConsumerProvider.Immediate parent;
	private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferBuilder(256));
	private int red = 255;
	private int green = 255;
	private int blue = 255;
	private int alpha = 255;

	public OutlineVertexConsumerProvider(VertexConsumerProvider.Immediate immediate) {
		this.parent = immediate;
	}

	@Override
	public net.minecraft.client.render.VertexConsumer getBuffer(RenderLayer renderLayer) {
		net.minecraft.client.render.VertexConsumer vertexConsumer = this.parent.getBuffer(renderLayer);
		Optional<RenderLayer> optional = renderLayer.getTexture();
		if (optional.isPresent()) {
			net.minecraft.client.render.VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer((RenderLayer)optional.get());
			OutlineVertexConsumerProvider.VertexConsumer vertexConsumer3 = new OutlineVertexConsumerProvider.VertexConsumer(
				vertexConsumer2, this.red, this.green, this.blue, this.alpha
			);
			return VertexConsumers.dual(vertexConsumer3, vertexConsumer);
		} else {
			return vertexConsumer;
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
	static class VertexConsumer extends FixedColorVertexConsumer {
		private final net.minecraft.client.render.VertexConsumer delegate;
		private double x;
		private double y;
		private double z;
		private float u;
		private float v;

		private VertexConsumer(net.minecraft.client.render.VertexConsumer delegate, int red, int green, int blue, int alpha) {
			this.delegate = delegate;
			super.fixedColor(red, green, blue, alpha);
		}

		@Override
		public void fixedColor(int red, int green, int blue, int alpha) {
		}

		@Override
		public net.minecraft.client.render.VertexConsumer vertex(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer color(int red, int green, int blue, int alpha) {
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer texture(float u, float v) {
			this.u = u;
			this.v = v;
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer overlay(int u, int v) {
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer light(int u, int v) {
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer normal(float x, float y, float z) {
			return this;
		}

		@Override
		public void vertex(
			float x,
			float y,
			float z,
			float red,
			float green,
			float blue,
			float alpha,
			float u,
			float v,
			int overlay,
			int light,
			float normalX,
			float normalY,
			float normalZ
		) {
			this.delegate.vertex((double)x, (double)y, (double)z).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(u, v).next();
		}

		@Override
		public void next() {
			this.delegate.vertex(this.x, this.y, this.z).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(this.u, this.v).next();
		}
	}
}
