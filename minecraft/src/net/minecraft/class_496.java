package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_496 extends class_463 {
	private final class_1918 field_2976;

	public class_496(class_1918 arg) {
		this.field_2976 = arg;
	}

	@Override
	public class_1918 method_2351() {
		return this.field_2976;
	}

	@Override
	int method_2364() {
		return 150;
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_2752 = this.method_2351().method_8296();
		this.method_2368();
		this.field_2751.method_1852(this.method_2351().method_8289());
	}

	@Override
	protected void method_2352(class_1918 arg) {
		if (arg instanceof class_1697.class_1698) {
			class_1697.class_1698 lv = (class_1697.class_1698)arg;
			this.field_2563.method_1562().method_2883(new class_2871(lv.method_7569().method_5628(), this.field_2751.method_1882(), arg.method_8296()));
		}
	}
}
