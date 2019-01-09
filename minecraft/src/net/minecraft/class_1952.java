package net.minecraft;

public class class_1952 extends class_3549.class_3550 {
	private final class_2487 field_9307;

	public class_1952() {
		super(1);
		this.field_9307 = new class_2487();
		this.field_9307.method_10582("id", "minecraft:pig");
	}

	public class_1952(class_2487 arg) {
		this(arg.method_10573("Weight", 99) ? arg.method_10550("Weight") : 1, arg.method_10562("Entity"));
	}

	public class_1952(int i, class_2487 arg) {
		super(i);
		this.field_9307 = arg;
	}

	public class_2487 method_8679() {
		class_2487 lv = new class_2487();
		if (!this.field_9307.method_10573("id", 8)) {
			this.field_9307.method_10582("id", "minecraft:pig");
		} else if (!this.field_9307.method_10558("id").contains(":")) {
			this.field_9307.method_10582("id", new class_2960(this.field_9307.method_10558("id")).toString());
		}

		lv.method_10566("Entity", this.field_9307);
		lv.method_10569("Weight", this.field_15774);
		return lv;
	}

	public class_2487 method_8678() {
		return this.field_9307;
	}
}
