package net.minecraft;

public class class_3229 {
	private final class_2680 field_14026;
	private final int field_14028;
	private int field_14027;

	public class_3229(int i, class_2248 arg) {
		this.field_14028 = i;
		this.field_14026 = arg.method_9564();
	}

	public int method_14289() {
		return this.field_14028;
	}

	public class_2680 method_14286() {
		return this.field_14026;
	}

	public int method_14288() {
		return this.field_14027;
	}

	public void method_14287(int i) {
		this.field_14027 = i;
	}

	public String toString() {
		return (this.field_14028 > 1 ? this.field_14028 + "*" : "") + class_2378.field_11146.method_10221(this.field_14026.method_11614());
	}
}
