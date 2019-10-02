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

	public GlyphRenderer(Identifier identifier, float f, float g, float h, float i, float j, float k, float l, float m) {
		this.id = identifier;
		this.uMin = f;
		this.uMax = g;
		this.vMin = h;
		this.vMax = i;
		this.xMin = j;
		this.xMax = k;
		this.yMin = l;
		this.yMax = m;
	}

	public void draw(boolean bl, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer, float h, float i, float j, float k, int l) {
		int m = 3;
		float n = f + this.xMin;
		float o = f + this.xMax;
		float p = this.yMin - 3.0F;
		float q = this.yMax - 3.0F;
		float r = g + p;
		float s = g + q;
		float t = bl ? 1.0F - 0.25F * p : 0.0F;
		float u = bl ? 1.0F - 0.25F * q : 0.0F;
		vertexConsumer.vertex(matrix4f, n + t, r, 0.0F).color(h, i, j, k).texture(this.uMin, this.vMin).light(l).next();
		vertexConsumer.vertex(matrix4f, n + u, s, 0.0F).color(h, i, j, k).texture(this.uMin, this.vMax).light(l).next();
		vertexConsumer.vertex(matrix4f, o + u, s, 0.0F).color(h, i, j, k).texture(this.uMax, this.vMax).light(l).next();
		vertexConsumer.vertex(matrix4f, o + t, r, 0.0F).color(h, i, j, k).texture(this.uMax, this.vMin).light(l).next();
	}

	public void method_22944(GlyphRenderer.Rectangle rectangle, Matrix4f matrix4f, VertexConsumer vertexConsumer, int i) {
		vertexConsumer.vertex(matrix4f, rectangle.xMin, rectangle.yMin, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMin, this.vMin)
			.light(i)
			.next();
		vertexConsumer.vertex(matrix4f, rectangle.xMax, rectangle.yMin, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMin, this.vMax)
			.light(i)
			.next();
		vertexConsumer.vertex(matrix4f, rectangle.xMax, rectangle.yMax, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMax, this.vMax)
			.light(i)
			.next();
		vertexConsumer.vertex(matrix4f, rectangle.xMin, rectangle.yMax, rectangle.field_20911)
			.color(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.texture(this.uMax, this.vMin)
			.light(i)
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
