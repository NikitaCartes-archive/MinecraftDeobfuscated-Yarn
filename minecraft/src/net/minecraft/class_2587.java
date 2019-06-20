package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2587 extends class_2586 {
	private class_1767 field_11869;

	public class_2587() {
		super(class_2591.field_11910);
	}

	public class_2587(class_1767 arg) {
		this();
		this.method_11019(arg);
	}

	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 11, this.method_16887());
	}

	@Environment(EnvType.CLIENT)
	public class_1767 method_11018() {
		if (this.field_11869 == null) {
			this.field_11869 = ((class_2244)this.method_11010().method_11614()).method_9487();
		}

		return this.field_11869;
	}

	public void method_11019(class_1767 arg) {
		this.field_11869 = arg;
	}
}
