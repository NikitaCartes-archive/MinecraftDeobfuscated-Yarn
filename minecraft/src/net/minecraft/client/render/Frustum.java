package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class Frustum {
	public static final int field_34820 = 4;
	private final FrustumIntersection frustumIntersection = new FrustumIntersection();
	private final Matrix4f field_40824 = new Matrix4f();
	private Vector4f field_34821;
	private double x;
	private double y;
	private double z;

	public Frustum(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
		this.init(positionMatrix, projectionMatrix);
	}

	public Frustum(Frustum frustum) {
		this.frustumIntersection.set(frustum.field_40824);
		this.field_40824.set(frustum.field_40824);
		this.x = frustum.x;
		this.y = frustum.y;
		this.z = frustum.z;
		this.field_34821 = frustum.field_34821;
	}

	public Frustum method_38557(int i) {
		double d = Math.floor(this.x / (double)i) * (double)i;
		double e = Math.floor(this.y / (double)i) * (double)i;
		double f = Math.floor(this.z / (double)i) * (double)i;
		double g = Math.ceil(this.x / (double)i) * (double)i;
		double h = Math.ceil(this.y / (double)i) * (double)i;

		for (double j = Math.ceil(this.z / (double)i) * (double)i;
			this.frustumIntersection
					.intersectAab((float)(d - this.x), (float)(e - this.y), (float)(f - this.z), (float)(g - this.x), (float)(h - this.y), (float)(j - this.z))
				!= -2;
			this.z = this.z - (double)(this.field_34821.z() * 4.0F)
		) {
			this.x = this.x - (double)(this.field_34821.x() * 4.0F);
			this.y = this.y - (double)(this.field_34821.y() * 4.0F);
		}

		return this;
	}

	public void setPosition(double cameraX, double cameraY, double cameraZ) {
		this.x = cameraX;
		this.y = cameraY;
		this.z = cameraZ;
	}

	private void init(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
		projectionMatrix.mul(positionMatrix, this.field_40824);
		this.frustumIntersection.set(this.field_40824);
		this.field_34821 = this.field_40824.transformTranspose(new Vector4f(0.0F, 0.0F, 1.0F, 0.0F));
	}

	public boolean isVisible(Box box) {
		return this.isVisible(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	private boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		float f = (float)(minX - this.x);
		float g = (float)(minY - this.y);
		float h = (float)(minZ - this.z);
		float i = (float)(maxX - this.x);
		float j = (float)(maxY - this.y);
		float k = (float)(maxZ - this.z);
		return this.frustumIntersection.testAab(f, g, h, i, j, k);
	}
}
