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

	public ModelPart(Model model, int textureOffsetU, int textureOffsetV) {
		this(model.textureWidth, model.textureHeight, textureOffsetU, textureOffsetV);
		model.method_22696(this);
	}

	public ModelPart(int textureWidth, int textureHeight, int textureOffsetU, int textureOffsetV) {
		this.setTextureSize(textureWidth, textureHeight);
		this.setTextureOffset(textureOffsetU, textureOffsetV);
	}

	public void copyPositionAndRotation(ModelPart modelPart) {
		this.pitch = modelPart.pitch;
		this.yaw = modelPart.yaw;
		this.roll = modelPart.roll;
		this.pivotX = modelPart.pivotX;
		this.pivotY = modelPart.pivotY;
		this.pivotZ = modelPart.pivotZ;
	}

	public void addChild(ModelPart part) {
		this.children.add(part);
	}

	public ModelPart setTextureOffset(int textureOffsetU, int textureOffsetV) {
		this.textureOffsetU = textureOffsetU;
		this.textureOffsetV = textureOffsetV;
		return this;
	}

	public ModelPart addCuboid(String name, float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra, int textureOffsetU, int textureOffsetV) {
		this.setTextureOffset(textureOffsetU, textureOffsetV);
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, (float)sizeX, (float)sizeY, (float)sizeZ, extra, extra, extra, this.mirror, false);
		return this;
	}

	public ModelPart addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, 0.0F, 0.0F, this.mirror, false);
		return this;
	}

	public ModelPart addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, boolean mirror) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, 0.0F, 0.0F, mirror, false);
		return this;
	}

	public void addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extra) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, extra, extra, this.mirror, false);
	}

	public void addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, this.mirror, false);
	}

	public void addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extra, boolean mirror) {
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, extra, extra, mirror, false);
	}

	private void addCuboid(
		int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, boolean bl
	) {
		this.cuboids.add(new ModelPart.Cuboid(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, this.textureWidth, this.textureHeight));
	}

	public void setPivot(float x, float y, float z) {
		this.pivotX = x;
		this.pivotY = y;
		this.pivotZ = z;
	}

	public void render(MatrixStack matrix, VertexConsumer vertexConsumer, float pivotDistance, int light, int i, @Nullable Sprite sprite) {
		this.render(matrix, vertexConsumer, pivotDistance, light, i, sprite, 1.0F, 1.0F, 1.0F);
	}

	public void render(
		MatrixStack matrix, VertexConsumer vertexConsumer, float pivotDistance, int light, int overlay, @Nullable Sprite sprite, float red, float green, float blue
	) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				matrix.push();
				this.rotate(matrix, pivotDistance);
				this.renderCuboids(matrix.peekModel(), vertexConsumer, pivotDistance, light, overlay, sprite, red, green, blue);

				for (ModelPart modelPart : this.children) {
					modelPart.render(matrix, vertexConsumer, pivotDistance, light, overlay, sprite, red, green, blue);
				}

				matrix.pop();
			}
		}
	}

	public void rotate(MatrixStack matrix, float pivotDistance) {
		matrix.translate((double)(this.pivotX * pivotDistance), (double)(this.pivotY * pivotDistance), (double)(this.pivotZ * pivotDistance));
		if (this.roll != 0.0F) {
			matrix.multiply(Vector3f.POSITIVE_Z.method_23626(this.roll));
		}

		if (this.yaw != 0.0F) {
			matrix.multiply(Vector3f.POSITIVE_Y.method_23626(this.yaw));
		}

		if (this.pitch != 0.0F) {
			matrix.multiply(Vector3f.POSITIVE_X.method_23626(this.pitch));
		}
	}

	private void renderCuboids(
		Matrix4f matrix, VertexConsumer vertexConsumer, float pivotDistance, int light, int overlay, @Nullable Sprite sprite, float red, float green, float blue
	) {
		Matrix3f matrix3f = new Matrix3f(matrix);

		for (ModelPart.Cuboid cuboid : this.cuboids) {
			for (ModelPart.Quad quad : cuboid.sides) {
				Vector3f vector3f = new Vector3f(quad.vertices[1].pos.reverseSubtract(quad.vertices[0].pos));
				Vector3f vector3f2 = new Vector3f(quad.vertices[1].pos.reverseSubtract(quad.vertices[2].pos));
				vector3f.multiply(matrix3f);
				vector3f2.multiply(matrix3f);
				vector3f2.cross(vector3f);
				vector3f2.reciprocal();
				float f = vector3f2.getX();
				float g = vector3f2.getY();
				float h = vector3f2.getZ();

				for (int i = 0; i < 4; i++) {
					ModelPart.Vertex vertex = quad.vertices[i];
					Vector4f vector4f = new Vector4f((float)vertex.pos.x * pivotDistance, (float)vertex.pos.y * pivotDistance, (float)vertex.pos.z * pivotDistance, 1.0F);
					vector4f.multiply(matrix);
					float j;
					float k;
					if (sprite == null) {
						j = vertex.u;
						k = vertex.v;
					} else {
						j = sprite.getU((double)(vertex.u * 16.0F));
						k = sprite.getV((double)(vertex.v * 16.0F));
					}

					vertexConsumer.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ())
						.color(red, green, blue, 1.0F)
						.texture(j, k)
						.overlay(overlay)
						.light(light)
						.normal(f, g, h)
						.next();
				}
			}
		}
	}

	public ModelPart setTextureSize(int width, int height) {
		this.textureWidth = (float)width;
		this.textureHeight = (float)height;
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

		public Cuboid(
			int u,
			int v,
			float x,
			float y,
			float z,
			float sizeX,
			float sizeY,
			float sizeZ,
			float extraX,
			float extraY,
			float extraZ,
			boolean mirror,
			float textureWidth,
			float textureHeight
		) {
			this.minX = x;
			this.minY = y;
			this.minZ = z;
			this.maxX = x + sizeX;
			this.maxY = y + sizeY;
			this.maxZ = z + sizeZ;
			this.sides = new ModelPart.Quad[6];
			float f = x + sizeX;
			float g = y + sizeY;
			float h = z + sizeZ;
			x -= extraX;
			y -= extraY;
			z -= extraZ;
			f += extraX;
			g += extraY;
			h += extraZ;
			if (mirror) {
				float i = f;
				f = x;
				x = i;
			}

			ModelPart.Vertex vertex = new ModelPart.Vertex(x, y, z, 0.0F, 0.0F);
			ModelPart.Vertex vertex2 = new ModelPart.Vertex(f, y, z, 0.0F, 8.0F);
			ModelPart.Vertex vertex3 = new ModelPart.Vertex(f, g, z, 8.0F, 8.0F);
			ModelPart.Vertex vertex4 = new ModelPart.Vertex(x, g, z, 8.0F, 0.0F);
			ModelPart.Vertex vertex5 = new ModelPart.Vertex(x, y, h, 0.0F, 0.0F);
			ModelPart.Vertex vertex6 = new ModelPart.Vertex(f, y, h, 0.0F, 8.0F);
			ModelPart.Vertex vertex7 = new ModelPart.Vertex(f, g, h, 8.0F, 8.0F);
			ModelPart.Vertex vertex8 = new ModelPart.Vertex(x, g, h, 8.0F, 0.0F);
			float j = (float)u;
			float k = (float)u + sizeZ;
			float l = (float)u + sizeZ + sizeX;
			float m = (float)u + sizeZ + sizeX + sizeX;
			float n = (float)u + sizeZ + sizeX + sizeZ;
			float o = (float)u + sizeZ + sizeX + sizeZ + sizeX;
			float p = (float)v;
			float q = (float)v + sizeZ;
			float r = (float)v + sizeZ + sizeY;
			this.sides[2] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight);
			this.sides[3] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight);
			this.sides[1] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight);
			this.sides[4] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight);
			this.sides[0] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight);
			this.sides[5] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight);
			if (mirror) {
				for (ModelPart.Quad quad : this.sides) {
					quad.flip();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Quad {
		public ModelPart.Vertex[] vertices;

		public Quad(ModelPart.Vertex[] vertices) {
			this.vertices = vertices;
		}

		public Quad(ModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV) {
			this(vertices);
			float f = 0.0F / squishU;
			float g = 0.0F / squishV;
			vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
			vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
			vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
			vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
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

		public Vertex(float x, float y, float z, float u, float v) {
			this(new Vec3d((double)x, (double)y, (double)z), u, v);
		}

		public ModelPart.Vertex remap(float u, float v) {
			return new ModelPart.Vertex(this.pos, u, v);
		}

		public Vertex(Vec3d pos, float u, float v) {
			this.pos = pos;
			this.u = u;
			this.v = v;
		}
	}
}
