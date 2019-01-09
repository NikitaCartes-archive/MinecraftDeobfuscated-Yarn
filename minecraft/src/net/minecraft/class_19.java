package net.minecraft;

public class class_19 {
	private final class_2338 field_75;
	private final int field_74;
	private final int field_73;

	public class_19(class_2338 arg, int i, int j) {
		this.field_75 = arg;
		this.field_74 = i;
		this.field_73 = j;
	}

	public static class_19 method_87(class_2487 arg) {
		class_2338 lv = class_2512.method_10691(arg.method_10562("Pos"));
		int i = arg.method_10550("Rotation");
		int j = arg.method_10550("EntityId");
		return new class_19(lv, i, j);
	}

	public class_2487 method_84() {
		class_2487 lv = new class_2487();
		lv.method_10566("Pos", class_2512.method_10692(this.field_75));
		lv.method_10569("Rotation", this.field_74);
		lv.method_10569("EntityId", this.field_73);
		return lv;
	}

	public class_2338 method_86() {
		return this.field_75;
	}

	public int method_83() {
		return this.field_74;
	}

	public int method_85() {
		return this.field_73;
	}

	public String method_82() {
		return method_81(this.field_75);
	}

	public static String method_81(class_2338 arg) {
		return "frame-" + arg.method_10263() + "," + arg.method_10264() + "," + arg.method_10260();
	}
}
