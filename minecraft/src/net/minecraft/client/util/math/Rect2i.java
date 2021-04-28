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

	public Rect2i intersection(Rect2i rect) {
		int i = this.x;
		int j = this.y;
		int k = this.x + this.width;
		int l = this.y + this.height;
		int m = rect.getX();
		int n = rect.getY();
		int o = m + rect.getWidth();
		int p = n + rect.getHeight();
		this.x = Math.max(i, m);
		this.y = Math.max(j, n);
		this.width = Math.max(0, Math.min(k, o) - this.x);
		this.height = Math.max(0, Math.min(l, p) - this.y);
		return this;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setStartPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}
}
