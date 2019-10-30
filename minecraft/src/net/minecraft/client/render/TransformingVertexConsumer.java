package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class TransformingVertexConsumer extends FixedColorVertexConsumer {
	private final VertexConsumer vertexConsumer;
	private final Matrix4f textureMatrix;
	private final Matrix3f normalMatrix;
	private float x;
	private float y;
	private float z;
	private int red;
	private int green;
	private int blue;
	private int alpha;
	private int u1;
	private int v1;
	private int light;
	private float normalX;
	private float normalY;
	private float normalZ;

	public TransformingVertexConsumer(VertexConsumer vertexConsumer, Matrix4f matrix) {
		this.vertexConsumer = vertexConsumer;
		this.textureMatrix = matrix.copy();
		this.textureMatrix.invert();
		this.normalMatrix = new Matrix3f(matrix);
		this.normalMatrix.transpose();
		this.init();
	}

	private void init() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		this.red = this.fixedRed;
		this.green = this.fixedGreen;
		this.blue = this.fixedBlue;
		this.alpha = this.fixedAlpha;
		this.u1 = 0;
		this.v1 = 10;
		this.light = 15728880;
		this.normalX = 0.0F;
		this.normalY = 1.0F;
		this.normalZ = 0.0F;
	}

	@Override
	public void next() {
		Vector3f vector3f = new Vector3f(this.normalX, this.normalY, this.normalZ);
		vector3f.multiply(this.normalMatrix);
		Direction direction = Direction.getFacing(vector3f.getX(), vector3f.getY(), vector3f.getZ());
		Vector4f vector4f = new Vector4f(this.x, this.y, this.z, 1.0F);
		vector4f.multiply(this.textureMatrix);
		float f;
		float g;
		switch (direction.getAxis()) {
			case X:
				f = vector4f.getZ();
				g = vector4f.getY();
				break;
			case Y:
				f = vector4f.getX();
				g = vector4f.getZ();
				break;
			case Z:
			default:
				f = vector4f.getX();
				g = vector4f.getY();
		}

		this.vertexConsumer
			.vertex((double)this.x, (double)this.y, (double)this.z)
			.color(this.red, this.green, this.blue, this.alpha)
			.texture(f, -g)
			.overlay(this.u1, this.v1)
			.light(this.light)
			.normal(this.normalX, this.normalY, this.normalZ)
			.next();
		this.init();
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
		return this;
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		if (this.colorFixed) {
			throw new IllegalStateException();
		} else {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
			return this;
		}
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		return this;
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		this.u1 = u;
		this.v1 = v;
		return this;
	}

	@Override
	public VertexConsumer light(int u, int v) {
		this.light = u | v << 16;
		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		this.normalX = x;
		this.normalY = y;
		this.normalZ = z;
		return this;
	}
}
