package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Vector2f {
	private final float x;
	private final float y;

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public String toString() {
		return "(" + this.x + "," + this.y + ")";
	}
}
