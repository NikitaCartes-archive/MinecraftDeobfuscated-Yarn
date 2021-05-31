package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class GlyphRenderer {
	private final RenderLayer textLayer;
	private final RenderLayer seeThroughTextLayer;
	private final RenderLayer field_33999;
	private final float minU;
	private final float maxU;
	private final float minV;
	private final float maxV;
	private final float minX;
	private final float maxX;
	private final float minY;
	private final float maxY;

	public GlyphRenderer(
		RenderLayer textLayer, RenderLayer seeThroughTextLayer, RenderLayer renderLayer, float f, float g, float h, float i, float j, float k, float l, float m
	) {
		this.textLayer = textLayer;
		this.seeThroughTextLayer = seeThroughTextLayer;
		this.field_33999 = renderLayer;
		this.minU = f;
		this.maxU = g;
		this.minV = h;
		this.maxV = i;
		this.minX = j;
		this.maxX = k;
		this.minY = l;
		this.maxY = m;
	}

	public void draw(boolean italic, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) {
		int i = 3;
		float f = x + this.minX;
		float g = x + this.maxX;
		float h = this.minY - 3.0F;
		float j = this.maxY - 3.0F;
		float k = y + h;
		float l = y + j;
		float m = italic ? 1.0F - 0.25F * h : 0.0F;
		float n = italic ? 1.0F - 0.25F * j : 0.0F;
		vertexConsumer.vertex(matrix, f + m, k, 0.0F).color(red, green, blue, alpha).texture(this.minU, this.minV).light(light).next();
		vertexConsumer.vertex(matrix, f + n, l, 0.0F).color(red, green, blue, alpha).texture(this.minU, this.maxV).light(light).next();
		vertexConsumer.vertex(matrix, g + n, l, 0.0F).color(red, green, blue, alpha).texture(this.maxU, this.maxV).light(light).next();
		vertexConsumer.vertex(matrix, g + m, k, 0.0F).color(red, green, blue, alpha).texture(this.maxU, this.minV).light(light).next();
	}

	public void drawRectangle(GlyphRenderer.Rectangle rectangle, Matrix4f matrix, VertexConsumer vertexConsumer, int light) {
		vertexConsumer.vertex(matrix, rectangle.minX, rectangle.minY, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.minU, this.minV)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.maxX, rectangle.minY, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.minU, this.maxV)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.maxX, rectangle.maxY, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.maxU, this.maxV)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.minX, rectangle.maxY, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.maxU, this.minV)
			.light(light)
			.next();
	}

	public RenderLayer getLayer(TextRenderer.class_6415 arg) {
		switch (arg) {
			case field_33993:
			default:
				return this.textLayer;
			case field_33994:
				return this.seeThroughTextLayer;
			case field_33995:
				return this.field_33999;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Rectangle {
		protected final float minX;
		protected final float minY;
		protected final float maxX;
		protected final float maxY;
		protected final float zIndex;
		protected final float red;
		protected final float green;
		protected final float blue;
		protected final float alpha;

		public Rectangle(float minX, float minY, float maxX, float maxY, float zIndex, float red, float green, float blue, float alpha) {
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
			this.zIndex = zIndex;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
	}
}
