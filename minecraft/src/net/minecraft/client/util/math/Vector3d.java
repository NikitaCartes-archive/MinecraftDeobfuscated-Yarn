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

	public void copy(Vector3d vector3d) {
		this.x = vector3d.x;
		this.y = vector3d.y;
		this.z = vector3d.z;
	}

	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void multiply(double amount) {
		this.x *= amount;
		this.y *= amount;
		this.z *= amount;
	}

	public void add(Vector3d vector3d) {
		this.x = this.x + vector3d.x;
		this.y = this.y + vector3d.y;
		this.z = this.z + vector3d.z;
	}
}
