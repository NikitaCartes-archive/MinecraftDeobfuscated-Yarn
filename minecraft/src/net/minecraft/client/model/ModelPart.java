package net.minecraft.client.model;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public final class ModelPart {
	public static final float field_37937 = 1.0F;
	public float pivotX;
	public float pivotY;
	public float pivotZ;
	public float pitch;
	public float yaw;
	public float roll;
	public float xScale = 1.0F;
	public float yScale = 1.0F;
	public float zScale = 1.0F;
	public boolean visible = true;
	public boolean hidden;
	private final List<ModelPart.Cuboid> cuboids;
	private final Map<String, ModelPart> children;
	private ModelTransform defaultTransform = ModelTransform.NONE;

	public ModelPart(List<ModelPart.Cuboid> cuboids, Map<String, ModelPart> children) {
		this.cuboids = cuboids;
		this.children = children;
	}

	public ModelTransform getTransform() {
		return ModelTransform.of(this.pivotX, this.pivotY, this.pivotZ, this.pitch, this.yaw, this.roll);
	}

	public ModelTransform getDefaultTransform() {
		return this.defaultTransform;
	}

	public void setDefaultTransform(ModelTransform transform) {
		this.defaultTransform = transform;
	}

	public void resetTransform() {
		this.setTransform(this.defaultTransform);
	}

	public void setTransform(ModelTransform rotationData) {
		this.pivotX = rotationData.pivotX;
		this.pivotY = rotationData.pivotY;
		this.pivotZ = rotationData.pivotZ;
		this.pitch = rotationData.pitch;
		this.yaw = rotationData.yaw;
		this.roll = rotationData.roll;
		this.xScale = 1.0F;
		this.yScale = 1.0F;
		this.zScale = 1.0F;
	}

	public void copyTransform(ModelPart part) {
		this.xScale = part.xScale;
		this.yScale = part.yScale;
		this.zScale = part.zScale;
		this.pitch = part.pitch;
		this.yaw = part.yaw;
		this.roll = part.roll;
		this.pivotX = part.pivotX;
		this.pivotY = part.pivotY;
		this.pivotZ = part.pivotZ;
	}

	public boolean hasChild(String child) {
		return this.children.containsKey(child);
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
				if (!this.hidden) {
					this.renderCuboids(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);
				}

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
		matrices.translate(this.pivotX / 16.0F, this.pivotY / 16.0F, this.pivotZ / 16.0F);
		if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
			matrices.multiply(new Quaternionf().rotationZYX(this.roll, this.yaw, this.pitch));
		}

		if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
			matrices.scale(this.xScale, this.yScale, this.zScale);
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

	public void translate(Vector3f vec3f) {
		this.pivotX = this.pivotX + vec3f.x();
		this.pivotY = this.pivotY + vec3f.y();
		this.pivotZ = this.pivotZ + vec3f.z();
	}

	public void rotate(Vector3f vec3f) {
		this.pitch = this.pitch + vec3f.x();
		this.yaw = this.yaw + vec3f.y();
		this.roll = this.roll + vec3f.z();
	}

	public void scale(Vector3f vec3f) {
		this.xScale = this.xScale + vec3f.x();
		this.yScale = this.yScale + vec3f.y();
		this.zScale = this.zScale + vec3f.z();
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
			float textureHeight,
			Set<Direction> set
		) {
			this.minX = x;
			this.minY = y;
			this.minZ = z;
			this.maxX = x + sizeX;
			this.maxY = y + sizeY;
			this.maxZ = z + sizeZ;
			this.sides = new ModelPart.Quad[set.size()];
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
			int s = 0;
			if (set.contains(Direction.DOWN)) {
				this.sides[s++] = new ModelPart.Quad(
					new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror, Direction.DOWN
				);
			}

			if (set.contains(Direction.UP)) {
				this.sides[s++] = new ModelPart.Quad(
					new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror, Direction.UP
				);
			}

			if (set.contains(Direction.WEST)) {
				this.sides[s++] = new ModelPart.Quad(
					new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror, Direction.WEST
				);
			}

			if (set.contains(Direction.NORTH)) {
				this.sides[s++] = new ModelPart.Quad(
					new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror, Direction.NORTH
				);
			}

			if (set.contains(Direction.EAST)) {
				this.sides[s++] = new ModelPart.Quad(
					new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST
				);
			}

			if (set.contains(Direction.SOUTH)) {
				this.sides[s] = new ModelPart.Quad(
					new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror, Direction.SOUTH
				);
			}
		}

		public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
			Matrix4f matrix4f = entry.getPositionMatrix();
			Matrix3f matrix3f = entry.getNormalMatrix();

			for (ModelPart.Quad quad : this.sides) {
				Vector3f vector3f = matrix3f.transform(new Vector3f(quad.direction));
				float f = vector3f.x();
				float g = vector3f.y();
				float h = vector3f.z();

				for (ModelPart.Vertex vertex : quad.vertices) {
					float i = vertex.pos.x() / 16.0F;
					float j = vertex.pos.y() / 16.0F;
					float k = vertex.pos.z() / 16.0F;
					Vector4f vector4f = matrix4f.transform(new Vector4f(i, j, k, 1.0F));
					vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
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
		public final Vector3f direction;

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
				this.direction.mul(-1.0F, 1.0F, 1.0F);
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

		public Vertex(Vector3f pos, float u, float v) {
			this.pos = pos;
			this.u = u;
			this.v = v;
		}
	}
}
