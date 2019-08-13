package net.minecraft.util;

public class Pair<A, B> {
	private A field_15772;
	private B field_15773;

	public Pair(A object, B object2) {
		this.field_15772 = object;
		this.field_15773 = object2;
	}

	public A getLeft() {
		return this.field_15772;
	}

	public B getRight() {
		return this.field_15773;
	}
}
