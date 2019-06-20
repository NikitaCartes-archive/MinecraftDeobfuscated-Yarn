package net.minecraft;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;

public class class_2884 implements class_2596<class_2792> {
	private UUID field_13129;

	public class_2884() {
	}

	public class_2884(UUID uUID) {
		this.field_13129 = uUID;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13129 = arg.method_10790();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10797(this.field_13129);
	}

	public void method_12542(class_2792 arg) {
		arg.method_12073(this);
	}

	@Nullable
	public class_1297 method_12541(class_3218 arg) {
		return arg.method_14190(this.field_13129);
	}
}
