package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2626 implements class_2596<class_2602> {
	private class_2338 field_12052;
	private class_2680 field_12051;

	public class_2626() {
	}

	public class_2626(class_1922 arg, class_2338 arg2) {
		this.field_12052 = arg2;
		this.field_12051 = arg.method_8320(arg2);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12052 = arg.method_10811();
		this.field_12051 = class_2248.field_10651.method_10200(arg.method_10816());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_12052);
		arg.method_10804(class_2248.method_9507(this.field_12051));
	}

	public void method_11310(class_2602 arg) {
		arg.method_11136(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2680 method_11308() {
		return this.field_12051;
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11309() {
		return this.field_12052;
	}
}
