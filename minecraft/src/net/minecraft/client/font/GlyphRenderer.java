package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GlyphRenderer {
	private final Identifier id;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;
	private final float xMin;
	private final float xMax;
	private final float yMin;
	private final float yMax;

	public GlyphRenderer(Identifier identifier, float uMin, float uMax, float vMin, float vMax, float xMin, float xMax, float yMin, float yMax) {
		this.id = identifier;
		this.uMin = uMin;
		this.uMax = uMax;
		this.vMin = vMin;
		this.vMax = vMax;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
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
		vertexConsumer.vertex(matrix, rectangle.xMin, rectangle.yMin, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMin, this.vMin)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.xMax, rectangle.yMin, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMin, this.vMax)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.xMax, rectangle.yMax, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMax, this.vMax)
			.light(light)
			.next();
		vertexConsumer.vertex(matrix, rectangle.xMin, rectangle.yMax, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMax, this.vMin)
			.light(light)
			.next();
	}

	@Nullable
	public Identifier getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public static class Rectangle {
		protected final float xMin;
		protected final float yMin;
		protected final float xMax;
		protected final float yMax;
		protected final float field_20911;
		protected final float red;
		protected final float green;
		protected final float blue;
		protected final float alpha;

		public Rectangle(float f, float g, float h, float i, float j, float k, float l, float m, float n) {
			this.xMin = f;
			this.yMin = g;
			this.xMax = h;
			this.yMax = i;
			this.field_20911 = j;
			this.red = k;
			this.green = l;
			this.blue = m;
			this.alpha = n;
		}
	}
}
