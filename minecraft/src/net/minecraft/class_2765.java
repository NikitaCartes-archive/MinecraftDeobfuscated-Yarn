package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;

public class class_2765 implements class_2596<class_2602> {
	private class_3414 field_12642;
	private class_3419 field_12641;
	private int field_12640;
	private float field_12639;
	private float field_12638;

	public class_2765() {
	}

	public class_2765(class_3414 arg, class_3419 arg2, class_1297 arg3, float f, float g) {
		Validate.notNull(arg, "sound");
		this.field_12642 = arg;
		this.field_12641 = arg2;
		this.field_12640 = arg3.method_5628();
		this.field_12639 = f;
		this.field_12638 = g;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12642 = class_2378.field_11156.method_10200(arg.method_10816());
		this.field_12641 = arg.method_10818(class_3419.class);
		this.field_12640 = arg.method_10816();
		this.field_12639 = arg.readFloat();
		this.field_12638 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(class_2378.field_11156.method_10249(this.field_12642));
		arg.method_10817(this.field_12641);
		arg.method_10804(this.field_12640);
		arg.writeFloat(this.field_12639);
		arg.writeFloat(this.field_12638);
	}

	@Environment(EnvType.CLIENT)
	public class_3414 method_11882() {
		return this.field_12642;
	}

	@Environment(EnvType.CLIENT)
	public class_3419 method_11881() {
		return this.field_12641;
	}

	@Environment(EnvType.CLIENT)
	public int method_11883() {
		return this.field_12640;
	}

	@Environment(EnvType.CLIENT)
	public float method_11885() {
		return this.field_12639;
	}

	@Environment(EnvType.CLIENT)
	public float method_11880() {
		return this.field_12638;
	}

	public void method_11884(class_2602 arg) {
		arg.method_11125(this);
	}
}
