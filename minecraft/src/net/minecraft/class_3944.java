package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3944 implements class_2596<class_2602> {
	private int field_17436;
	private int field_17437;
	private class_2561 field_17438;

	public class_3944() {
	}

	public class_3944(int i, class_3917<?> arg, class_2561 arg2) {
		this.field_17436 = i;
		this.field_17437 = class_2378.field_17429.method_10249(arg);
		this.field_17438 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_17436 = arg.method_10816();
		this.field_17437 = arg.method_10816();
		this.field_17438 = arg.method_10808();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_17436);
		arg.method_10804(this.field_17437);
		arg.method_10805(this.field_17438);
	}

	public void method_17591(class_2602 arg) {
		arg.method_17587(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_17592() {
		return this.field_17436;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_3917<?> method_17593() {
		return class_2378.field_17429.method_10200(this.field_17437);
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_17594() {
		return this.field_17438;
	}
}
