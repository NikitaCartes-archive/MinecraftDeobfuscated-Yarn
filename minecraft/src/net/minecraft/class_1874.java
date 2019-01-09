package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1874 implements class_1860 {
	protected final class_2960 field_9060;
	protected final String field_9062;
	protected final class_1856 field_9061;
	protected final class_1799 field_9059;
	protected final float field_9057;
	protected final int field_9058;

	public class_1874(class_2960 arg, String string, class_1856 arg2, class_1799 arg3, float f, int i) {
		this.field_9060 = arg;
		this.field_9062 = string;
		this.field_9061 = arg2;
		this.field_9059 = arg3;
		this.field_9057 = f;
		this.field_9058 = i;
	}

	@Override
	public class_1799 method_8116(class_1263 arg) {
		return this.field_9059.method_7972();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return true;
	}

	@Override
	public class_2371<class_1856> method_8117() {
		class_2371<class_1856> lv = class_2371.method_10211();
		lv.add(this.field_9061);
		return lv;
	}

	public float method_8171() {
		return this.field_9057;
	}

	@Override
	public class_1799 method_8110() {
		return this.field_9059;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_8112() {
		return this.field_9062;
	}

	public int method_8167() {
		return this.field_9058;
	}

	@Override
	public class_2960 method_8114() {
		return this.field_9060;
	}
}
