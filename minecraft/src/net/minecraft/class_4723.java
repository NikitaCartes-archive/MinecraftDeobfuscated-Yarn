package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public class class_4723 implements VertexConsumer {
	private final VertexConsumer field_21730;
	private final Sprite field_21731;

	public class_4723(VertexConsumer vertexConsumer, Sprite sprite) {
		this.field_21730 = vertexConsumer;
		this.field_21731 = sprite;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		return this.field_21730.vertex(x, y, z);
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		return this.field_21730.color(red, green, blue, alpha);
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		return this.field_21730.texture(this.field_21731.getFrameU((double)(u * 16.0F)), this.field_21731.getFrameV((double)(v * 16.0F)));
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		return this.field_21730.overlay(u, v);
	}

	@Override
	public VertexConsumer light(int u, int v) {
		return this.field_21730.light(u, v);
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		return this.field_21730.normal(x, y, z);
	}

	@Override
	public void next() {
		this.field_21730.next();
	}

	@Override
	public void method_23919(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
		this.field_21730
			.method_23919(f, g, h, i, j, k, l, this.field_21731.getFrameU((double)(m * 16.0F)), this.field_21731.getFrameV((double)(n * 16.0F)), o, p, q, r, s);
	}
}
