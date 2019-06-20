package net.minecraft;

public class class_3919 implements class_3913 {
	private final int[] field_17345;

	public class_3919(int i) {
		this.field_17345 = new int[i];
	}

	@Override
	public int method_17390(int i) {
		return this.field_17345[i];
	}

	@Override
	public void method_17391(int i, int j) {
		this.field_17345[i] = j;
	}

	@Override
	public int method_17389() {
		return this.field_17345.length;
	}
}
