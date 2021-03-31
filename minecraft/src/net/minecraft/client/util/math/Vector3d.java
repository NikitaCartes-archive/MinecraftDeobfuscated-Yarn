package net.minecraft.client.util.math;

public class Vector3d {
	public double x;
	public double y;
	public double z;

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void method_35875(Vector3d vector3d) {
		this.x = vector3d.x;
		this.y = vector3d.y;
		this.z = vector3d.z;
	}

	public void method_35874(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
	}

	public void method_35873(double d) {
		this.x *= d;
		this.y *= d;
		this.z *= d;
	}

	public void method_35876(Vector3d vector3d) {
		this.x = this.x + vector3d.x;
		this.y = this.y + vector3d.y;
		this.z = this.z + vector3d.z;
	}
}
