package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.AbstractVertexConsumer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DelegatingVertexConsumer;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_4618 implements LayeredVertexConsumerStorage {
	private final LayeredVertexConsumerStorage.class_4598 field_21058;
	private final LayeredVertexConsumerStorage.class_4598 field_21059 = LayeredVertexConsumerStorage.method_22991(new BufferBuilder(256));
	private int field_21060 = 255;
	private int field_21061 = 255;
	private int field_21062 = 255;
	private int field_21063 = 255;

	public class_4618(LayeredVertexConsumerStorage.class_4598 arg) {
		this.field_21058 = arg;
	}

	@Override
	public VertexConsumer getBuffer(RenderLayer renderLayer) {
		VertexConsumer vertexConsumer = this.field_21058.getBuffer(renderLayer);
		Optional<Identifier> optional = renderLayer.method_23289();
		if (optional.isPresent()) {
			VertexConsumer vertexConsumer2 = this.field_21059.getBuffer(RenderLayer.method_23287((Identifier)optional.get()));
			class_4618.class_4586 lv = new class_4618.class_4586(vertexConsumer2, this.field_21060, this.field_21061, this.field_21062, this.field_21063);
			return new DelegatingVertexConsumer(ImmutableList.of(lv, vertexConsumer));
		} else {
			return vertexConsumer;
		}
	}

	public void method_23286(int i, int j, int k, int l) {
		this.field_21060 = i;
		this.field_21061 = j;
		this.field_21062 = k;
		this.field_21063 = l;
	}

	public void method_23285() {
		this.field_21059.method_22993();
	}

	@Environment(EnvType.CLIENT)
	static class class_4586 extends AbstractVertexConsumer {
		private final VertexConsumer field_20897;
		private double field_21064;
		private double field_21065;
		private double field_21066;
		private float field_21067;
		private float field_21068;

		private class_4586(VertexConsumer vertexConsumer, int i, int j, int k, int l) {
			this.field_20897 = vertexConsumer;
			super.method_22901(i, j, k, l);
		}

		@Override
		public void method_22901(int i, int j, int k, int l) {
		}

		@Override
		public void defaultOverlay(int i, int j) {
		}

		@Override
		public void clearDefaultOverlay() {
		}

		@Override
		public VertexConsumer vertex(double d, double e, double f) {
			this.field_21064 = d;
			this.field_21065 = e;
			this.field_21066 = f;
			return this;
		}

		@Override
		public VertexConsumer color(int i, int j, int k, int l) {
			return this;
		}

		@Override
		public VertexConsumer texture(float f, float g) {
			this.field_21067 = f;
			this.field_21068 = g;
			return this;
		}

		@Override
		public VertexConsumer overlay(int i, int j) {
			return this;
		}

		@Override
		public VertexConsumer light(int i, int j) {
			return this;
		}

		@Override
		public VertexConsumer normal(float f, float g, float h) {
			return this;
		}

		@Override
		public void next() {
			this.field_20897
				.vertex(this.field_21064, this.field_21065, this.field_21066)
				.color(this.field_20890, this.field_20891, this.field_20892, this.field_20893)
				.texture(this.field_21067, this.field_21068)
				.next();
		}
	}
}
