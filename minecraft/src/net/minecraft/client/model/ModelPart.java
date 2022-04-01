package net.minecraft.client.model;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

@Environment(EnvType.CLIENT)
public final class ModelPart {
	public static final float field_38657 = 1.0F;
	public float pivotX;
	public float pivotY;
	public float pivotZ;
	public float pitch;
	public float yaw;
	public float roll;
	public float field_38658 = 1.0F;
	public float field_38659 = 1.0F;
	public float field_38660 = 1.0F;
	public boolean visible = true;
	private final List<ModelPart.Cuboid> cuboids;
	private final Map<String, ModelPart> children;
	private ModelTransform field_38661 = ModelTransform.NONE;

	public ModelPart(List<ModelPart.Cuboid> list, Map<String, ModelPart> children) {
		this.cuboids = list;
		this.children = children;
	}

	public ModelTransform getTransform() {
		return ModelTransform.of(this.pivotX, this.pivotY, this.pivotZ, this.pitch, this.yaw, this.roll);
	}

	public ModelTransform method_42990() {
		return this.field_38661;
	}

	public void method_42987(ModelTransform modelTransform) {
		this.field_38661 = modelTransform;
	}

	public void method_42992() {
		this.setTransform(this.field_38661);
	}

	public void setTransform(ModelTransform rotationData) {
		this.pivotX = rotationData.pivotX;
		this.pivotY = rotationData.pivotY;
		this.pivotZ = rotationData.pivotZ;
		this.pitch = rotationData.pitch;
		this.yaw = rotationData.yaw;
		this.roll = rotationData.roll;
		this.field_38658 = 1.0F;
		this.field_38659 = 1.0F;
		this.field_38660 = 1.0F;
	}

	public void copyTransform(ModelPart part) {
		this.field_38658 = part.field_38658;
		this.field_38659 = part.field_38659;
		this.field_38660 = part.field_38660;
		this.pitch = part.pitch;
		this.yaw = part.yaw;
		this.roll = part.roll;
		this.pivotX = part.pivotX;
		this.pivotY = part.pivotY;
		this.pivotZ = part.pivotZ;
		this.visible = part.visible;
	}

	public boolean method_42988(String string) {
		return this.children.containsKey(string);
	}

	public ModelPart getChild(String name) {
		ModelPart modelPart = (ModelPart)this.children.get(name);
		if (modelPart == null) {
			throw new NoSuchElementException("Can't find part " + name);
		} else {
			return modelPart;
		}
	}

	public void setPivot(float x, float y, float z) {
		this.pivotX = x;
		this.pivotY = y;
		this.pivotZ = z;
	}

	public void setAngles(float pitch, float yaw, float roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.render(matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				matrices.push();
				this.rotate(matrices);
				this.renderCuboids(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);

				for (ModelPart modelPart : this.children.values()) {
					modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
				}

				matrices.pop();
			}
		}
	}

	public void forEachCuboid(MatrixStack matrices, ModelPart.CuboidConsumer consumer) {
		this.forEachCuboid(matrices, consumer, "");
	}

	private void forEachCuboid(MatrixStack matrices, ModelPart.CuboidConsumer consumer, String path) {
		if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
			matrices.push();
			this.rotate(matrices);
			MatrixStack.Entry entry = matrices.peek();

			for (int i = 0; i < this.cuboids.size(); i++) {
				consumer.accept(entry, path, i, (ModelPart.Cuboid)this.cuboids.get(i));
			}

			String string = path + "/";
			this.children.forEach((name, part) -> part.forEachCuboid(matrices, consumer, string + name));
			matrices.pop();
		}
	}

	public void rotate(MatrixStack matrices) {
		matrices.translate((double)(this.pivotX / 16.0F), (double)(this.pivotY / 16.0F), (double)(this.pivotZ / 16.0F));
		if (this.roll != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(this.roll));
		}

		if (this.yaw != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(this.yaw));
		}

		if (this.pitch != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(this.pitch));
		}

		if (this.field_38658 != 1.0F || this.field_38659 != 1.0F || this.field_38660 != 1.0F) {
			matrices.scale(this.field_38658, this.field_38659, this.field_38660);
		}
	}

	private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		for (ModelPart.Cuboid cuboid : this.cuboids) {
			cuboid.renderCuboid(entry, vertexConsumer, light, overlay, red, green, blue, alpha);
		}
	}

	public ModelPart.Cuboid getRandomCuboid(Random random) {
		return (ModelPart.Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
	}

	public boolean isEmpty() {
		return this.cuboids.isEmpty();
	}

	public void method_42989(Vec3f vec3f) {
		this.pivotX = this.pivotX + vec3f.getX();
		this.pivotY = this.pivotY + vec3f.getY();
		this.pivotZ = this.pivotZ + vec3f.getZ();
	}

	public void method_42991(Vec3f vec3f) {
		this.pitch = this.pitch + vec3f.getX();
		this.yaw = this.yaw + vec3f.getY();
		this.roll = this.roll + vec3f.getZ();
	}

	public void method_42993(Vec3f vec3f) {
		this.field_38658 = this.field_38658 + vec3f.getX();
		this.field_38659 = this.field_38659 + vec3f.getY();
		this.field_38660 = this.field_38660 + vec3f.getZ();
	}

	public Stream<ModelPart> traverse() {
		return Stream.concat(Stream.of(this), this.children.values().stream().flatMap(ModelPart::traverse));
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

		public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
			Matrix4f matrix4f = entry.getPositionMatrix();
			Matrix3f matrix3f = entry.getNormalMatrix();

			for (ModelPart.Quad quad : this.sides) {
				Vec3f vec3f = quad.direction.copy();
				vec3f.transform(matrix3f);
				float f = vec3f.getX();
				float g = vec3f.getY();
				float h = vec3f.getZ();

				for (ModelPart.Vertex vertex : quad.vertices) {
					float i = vertex.pos.getX() / 16.0F;
					float j = vertex.pos.getY() / 16.0F;
					float k = vertex.pos.getZ() / 16.0F;
					Vector4f vector4f = new Vector4f(i, j, k, 1.0F);
					vector4f.transform(matrix4f);
					vertexConsumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
				}
			}
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface CuboidConsumer {
		/**
		 * Accepts a cuboid from a model part.
		 * 
		 * @see ModelPart#forEachCuboid(MatrixStack, CuboidConsumer)
		 * 
		 * @param matrix the current matrix transformation from the model parts
		 * @param path the path of the current model part, separated by {@code /}
		 * @param index the index of the current cuboid in the current model part
		 * @param cuboid the current cuboid
		 */
		void accept(MatrixStack.Entry matrix, String path, int index, ModelPart.Cuboid cuboid);
	}

	@Environment(EnvType.CLIENT)
	static class Quad {
		public final ModelPart.Vertex[] vertices;
		public final Vec3f direction;

		public Quad(ModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction) {
			this.vertices = vertices;
			float f = 0.0F / squishU;
			float g = 0.0F / squishV;
			vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
			vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
			vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
			vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
			if (flip) {
				int i = vertices.length;

				for (int j = 0; j < i / 2; j++) {
					ModelPart.Vertex vertex = vertices[j];
					vertices[j] = vertices[i - 1 - j];
					vertices[i - 1 - j] = vertex;
				}
			}

			this.direction = direction.getUnitVector();
			if (flip) {
				this.direction.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Vertex {
		public final Vec3f pos;
		public final float u;
		public final float v;

		public Vertex(float x, float y, float z, float u, float v) {
			this(new Vec3f(x, y, z), u, v);
		}

		public ModelPart.Vertex remap(float u, float v) {
			return new ModelPart.Vertex(this.pos, u, v);
		}

		public Vertex(Vec3f pos, float u, float v) {
			this.pos = pos;
			this.u = u;
			this.v = v;
		}
	}
}
