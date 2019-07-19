package net.minecraft.util;

public class Pair<A, B> {
	private A field_15772;
	private B field_15773;

	public Pair(A left, B right) {
		this.field_15772 = left;
		this.field_15773 = right;
	}

	public A getLeft() {
		return this.field_15772;
	}

	public B getRight() {
		return this.field_15773;
	}
}
