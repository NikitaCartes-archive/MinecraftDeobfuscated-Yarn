package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class OverlayVertexConsumer extends FixedColorVertexConsumer {
	private final VertexConsumer vertexConsumer;
	private final Matrix4f textureMatrix;
	private final Matrix3f normalMatrix;
	private float x;
	private float y;
	private float z;
	private int u1;
	private int v1;
	private int light;
	private float normalX;
	private float normalY;
	private float normalZ;

	public OverlayVertexConsumer(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f) {
		this.vertexConsumer = vertexConsumer;
		this.textureMatrix = matrix4f.copy();
		this.textureMatrix.invert();
		this.normalMatrix = matrix3f.copy();
		this.normalMatrix.invert();
		this.init();
	}

	private void init() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		this.u1 = 0;
		this.v1 = 10;
		this.light = 15728880;
		this.normalX = 0.0F;
		this.normalY = 1.0F;
		this.normalZ = 0.0F;
	}

	@Override
	public void next() {
		Vec3f vec3f = new Vec3f(this.normalX, this.normalY, this.normalZ);
		vec3f.transform(this.normalMatrix);
		Direction direction = Direction.getFacing(vec3f.getX(), vec3f.getY(), vec3f.getZ());
		Vector4f vector4f = new Vector4f(this.x, this.y, this.z, 1.0F);
		vector4f.transform(this.textureMatrix);
		vector4f.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		vector4f.rotate(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
		vector4f.rotate(direction.getRotationQuaternion());
		float f = -vector4f.getX();
		float g = -vector4f.getY();
		this.vertexConsumer
			.vertex((double)this.x, (double)this.y, (double)this.z)
			.color(1.0F, 1.0F, 1.0F, 1.0F)
			.texture(f, g)
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
		return this;
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
