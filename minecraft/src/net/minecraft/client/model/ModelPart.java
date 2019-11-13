package net.minecraft.client.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
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
import net.minecraft.util.math.Direction;

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
	private final ObjectList<ModelPart.Cuboid> cuboids = new ObjectArrayList<>();
	private final ObjectList<ModelPart> children = new ObjectArrayList<>();

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

	public void render(MatrixStack matrix, VertexConsumer vertexConsumer, int i, int j, @Nullable Sprite sprite) {
		this.render(matrix, vertexConsumer, i, j, sprite, 1.0F, 1.0F, 1.0F);
	}

	public void render(MatrixStack matrix, VertexConsumer vertexConsumer, int i, int j, @Nullable Sprite sprite, float f, float g, float h) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				matrix.push();
				this.rotate(matrix);
				this.renderCuboids(matrix.peek(), vertexConsumer, i, j, sprite, f, g, h);

				for (ModelPart modelPart : this.children) {
					modelPart.render(matrix, vertexConsumer, i, j, sprite, f, g, h);
				}

				matrix.pop();
			}
		}
	}

	public void rotate(MatrixStack matrix) {
		matrix.translate((double)(this.pivotX / 16.0F), (double)(this.pivotY / 16.0F), (double)(this.pivotZ / 16.0F));
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

	private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int i, int j, @Nullable Sprite sprite, float f, float g, float h) {
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();

		for (ModelPart.Cuboid cuboid : this.cuboids) {
			for (ModelPart.Quad quad : cuboid.sides) {
				Vector3f vector3f = quad.field_21618.method_23850();
				vector3f.multiply(matrix3f);
				float k = vector3f.getX();
				float l = vector3f.getY();
				float m = vector3f.getZ();

				for (int n = 0; n < 4; n++) {
					ModelPart.Vertex vertex = quad.vertices[n];
					float o = vertex.pos.getX() / 16.0F;
					float p = vertex.pos.getY() / 16.0F;
					float q = vertex.pos.getZ() / 16.0F;
					Vector4f vector4f = new Vector4f(o, p, q, 1.0F);
					vector4f.multiply(matrix4f);
					float r;
					float s;
					if (sprite == null) {
						r = vertex.u;
						s = vertex.v;
					} else {
						r = sprite.getU((double)(vertex.u * 16.0F));
						s = sprite.getV((double)(vertex.v * 16.0F));
					}

					vertexConsumer.method_23919(vector4f.getX(), vector4f.getY(), vector4f.getZ(), f, g, h, 1.0F, r, s, j, i, k, l, m);
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
			this.sides[2] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror, Direction.DOWN
			);
			this.sides[3] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror, Direction.UP);
			this.sides[1] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror, Direction.WEST
			);
			this.sides[4] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror, Direction.NORTH
			);
			this.sides[0] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST
			);
			this.sides[5] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror, Direction.SOUTH
			);
		}
	}

	@Environment(EnvType.CLIENT)
	static class Quad {
		public final ModelPart.Vertex[] vertices;
		public final Vector3f field_21618;

		public Quad(ModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean bl, Direction direction) {
			this.vertices = vertices;
			float f = 0.0F / squishU;
			float g = 0.0F / squishV;
			vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
			vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
			vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
			vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
			if (bl) {
				int i = vertices.length;

				for (int j = 0; j < i / 2; j++) {
					ModelPart.Vertex vertex = vertices[j];
					vertices[j] = vertices[i - 1 - j];
					vertices[i - 1 - j] = vertex;
				}
			}

			this.field_21618 = direction.method_23955();
			if (bl) {
				this.field_21618.method_23849(-1.0F, 1.0F, 1.0F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Vertex {
		public final Vector3f pos;
		public final float u;
		public final float v;

		public Vertex(float x, float y, float z, float u, float v) {
			this(new Vector3f(x, y, z), u, v);
		}

		public ModelPart.Vertex remap(float u, float v) {
			return new ModelPart.Vertex(this.pos, u, v);
		}

		public Vertex(Vector3f vector3f, float u, float v) {
			this.pos = vector3f;
			this.u = u;
			this.v = v;
		}
	}
}
