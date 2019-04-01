package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4292 extends class_4289 {
	public static final class_4289.class_4291 field_19232 = new class_4289.class_4291(1, 0);
	public static final class_4289.class_4291 field_19233 = new class_4289.class_4291(1, field_19222.field_19228);

	public class_4292(class_310 arg) {
		super(arg);
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		this.method_20275();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		this.method_20276(lv2, class_4289.class_4291.field_19231, field_19221);
		this.method_20276(lv2, field_19232, field_19222);
		this.method_20276(lv2, field_19233, field_19223);
		lv.method_1350();
	}

	@Override
	public boolean method_18640() {
		return true;
	}
}
