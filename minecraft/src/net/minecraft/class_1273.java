package net.minecraft;

import javax.annotation.concurrent.Immutable;

@Immutable
public class class_1273 {
	public static final class_1273 field_5817 = new class_1273("");
	private final String field_5818;

	public class_1273(String string) {
		this.field_5818 = string;
	}

	public boolean method_5472(class_1799 arg) {
		return this.field_5818.isEmpty() || !arg.method_7960() && arg.method_7938() && this.field_5818.equals(arg.method_7964().getString());
	}

	public void method_5474(class_2487 arg) {
		if (!this.field_5818.isEmpty()) {
			arg.method_10582("Lock", this.field_5818);
		}
	}

	public static class_1273 method_5473(class_2487 arg) {
		return arg.method_10573("Lock", 8) ? new class_1273(arg.method_10558("Lock")) : field_5817;
	}
}
