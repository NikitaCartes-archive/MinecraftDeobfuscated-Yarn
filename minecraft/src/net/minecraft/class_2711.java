package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2711 implements class_2596<class_2602> {
	private int field_12406;
	private class_2338 field_12407;

	public class_2711() {
	}

	public class_2711(class_1657 arg, class_2338 arg2) {
		this.field_12406 = arg.method_5628();
		this.field_12407 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12406 = arg.method_10816();
		this.field_12407 = arg.method_10811();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12406);
		arg.method_10807(this.field_12407);
	}

	public void method_11748(class_2602 arg) {
		arg.method_11137(this);
	}

	@Environment(EnvType.CLIENT)
	public class_1657 method_11747(class_1937 arg) {
		return (class_1657)arg.method_8469(this.field_12406);
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11749() {
		return this.field_12407;
	}
}
