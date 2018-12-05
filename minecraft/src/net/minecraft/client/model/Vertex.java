package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class Vertex {
	public Vec3d pos;
	public float u;
	public float v;

	public Vertex(float f, float g, float h, float i, float j) {
		this(new Vec3d((double)f, (double)g, (double)h), i, j);
	}

	public Vertex remap(float f, float g) {
		return new Vertex(this, f, g);
	}

	public Vertex(Vertex vertex, float f, float g) {
		this.pos = vertex.pos;
		this.u = f;
		this.v = g;
	}

	public Vertex(Vec3d vec3d, float f, float g) {
		this.pos = vec3d;
		this.u = f;
		this.v = g;
	}
}
