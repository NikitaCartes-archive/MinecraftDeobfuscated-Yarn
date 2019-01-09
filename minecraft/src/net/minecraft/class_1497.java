package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_1497 {
	field_6979(0),
	field_6977(5, "iron", "meo"),
	field_6982(7, "gold", "goo"),
	field_6980(11, "diamond", "dio");

	private final String field_6983;
	private final String field_6984;
	private final int field_6978;

	private class_1497(int j) {
		this.field_6978 = j;
		this.field_6983 = null;
		this.field_6984 = "";
	}

	private class_1497(int j, String string2, String string3) {
		this.field_6978 = j;
		this.field_6983 = "textures/entity/horse/armor/horse_armor_" + string2 + ".png";
		this.field_6984 = string3;
	}

	public int method_6778() {
		return this.ordinal();
	}

	@Environment(EnvType.CLIENT)
	public String method_6780() {
		return this.field_6984;
	}

	public int method_6776() {
		return this.field_6978;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_6775() {
		return this.field_6983;
	}

	public static class_1497 method_6781(int i) {
		return values()[i];
	}

	public static class_1497 method_6779(class_1799 arg) {
		return arg.method_7960() ? field_6979 : method_6777(arg.method_7909());
	}

	public static class_1497 method_6777(class_1792 arg) {
		if (arg == class_1802.field_8578) {
			return field_6977;
		} else if (arg == class_1802.field_8560) {
			return field_6982;
		} else {
			return arg == class_1802.field_8807 ? field_6980 : field_6979;
		}
	}

	public static boolean method_6782(class_1792 arg) {
		return method_6777(arg) != field_6979;
	}
}
