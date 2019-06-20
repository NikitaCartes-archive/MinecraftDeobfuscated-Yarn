package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_1934 {
	field_9218(-1, ""),
	field_9215(0, "survival"),
	field_9220(1, "creative"),
	field_9216(2, "adventure"),
	field_9219(3, "spectator");

	private final int field_9217;
	private final String field_9221;

	private class_1934(int j, String string2) {
		this.field_9217 = j;
		this.field_9221 = string2;
	}

	public int method_8379() {
		return this.field_9217;
	}

	public String method_8381() {
		return this.field_9221;
	}

	public class_2561 method_8383() {
		return new class_2588("gameMode." + this.field_9221);
	}

	public void method_8382(class_1656 arg) {
		if (this == field_9220) {
			arg.field_7478 = true;
			arg.field_7477 = true;
			arg.field_7480 = true;
		} else if (this == field_9219) {
			arg.field_7478 = true;
			arg.field_7477 = false;
			arg.field_7480 = true;
			arg.field_7479 = true;
		} else {
			arg.field_7478 = false;
			arg.field_7477 = false;
			arg.field_7480 = false;
			arg.field_7479 = false;
		}

		arg.field_7476 = !this.method_8387();
	}

	public boolean method_8387() {
		return this == field_9216 || this == field_9219;
	}

	public boolean method_8386() {
		return this == field_9220;
	}

	public boolean method_8388() {
		return this == field_9215 || this == field_9216;
	}

	@Environment(EnvType.CLIENT)
	public float method_26744() {
		return 3.0F;
	}

	@Environment(EnvType.CLIENT)
	public float method_26745() {
		return 6.0F;
	}

	@Environment(EnvType.CLIENT)
	public float method_26746() {
		return this.method_8386() ? 5.0F : 4.5F;
	}

	public static class_1934 method_8384(int i) {
		return method_8380(i, field_9215);
	}

	public static class_1934 method_8380(int i, class_1934 arg) {
		for (class_1934 lv : values()) {
			if (lv.field_9217 == i) {
				return lv;
			}
		}

		return arg;
	}

	public static class_1934 method_8385(String string) {
		return method_8378(string, field_9215);
	}

	public static class_1934 method_8378(String string, class_1934 arg) {
		for (class_1934 lv : values()) {
			if (lv.field_9221.equals(string)) {
				return lv;
			}
		}

		return arg;
	}
}
