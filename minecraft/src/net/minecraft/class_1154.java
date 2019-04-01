package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1154 implements class_1155 {
	private static final class_2561 field_5643 = new class_2588("tutorial.open_inventory.title");
	private static final class_2561 field_5644 = new class_2588("tutorial.open_inventory.description", class_1156.method_4913("inventory"));
	private final class_1156 field_5640;
	private class_372 field_5642;
	private int field_5641;

	public class_1154(class_1156 arg) {
		this.field_5640 = arg;
	}

	@Override
	public void method_4899() {
		this.field_5641++;
		if (this.field_5640.method_4905() != class_1934.field_9215) {
			this.field_5640.method_4910(class_1157.field_5653);
		} else {
			if (this.field_5641 >= 600 && this.field_5642 == null) {
				this.field_5642 = new class_372(class_372.class_373.field_2233, field_5643, field_5644, false);
				this.field_5640.method_4914().method_1566().method_1999(this.field_5642);
			}
		}
	}

	@Override
	public void method_4902() {
		if (this.field_5642 != null) {
			this.field_5642.method_1993();
			this.field_5642 = null;
		}
	}

	@Override
	public void method_4904() {
		this.field_5640.method_4910(class_1157.field_5655);
	}
}
