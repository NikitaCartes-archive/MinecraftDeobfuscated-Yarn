package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2707 implements class_2596<class_2602> {
	private double field_12386;
	private double field_12384;
	private double field_12383;
	private int field_12388;
	private class_2183.class_2184 field_12385;
	private class_2183.class_2184 field_12389;
	private boolean field_12387;

	public class_2707() {
	}

	public class_2707(class_2183.class_2184 arg, double d, double e, double f) {
		this.field_12385 = arg;
		this.field_12386 = d;
		this.field_12384 = e;
		this.field_12383 = f;
	}

	public class_2707(class_2183.class_2184 arg, class_1297 arg2, class_2183.class_2184 arg3) {
		this.field_12385 = arg;
		this.field_12388 = arg2.method_5628();
		this.field_12389 = arg3;
		class_243 lv = arg3.method_9302(arg2);
		this.field_12386 = lv.field_1352;
		this.field_12384 = lv.field_1351;
		this.field_12383 = lv.field_1350;
		this.field_12387 = true;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12385 = arg.method_10818(class_2183.class_2184.class);
		this.field_12386 = arg.readDouble();
		this.field_12384 = arg.readDouble();
		this.field_12383 = arg.readDouble();
		if (arg.readBoolean()) {
			this.field_12387 = true;
			this.field_12388 = arg.method_10816();
			this.field_12389 = arg.method_10818(class_2183.class_2184.class);
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12385);
		arg.writeDouble(this.field_12386);
		arg.writeDouble(this.field_12384);
		arg.writeDouble(this.field_12383);
		arg.writeBoolean(this.field_12387);
		if (this.field_12387) {
			arg.method_10804(this.field_12388);
			arg.method_10817(this.field_12389);
		}
	}

	public void method_11731(class_2602 arg) {
		arg.method_11092(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2183.class_2184 method_11730() {
		return this.field_12385;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_243 method_11732(class_1937 arg) {
		if (this.field_12387) {
			class_1297 lv = arg.method_8469(this.field_12388);
			return lv == null ? new class_243(this.field_12386, this.field_12384, this.field_12383) : this.field_12389.method_9302(lv);
		} else {
			return new class_243(this.field_12386, this.field_12384, this.field_12383);
		}
	}
}
