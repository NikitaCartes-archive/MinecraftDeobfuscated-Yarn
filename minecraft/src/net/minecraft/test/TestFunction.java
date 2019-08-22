package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.class_4516;

public class TestFunction {
	private final String field_20583;
	private final String field_20584;
	private final String field_20585;
	private final boolean field_20586;
	private final Consumer<class_4516> field_20587;
	private final int field_20588;

	public void method_22297(class_4516 arg) {
		this.field_20587.accept(arg);
	}

	public String method_22296() {
		return this.field_20584;
	}

	public String method_22298() {
		return this.field_20585;
	}

	public String toString() {
		return this.field_20584;
	}

	public int method_22299() {
		return this.field_20588;
	}

	public boolean method_22300() {
		return this.field_20586;
	}

	public String method_22301() {
		return this.field_20583;
	}
}
