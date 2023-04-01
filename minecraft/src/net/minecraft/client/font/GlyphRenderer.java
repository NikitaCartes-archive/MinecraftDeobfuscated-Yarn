package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class GlyphRenderer {
	private final RenderLayer textLayer;
	private final RenderLayer seeThroughTextLayer;
	private final RenderLayer polygonOffsetTextLayer;
	private final float minU;
	private final float maxU;
	private final float minV;
	private final float maxV;
	private final float minX;
	private final float maxX;
	private final float minY;
	private final float maxY;

	public GlyphRenderer(
		RenderLayer textLayer,
		RenderLayer seeThroughTextLayer,
		RenderLayer polygonOffsetTextLayer,
		float minU,
		float maxU,
		float minV,
		float maxV,
		float minX,
		float maxX,
		float minY,
		float maxY
	) {
		this.textLayer = textLayer;
		this.seeThroughTextLayer = seeThroughTextLayer;
		this.polygonOffsetTextLayer = polygonOffsetTextLayer;
		this.minU = minU;
		this.maxU = maxU;
		this.minV = minV;
		this.maxV = maxV;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public void draw(boolean bl, boolean bl2, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer, float h, float i, float j, float k, int l) {
		int m = 3;
		float n = f + this.minX;
		float o = f + this.maxX;
		float p = this.minY - 3.0F;
		float q = this.maxY - 3.0F;
		float r = g + p;
		float s = g + q;
		float t = bl2 ? 1.0F - 0.25F * p : 0.0F;
		float u = bl2 ? 1.0F - 0.25F * q : 0.0F;
		float v = bl ? this.maxU : this.minU;
		float w = bl ? this.minU : this.maxU;
		vertexConsumer.vertex(matrix4f, n + t, r, 0.0F).color(h, i, j, k).texture(v, this.minV).light(l).next();
		vertexConsumer.vertex(matrix4f, n + u, s, 0.0F).color(h, i, j, k).texture(v, this.maxV).light(l).next();
		vertexConsumer.vertex(matrix4f, o + u, s, 0.0F).color(h, i, j, k).texture(w, this.maxV).light(l).next();
		vertexConsumer.vertex(matrix4f, o + t, r, 0.0F).color(h, i, j, k).texture(w, this.minV).light(l).next();
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

	public RenderLayer getLayer(TextRenderer.TextLayerType layerType) {
		return switch (layerType) {
			case NORMAL -> this.textLayer;
			case SEE_THROUGH -> this.seeThroughTextLayer;
			case POLYGON_OFFSET -> this.polygonOffsetTextLayer;
		};
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
