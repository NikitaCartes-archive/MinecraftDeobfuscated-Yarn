package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2743 implements class_2596<class_2602> {
	private int field_12564;
	private int field_12563;
	private int field_12562;
	private int field_12561;

	public class_2743() {
	}

	public class_2743(class_1297 arg) {
		this(arg.method_5628(), arg.method_18798());
	}

	public class_2743(int i, class_243 arg) {
		this.field_12564 = i;
		double d = 3.9;
		double e = class_3532.method_15350(arg.field_1352, -3.9, 3.9);
		double f = class_3532.method_15350(arg.field_1351, -3.9, 3.9);
		double g = class_3532.method_15350(arg.field_1350, -3.9, 3.9);
		this.field_12563 = (int)(e * 8000.0);
		this.field_12562 = (int)(f * 8000.0);
		this.field_12561 = (int)(g * 8000.0);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12564 = arg.method_10816();
		this.field_12563 = arg.readShort();
		this.field_12562 = arg.readShort();
		this.field_12561 = arg.readShort();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12564);
		arg.writeShort(this.field_12563);
		arg.writeShort(this.field_12562);
		arg.writeShort(this.field_12561);
	}

	public void method_11817(class_2602 arg) {
		arg.method_11132(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11818() {
		return this.field_12564;
	}

	@Environment(EnvType.CLIENT)
	public int method_11815() {
		return this.field_12563;
	}

	@Environment(EnvType.CLIENT)
	public int method_11816() {
		return this.field_12562;
	}

	@Environment(EnvType.CLIENT)
	public int method_11819() {
		return this.field_12561;
	}
}
