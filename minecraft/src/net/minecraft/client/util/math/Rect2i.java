package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Rect2i {
	private int x;
	private int y;
	private int width;
	private int height;

	public Rect2i(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}
}
