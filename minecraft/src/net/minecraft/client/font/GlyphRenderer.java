package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;
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

	public void draw(boolean bl, float f, float g, Matrix4f matrix4f, class_4588 arg, float h, float i, float j, float k, int l) {
		int m = 3;
		float n = f + this.xMin;
		float o = f + this.xMax;
		float p = this.yMin - 3.0F;
		float q = this.yMax - 3.0F;
		float r = g + p;
		float s = g + q;
		float t = bl ? 1.0F - 0.25F * p : 0.0F;
		float u = bl ? 1.0F - 0.25F * q : 0.0F;
		arg.method_22918(matrix4f, n + t, r, 0.0F).texture(this.uMin, this.vMin).method_22916(l).method_22915(h, i, j, k).next();
		arg.method_22918(matrix4f, n + u, s, 0.0F).texture(this.uMin, this.vMax).method_22916(l).method_22915(h, i, j, k).next();
		arg.method_22918(matrix4f, o + u, s, 0.0F).texture(this.uMax, this.vMax).method_22916(l).method_22915(h, i, j, k).next();
		arg.method_22918(matrix4f, o + t, r, 0.0F).texture(this.uMax, this.vMin).method_22916(l).method_22915(h, i, j, k).next();
	}

	public void method_22944(GlyphRenderer.Rectangle rectangle, Matrix4f matrix4f, class_4588 arg, int i) {
		arg.method_22918(matrix4f, rectangle.xMin, rectangle.yMin, rectangle.field_20911)
			.texture(this.uMin, this.vMin)
			.method_22916(i)
			.method_22915(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.next();
		arg.method_22918(matrix4f, rectangle.xMax, rectangle.yMin, rectangle.field_20911)
			.texture(this.uMin, this.vMax)
			.method_22916(i)
			.method_22915(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.next();
		arg.method_22918(matrix4f, rectangle.xMax, rectangle.yMax, rectangle.field_20911)
			.texture(this.uMax, this.vMax)
			.method_22916(i)
			.method_22915(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
			.next();
		arg.method_22918(matrix4f, rectangle.xMin, rectangle.yMax, rectangle.field_20911)
			.texture(this.uMax, this.vMin)
			.method_22916(i)
			.method_22915(rectangle.red, rectangle.green, rectangle.blue, rectangle.alpha)
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
