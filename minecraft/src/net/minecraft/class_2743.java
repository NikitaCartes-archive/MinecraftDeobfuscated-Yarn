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
		this(arg.method_5628(), arg.field_5967, arg.field_5984, arg.field_6006);
	}

	public class_2743(int i, double d, double e, double f) {
		this.field_12564 = i;
		double g = 3.9;
		if (d < -3.9) {
			d = -3.9;
		}

		if (e < -3.9) {
			e = -3.9;
		}

		if (f < -3.9) {
			f = -3.9;
		}

		if (d > 3.9) {
			d = 3.9;
		}

		if (e > 3.9) {
			e = 3.9;
		}

		if (f > 3.9) {
			f = 3.9;
		}

		this.field_12563 = (int)(d * 8000.0);
		this.field_12562 = (int)(e * 8000.0);
		this.field_12561 = (int)(f * 8000.0);
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
