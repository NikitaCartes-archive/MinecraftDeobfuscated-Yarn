package net.minecraft;

public abstract class class_18 {
	private final String field_71;
	private boolean field_72;

	public class_18(String string) {
		this.field_71 = string;
	}

	public abstract void method_77(class_2487 arg);

	public abstract class_2487 method_75(class_2487 arg);

	public void method_80() {
		this.method_78(true);
	}

	public void method_78(boolean bl) {
		this.field_72 = bl;
	}

	public boolean method_79() {
		return this.field_72;
	}

	public String method_76() {
		return this.field_71;
	}
}
