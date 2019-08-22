package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Rect2i {
	private int x;
	private int y;
	private int width;
	private int height;

	public Rect2i(int i, int j, int k, int l) {
		this.x = i;
		this.y = j;
		this.width = k;
		this.height = l;
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

	public boolean contains(int i, int j) {
		return i >= this.x && i <= this.x + this.width && j >= this.y && j <= this.y + this.height;
	}
}
