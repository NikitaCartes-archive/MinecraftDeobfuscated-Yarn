package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class GlyphRenderer {
	private final RenderLayer field_21692;
	private final RenderLayer field_21693;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;
	private final float xMin;
	private final float xMax;
	private final float yMin;
	private final float yMax;

	public GlyphRenderer(RenderLayer renderLayer, RenderLayer renderLayer2, float f, float g, float h, float i, float j, float k, float l, float m) {
		this.field_21692 = renderLayer;
		this.field_21693 = renderLayer2;
		this.uMin = f;
		this.uMax = g;
		this.vMin = h;
		this.vMax = i;
		this.xMin = j;
		this.xMax = k;
		this.yMin = l;
		this.yMax = m;
	}

	public void draw(boolean italic, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light) {
		int i = 3;
		float f = x + this.xMin;
		float g = x + this.xMax;
		float h = this.yMin - 3.0F;
		float j = this.yMax - 3.0F;
		float k = y + h;
		float l = y + j;
		float m = italic ? 1.0F - 0.25F * h : 0.0F;
		float n = italic ? 1.0F - 0.25F * j : 0.0F;
		vertexConsumer.vertex(matrix, f + m, k, 0.0F).color(red, green, blue, alpha).texture(this.uMin, this.vMin).light(light).next();
		vertexConsumer.vertex(matrix, f + n, l, 0.0F).color(red, green, blue, alpha).texture(this.uMin, this.vMax).light(light).next();
		vertexConsumer.vertex(matrix, g + n, l, 0.0F).color(red, green, blue, alpha).texture(this.uMax, this.vMax).light(light).next();
		vertexConsumer.vertex(matrix, g + m, k, 0.0F).color(red, green, blue, alpha).texture(this.uMax, this.vMin).light(light).next();
	}

	public void drawRectangle(GlyphRenderer.Rectangle rectangle, Matrix4f matrix, VertexConsumer vertexConsumer, int light) {
		vertexConsumer.vertex(matrix, rectangle.xMin, rectangle.yMin, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMin, this.vMin)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.xMax, rectangle.yMin, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMin, this.vMax)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.xMax, rectangle.yMax, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMax, this.vMax)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.xMin, rectangle.yMax, rectangle.zIndex)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMax, this.vMin)
			.light(light)
			.next();
	}

	public RenderLayer method_24045(boolean bl) {
		return bl ? this.field_21693 : this.field_21692;
	}

	@Environment(EnvType.CLIENT)
	public static class Rectangle {
		protected final float xMin;
		protected final float yMin;
		protected final float xMax;
		protected final float yMax;
		protected final float zIndex;
		protected final float red;
		protected final float green;
		protected final float blue;
		protected final float alpha;

		public Rectangle(float xMin, float yMin, float xMax, float yMax, float zndex, float red, float green, float blue, float alpha) {
			this.xMin = xMin;
			this.yMin = yMin;
			this.xMax = xMax;
			this.yMax = yMax;
			this.zIndex = zndex;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
	}
}
