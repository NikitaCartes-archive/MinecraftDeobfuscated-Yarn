package net.minecraft.client.util;

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

	public Rect2i method_35780(Rect2i rect2i) {
		int i = this.x;
		int j = this.y;
		int k = this.x + this.width;
		int l = this.y + this.height;
		int m = rect2i.getX();
		int n = rect2i.getY();
		int o = m + rect2i.getWidth();
		int p = n + rect2i.getHeight();
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

	public void method_35778(int i) {
		this.x = i;
	}

	public void method_35781(int i) {
		this.y = i;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void method_35782(int i) {
		this.width = i;
	}

	public void method_35783(int i) {
		this.height = i;
	}

	public void method_35779(int i, int j) {
		this.x = i;
		this.y = j;
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}
}
