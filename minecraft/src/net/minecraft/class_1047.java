package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class class_1047 extends class_1058 {
	private static final class_2960 field_5219 = new class_2960("missingno");
	@Nullable
	private static class_1043 field_5220;
	private static final class_1011 field_5221 = new class_1011(16, 16, false);
	private static final class_1047 field_5218 = class_156.method_656(() -> {
		class_1047 lv = new class_1047();
		int i = -16777216;
		int j = -524040;

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				if (k < 8 ^ l < 8) {
					field_5221.method_4305(l, k, -524040);
				} else {
					field_5221.method_4305(l, k, -16777216);
				}
			}
		}

		field_5221.method_4302();
		return lv;
	});

	private class_1047() {
		super(field_5219, 16, 16);
		this.field_5262 = new class_1011[1];
		this.field_5262[0] = field_5221;
	}

	public static class_1047 method_4541() {
		return field_5218;
	}

	public static class_2960 method_4539() {
		return field_5219;
	}

	@Override
	public void method_4588() {
		for (int i = 1; i < this.field_5262.length; i++) {
			this.field_5262[i].close();
		}

		this.field_5262 = new class_1011[1];
		this.field_5262[0] = field_5221;
	}

	public static class_1043 method_4540() {
		if (field_5220 == null) {
			field_5220 = new class_1043(field_5221);
			class_310.method_1551().method_1531().method_4616(field_5219, field_5220);
		}

		return field_5220;
	}
}
