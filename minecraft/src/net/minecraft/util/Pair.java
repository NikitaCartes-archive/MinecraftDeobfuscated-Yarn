package net.minecraft.util;

public class Pair<A, B> {
	private A left;
	private B right;

	public Pair(A left, B right) {
		this.left = left;
		this.right = right;
	}

	public A getLeft() {
		return this.left;
	}

	public B getRight() {
		return this.right;
	}
}
