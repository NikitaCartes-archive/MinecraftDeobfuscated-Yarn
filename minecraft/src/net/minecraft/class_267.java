package net.minecraft;

import java.util.Comparator;
import javax.annotation.Nullable;

public class class_267 {
	public static final Comparator<class_267> field_1413 = (arg, arg2) -> {
		if (arg.method_1126() > arg2.method_1126()) {
			return 1;
		} else {
			return arg.method_1126() < arg2.method_1126() ? -1 : arg2.method_1129().compareToIgnoreCase(arg.method_1129());
		}
	};
	private final class_269 field_1407;
	@Nullable
	private final class_266 field_1412;
	private final String field_1409;
	private int field_1410;
	private boolean field_1411;
	private boolean field_1408;

	public class_267(class_269 arg, class_266 arg2, String string) {
		this.field_1407 = arg;
		this.field_1412 = arg2;
		this.field_1409 = string;
		this.field_1411 = true;
		this.field_1408 = true;
	}

	public void method_1124(int i) {
		if (this.field_1412.method_1116().method_1226()) {
			throw new IllegalStateException("Cannot modify read-only score");
		} else {
			this.method_1128(this.method_1126() + i);
		}
	}

	public void method_1130() {
		this.method_1124(1);
	}

	public int method_1126() {
		return this.field_1410;
	}

	public void method_1132() {
		this.method_1128(0);
	}

	public void method_1128(int i) {
		int j = this.field_1410;
		this.field_1410 = i;
		if (j != i || this.field_1408) {
			this.field_1408 = false;
			this.method_1122().method_1176(this);
		}
	}

	@Nullable
	public class_266 method_1127() {
		return this.field_1412;
	}

	public String method_1129() {
		return this.field_1409;
	}

	public class_269 method_1122() {
		return this.field_1407;
	}

	public boolean method_1131() {
		return this.field_1411;
	}

	public void method_1125(boolean bl) {
		this.field_1411 = bl;
	}
}
