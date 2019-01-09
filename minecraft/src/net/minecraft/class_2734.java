package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2734 implements class_2596<class_2602> {
	public int field_12462;

	public class_2734() {
	}

	public class_2734(class_1297 arg) {
		this.field_12462 = arg.method_5628();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12462 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12462);
	}

	public void method_11801(class_2602 arg) {
		arg.method_11111(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1297 method_11800(class_1937 arg) {
		return arg.method_8469(this.field_12462);
	}
}
