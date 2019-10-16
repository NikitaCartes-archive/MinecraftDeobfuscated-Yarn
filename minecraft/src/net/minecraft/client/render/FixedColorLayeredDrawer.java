package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FixedColorLayeredDrawer implements LayeredVertexConsumerStorage {
	private final LayeredVertexConsumerStorage.Drawer parent;
	private final LayeredVertexConsumerStorage.Drawer plainDrawer = LayeredVertexConsumerStorage.makeDrawer(new BufferBuilder(256));
	private int red = 255;
	private int green = 255;
	private int blue = 255;
	private int alpha = 255;

	public FixedColorLayeredDrawer(LayeredVertexConsumerStorage.Drawer drawer) {
		this.parent = drawer;
	}

	@Override
	public net.minecraft.client.render.VertexConsumer getBuffer(RenderLayer renderLayer) {
		net.minecraft.client.render.VertexConsumer vertexConsumer = this.parent.getBuffer(renderLayer);
		Optional<Identifier> optional = renderLayer.getTexture();
		if (optional.isPresent()) {
			net.minecraft.client.render.VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer(RenderLayer.getOutline((Identifier)optional.get()));
			FixedColorLayeredDrawer.VertexConsumer vertexConsumer3 = new FixedColorLayeredDrawer.VertexConsumer(
				vertexConsumer2, this.red, this.green, this.blue, this.alpha
			);
			return new DelegatingVertexConsumer(ImmutableList.of(vertexConsumer3, vertexConsumer));
		} else {
			return vertexConsumer;
		}
	}

	public void setColor(int i, int j, int k, int l) {
		this.red = i;
		this.green = j;
		this.blue = k;
		this.alpha = l;
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

		private VertexConsumer(net.minecraft.client.render.VertexConsumer vertexConsumer, int i, int j, int k, int l) {
			this.delegate = vertexConsumer;
			super.fixedColor(i, j, k, l);
		}

		@Override
		public void fixedColor(int i, int j, int k, int l) {
		}

		@Override
		public net.minecraft.client.render.VertexConsumer vertex(double d, double e, double f) {
			this.x = d;
			this.y = e;
			this.z = f;
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer color(int i, int j, int k, int l) {
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer texture(float f, float g) {
			this.u = f;
			this.v = g;
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer overlay(int i, int j) {
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer light(int i, int j) {
			return this;
		}

		@Override
		public net.minecraft.client.render.VertexConsumer normal(float f, float g, float h) {
			return this;
		}

		@Override
		public void next() {
			this.delegate.vertex(this.x, this.y, this.z).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(this.u, this.v).next();
		}
	}
}
