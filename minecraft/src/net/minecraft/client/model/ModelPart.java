package net.minecraft.client.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ModelPart {
	private static final BufferBuilder field_20790 = new BufferBuilder(256);
	private float textureWidth = 64.0F;
	private float textureHeight = 32.0F;
	private int textureOffsetU;
	private int textureOffsetV;
	public float rotationPointX;
	public float rotationPointY;
	public float rotationPointZ;
	public float pitch;
	public float yaw;
	public float roll;
	@Nullable
	private ByteBuffer compiled;
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

	public void copyRotation(ModelPart modelPart) {
		this.pitch = modelPart.pitch;
		this.yaw = modelPart.yaw;
		this.roll = modelPart.roll;
		this.rotationPointX = modelPart.rotationPointX;
		this.rotationPointY = modelPart.rotationPointY;
		this.rotationPointZ = modelPart.rotationPointZ;
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
		this.cuboids
			.add(
				new ModelPart.Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, (float)i, (float)j, (float)k, l, this.mirror, this.textureWidth, this.textureHeight)
			);
		return this;
	}

	public ModelPart addCuboid(float f, float g, float h, float i, float j, float k) {
		this.cuboids.add(new ModelPart.Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F, this.mirror, this.textureWidth, this.textureHeight));
		return this;
	}

	public ModelPart addCuboid(float f, float g, float h, float i, float j, float k, boolean bl) {
		this.cuboids.add(new ModelPart.Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F, bl, this.textureWidth, this.textureHeight));
		return this;
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l) {
		this.cuboids.add(new ModelPart.Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, this.mirror, this.textureWidth, this.textureHeight));
	}

	public void addCuboid(float f, float g, float h, float i, float j, float k, float l, boolean bl) {
		this.cuboids.add(new ModelPart.Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, bl, this.textureWidth, this.textureHeight));
	}

	public void setRotationPoint(float f, float g, float h) {
		this.rotationPointX = f;
		this.rotationPointY = g;
		this.rotationPointZ = h;
	}

	public void render(float f) {
		if (this.visible) {
			this.compile(f);
			if (this.compiled != null) {
				RenderSystem.pushMatrix();
				this.method_22703(f);
				this.compiled.clear();
				int i = this.compiled.remaining() / VertexFormats.POSITION_UV_NORMAL_2.getVertexSize();
				BufferRenderer.method_22637(this.compiled, 7, VertexFormats.POSITION_UV_NORMAL_2, i);

				for (ModelPart modelPart : this.children) {
					modelPart.render(f);
				}

				RenderSystem.popMatrix();
			}
		}
	}

	public void method_22698(BufferBuilder bufferBuilder, float f, int i, int j, Sprite sprite) {
		this.method_22699(bufferBuilder, f, i, j, sprite, 1.0F, 1.0F, 1.0F);
	}

	public void method_22699(BufferBuilder bufferBuilder, float f, int i, int j, Sprite sprite, float g, float h, float k) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				bufferBuilder.method_22629();
				bufferBuilder.method_22626((double)(this.rotationPointX * f), (double)(this.rotationPointY * f), (double)(this.rotationPointZ * f));
				if (this.roll != 0.0F) {
					bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, this.roll, false));
				}

				if (this.yaw != 0.0F) {
					bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, this.yaw, false));
				}

				if (this.pitch != 0.0F) {
					bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, this.pitch, false));
				}

				this.method_22702(bufferBuilder, f, i, j, sprite, g, h, k);

				for (ModelPart modelPart : this.children) {
					modelPart.method_22698(bufferBuilder, f, i, j, sprite);
				}

				bufferBuilder.method_22630();
			}
		}
	}

	private void compile(float f) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				if (this.compiled == null) {
					field_20790.begin(7, VertexFormats.POSITION_UV_NORMAL_2);
					this.method_22701(field_20790, f, 240, 240, null);
					field_20790.end();
					Pair<BufferBuilder.class_4574, ByteBuffer> pair = field_20790.method_22632();
					ByteBuffer byteBuffer = pair.getSecond();
					this.compiled = GlAllocationUtils.allocateByteBuffer(byteBuffer.remaining());
					this.compiled.put(byteBuffer);
				}
			}
		}
	}

	public void applyTransform(float f) {
		if (this.visible) {
			this.method_22703(f);
		}
	}

	private void method_22703(float f) {
		RenderSystem.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
		if (this.roll != 0.0F) {
			RenderSystem.rotatef(this.roll * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
		}

		if (this.yaw != 0.0F) {
			RenderSystem.rotatef(this.yaw * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		}

		if (this.pitch != 0.0F) {
			RenderSystem.rotatef(this.pitch * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
		}
	}

	private void method_22701(BufferBuilder bufferBuilder, float f, int i, int j, @Nullable Sprite sprite) {
		this.method_22702(bufferBuilder, f, i, j, sprite, 1.0F, 1.0F, 1.0F);
	}

	private void method_22702(BufferBuilder bufferBuilder, float f, int i, int j, @Nullable Sprite sprite, float g, float h, float k) {
		Matrix4f matrix4f = bufferBuilder.method_22631();
		VertexFormat vertexFormat = bufferBuilder.getVertexFormat();

		for (ModelPart.Cuboid cuboid : this.cuboids) {
			for (ModelPart.Quad quad : cuboid.polygons) {
				Vec3d vec3d = quad.vertices[1].pos.reverseSubtract(quad.vertices[0].pos);
				Vec3d vec3d2 = quad.vertices[1].pos.reverseSubtract(quad.vertices[2].pos);
				Vec3d vec3d3 = vec3d2.crossProduct(vec3d).normalize();
				float l = (float)vec3d3.x;
				float m = (float)vec3d3.y;
				float n = (float)vec3d3.z;

				for (int o = 0; o < 4; o++) {
					ModelPart.Vertex vertex = quad.vertices[o];
					Vector4f vector4f = new Vector4f((float)vertex.pos.x * f, (float)vertex.pos.y * f, (float)vertex.pos.z * f, 1.0F);
					vector4f.method_22674(matrix4f);
					bufferBuilder.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
					if (vertexFormat.hasColorElement()) {
						float p = MathHelper.method_22451(l, m, n);
						bufferBuilder.color(p * g, p * h, p * k, 1.0F);
					}

					if (sprite == null) {
						bufferBuilder.texture((double)vertex.u, (double)vertex.v);
					} else {
						bufferBuilder.texture((double)sprite.getU((double)(vertex.u * 16.0F)), (double)sprite.getV((double)(vertex.v * 16.0F)));
					}

					if (vertexFormat.hasUvElement(1)) {
						bufferBuilder.texture(i, j);
					}

					bufferBuilder.normal(l, m, n).next();
				}
			}
		}
	}

	public ModelPart setTextureSize(int i, int j) {
		this.textureWidth = (float)i;
		this.textureHeight = (float)j;
		return this;
	}

	public ModelPart.Cuboid method_22700(Random random) {
		return (ModelPart.Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
	}

	@Environment(EnvType.CLIENT)
	public static class Cuboid {
		private final ModelPart.Quad[] polygons;
		public final float xMin;
		public final float yMin;
		public final float zMin;
		public final float xMax;
		public final float yMax;
		public final float zMax;

		public Cuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, boolean bl, float o, float p) {
			this.xMin = f;
			this.yMin = g;
			this.zMin = h;
			this.xMax = f + k;
			this.yMax = g + l;
			this.zMax = h + m;
			this.polygons = new ModelPart.Quad[6];
			float q = f + k;
			float r = g + l;
			float s = h + m;
			f -= n;
			g -= n;
			h -= n;
			q += n;
			r += n;
			s += n;
			if (bl) {
				float t = q;
				q = f;
				f = t;
			}

			ModelPart.Vertex vertex = new ModelPart.Vertex(f, g, h, 0.0F, 0.0F);
			ModelPart.Vertex vertex2 = new ModelPart.Vertex(q, g, h, 0.0F, 8.0F);
			ModelPart.Vertex vertex3 = new ModelPart.Vertex(q, r, h, 8.0F, 8.0F);
			ModelPart.Vertex vertex4 = new ModelPart.Vertex(f, r, h, 8.0F, 0.0F);
			ModelPart.Vertex vertex5 = new ModelPart.Vertex(f, g, s, 0.0F, 0.0F);
			ModelPart.Vertex vertex6 = new ModelPart.Vertex(q, g, s, 0.0F, 8.0F);
			ModelPart.Vertex vertex7 = new ModelPart.Vertex(q, r, s, 8.0F, 8.0F);
			ModelPart.Vertex vertex8 = new ModelPart.Vertex(f, r, s, 8.0F, 0.0F);
			this.polygons[0] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, (float)i + m + k, (float)j + m, (float)i + m + k + m, (float)j + m + l, o, p
			);
			this.polygons[1] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, (float)i, (float)j + m, (float)i + m, (float)j + m + l, o, p
			);
			this.polygons[2] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, (float)i + m, (float)j, (float)i + m + k, (float)j + m, o, p
			);
			this.polygons[3] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, (float)i + m + k, (float)j + m, (float)i + m + k + k, (float)j, o, p
			);
			this.polygons[4] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, (float)i + m, (float)j + m, (float)i + m + k, (float)j + m + l, o, p
			);
			this.polygons[5] = new ModelPart.Quad(
				new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, (float)i + m + k + m, (float)j + m, (float)i + m + k + m + k, (float)j + m + l, o, p
			);
			if (bl) {
				for (ModelPart.Quad quad : this.polygons) {
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
