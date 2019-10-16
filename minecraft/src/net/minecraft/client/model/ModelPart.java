package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ModelPart {
	private float textureWidth = 64.0F;
	private float textureHeight = 32.0F;
	private int textureOffsetU;
	private int textureOffsetV;
	public float pivotX;
	public float pivotY;
	public float pivotZ;
	public float pitch;
	public float yaw;
	public float roll;
	public boolean mirror;
	public boolean visible = true;
	private final List<ModelPart.Cuboid> cuboids = Lists.<ModelPart.Cuboid>newArrayList();
	private final List<ModelPart> children = Lists.<ModelPart>newArrayList();

	public ModelPart(Model model) {
		model.method_22696(this);
		this.setTextureSize(model.textureWidth, model.textureHeight);
	}

	public ModelPart(Model model, int i, int j) {
		this(model.textureWidth, model.textureHeight, i, j);
		model.method_22696(this);
	}

	public ModelPart(int i, int j, int k, int l) {
		this.setTextureSize(i, j);
		this.setTextureOffset(k, l);
	}

	public void copyPositionAndRotation(ModelPart modelPart) {
		this.pitch = modelPart.pitch;
		this.yaw = modelPart.yaw;
		this.roll = modelPart.roll;
		this.pivotX = modelPart.pivotX;
		this.pivotY = modelPart.pivotY;
		this.pivotZ = modelPart.pivotZ;
	}

	public void addChild(ModelPart modelPart) {
		this.children.add(modelPart);
	}

	public ModelPart setTextureOffset(int i, int j) {
		this.textureOffsetU = i;
		this.textureOffsetV = j;
		return this;
	}

	public ModelPart addCuboid(String string, float f, float g, float h, int i, int j, int k, float l, int m, int n) {
		this.setTextureOffset(m, n);
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, (float)i, (float)j, (float)k, l, l, l, this.mirror, false);
		return this;
	}

	public ModelPart addCuboid(float f, float g, float h, float i, float j, float k) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F, 0.0F, 0.0F, this.mirror, false);
		return this;
	}

	public ModelPart addCuboid(float f, float g, float h, float i, float j, float k, boolean bl) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F, 0.0F, 0.0F, bl, false);
		return this;
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, this.mirror, false);
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l, float m, float n) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, m, n, this.mirror, false);
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l, boolean bl) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, bl, false);
	}

	private void addCuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, boolean bl2) {
		this.cuboids.add(new ModelPart.Cuboid(i, j, f, g, h, k, l, m, n, o, p, bl, this.textureWidth, this.textureHeight));
	}

	public void setPivot(float f, float g, float h) {
		this.pivotX = f;
		this.pivotY = g;
		this.pivotZ = h;
	}

	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, int i, int j, @Nullable Sprite sprite) {
		this.render(matrixStack, vertexConsumer, f, i, j, sprite, 1.0F, 1.0F, 1.0F);
	}

	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, int i, int j, @Nullable Sprite sprite, float g, float h, float k) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				matrixStack.push();
				this.rotate(matrixStack, f);
				this.renderCuboids(matrixStack.peek(), vertexConsumer, f, i, j, sprite, g, h, k);

				for (ModelPart modelPart : this.children) {
					modelPart.render(matrixStack, vertexConsumer, f, i, j, sprite, g, h, k);
				}

				matrixStack.pop();
			}
		}
	}

	public void rotate(MatrixStack matrixStack, float f) {
		matrixStack.translate((double)(this.pivotX * f), (double)(this.pivotY * f), (double)(this.pivotZ * f));
		if (this.roll != 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_Z.method_23626(this.roll));
		}

		if (this.yaw != 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_Y.method_23626(this.yaw));
		}

		if (this.pitch != 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_X.method_23626(this.pitch));
		}
	}

	private void renderCuboids(Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, int i, int j, @Nullable Sprite sprite, float g, float h, float k) {
		Matrix3f matrix3f = new Matrix3f(matrix4f);

		for (ModelPart.Cuboid cuboid : this.cuboids) {
			for (ModelPart.Quad quad : cuboid.sides) {
				Vector3f vector3f = new Vector3f(quad.vertices[1].pos.reverseSubtract(quad.vertices[0].pos));
				Vector3f vector3f2 = new Vector3f(quad.vertices[1].pos.reverseSubtract(quad.vertices[2].pos));
				vector3f.multiply(matrix3f);
				vector3f2.multiply(matrix3f);
				vector3f2.cross(vector3f);
				vector3f2.reciprocal();
				float l = vector3f2.getX();
				float m = vector3f2.getY();
				float n = vector3f2.getZ();

				for (int o = 0; o < 4; o++) {
					ModelPart.Vertex vertex = quad.vertices[o];
					Vector4f vector4f = new Vector4f((float)vertex.pos.x * f, (float)vertex.pos.y * f, (float)vertex.pos.z * f, 1.0F);
					vector4f.multiply(matrix4f);
					float p;
					float q;
					if (sprite == null) {
						p = vertex.u;
						q = vertex.v;
					} else {
						p = sprite.getU((double)(vertex.u * 16.0F));
						q = sprite.getV((double)(vertex.v * 16.0F));
					}

					vertexConsumer.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ())
						.color(g, h, k, 1.0F)
						.texture(p, q)
						.defaultOverlay(j)
						.light(i)
						.normal(l, m, n)
						.next();
				}
			}
		}
	}

	public ModelPart setTextureSize(int i, int j) {
		this.textureWidth = (float)i;
		this.textureHeight = (float)j;
		return this;
	}

	public ModelPart.Cuboid getRandomCuboid(Random random) {
		return (ModelPart.Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
	}

	@Environment(EnvType.CLIENT)
	public static class Cuboid {
		private final ModelPart.Quad[] sides;
		public final float minX;
		public final float minY;
		public final float minZ;
		public final float maxX;
		public final float maxY;
		public final float maxZ;

		public Cuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, float q, float r) {
			this.minX = f;
			this.minY = g;
			this.minZ = h;
			this.maxX = f + k;
			this.maxY = g + l;
			this.maxZ = h + m;
			this.sides = new ModelPart.Quad[6];
			float s = f + k;
			float t = g + l;
			float u = h + m;
			f -= n;
			g -= o;
			h -= p;
			s += n;
			t += o;
			u += p;
			if (bl) {
				float v = s;
				s = f;
				f = v;
			}

			ModelPart.Vertex vertex = new ModelPart.Vertex(f, g, h, 0.0F, 0.0F);
			ModelPart.Vertex vertex2 = new ModelPart.Vertex(s, g, h, 0.0F, 8.0F);
			ModelPart.Vertex vertex3 = new ModelPart.Vertex(s, t, h, 8.0F, 8.0F);
			ModelPart.Vertex vertex4 = new ModelPart.Vertex(f, t, h, 8.0F, 0.0F);
			ModelPart.Vertex vertex5 = new ModelPart.Vertex(f, g, u, 0.0F, 0.0F);
			ModelPart.Vertex vertex6 = new ModelPart.Vertex(s, g, u, 0.0F, 8.0F);
			ModelPart.Vertex vertex7 = new ModelPart.Vertex(s, t, u, 8.0F, 8.0F);
			ModelPart.Vertex vertex8 = new ModelPart.Vertex(f, t, u, 8.0F, 0.0F);
			float w = (float)i;
			float x = (float)i + m;
			float y = (float)i + m + k;
			float z = (float)i + m + k + k;
			float aa = (float)i + m + k + m;
			float ab = (float)i + m + k + m + k;
			float ac = (float)j;
			float ad = (float)j + m;
			float ae = (float)j + m + l;
			this.sides[2] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, x, ac, y, ad, q, r);
			this.sides[3] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, y, ad, z, ac, q, r);
			this.sides[1] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, w, ad, x, ae, q, r);
			this.sides[4] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, x, ad, y, ae, q, r);
			this.sides[0] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, y, ad, aa, ae, q, r);
			this.sides[5] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, aa, ad, ab, ae, q, r);
			if (bl) {
				for (ModelPart.Quad quad : this.sides) {
					quad.flip();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Quad {
		public ModelPart.Vertex[] vertices;

		public Quad(ModelPart.Vertex[] vertexs) {
			this.vertices = vertexs;
		}

		public Quad(ModelPart.Vertex[] vertexs, float f, float g, float h, float i, float j, float k) {
			this(vertexs);
			float l = 0.0F / j;
			float m = 0.0F / k;
			vertexs[0] = vertexs[0].remap(h / j - l, g / k + m);
			vertexs[1] = vertexs[1].remap(f / j + l, g / k + m);
			vertexs[2] = vertexs[2].remap(f / j + l, i / k - m);
			vertexs[3] = vertexs[3].remap(h / j - l, i / k - m);
		}

		public void flip() {
			ModelPart.Vertex[] vertexs = new ModelPart.Vertex[this.vertices.length];

			for (int i = 0; i < this.vertices.length; i++) {
				vertexs[i] = this.vertices[this.vertices.length - i - 1];
			}

			this.vertices = vertexs;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Vertex {
		public final Vec3d pos;
		public final float u;
		public final float v;

		public Vertex(float f, float g, float h, float i, float j) {
			this(new Vec3d((double)f, (double)g, (double)h), i, j);
		}

		public ModelPart.Vertex remap(float f, float g) {
			return new ModelPart.Vertex(this.pos, f, g);
		}

		public Vertex(Vec3d vec3d, float f, float g) {
			this.pos = vec3d;
			this.u = f;
			this.v = g;
		}
	}
}
