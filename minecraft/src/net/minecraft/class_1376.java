package net.minecraft;

import java.util.EnumSet;

public class class_1376 extends class_1352 {
	private final class_1308 field_6556;
	private double field_6554;
	private double field_6553;
	private int field_6555;

	public class_1376(class_1308 arg) {
		this.field_6556 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
	}

	@Override
	public boolean method_6264() {
		return this.field_6556.method_6051().nextFloat() < 0.02F;
	}

	@Override
	public boolean method_6266() {
		return this.field_6555 >= 0;
	}

	@Override
	public void method_6269() {
		double d = (Math.PI * 2) * this.field_6556.method_6051().nextDouble();
		this.field_6554 = Math.cos(d);
		this.field_6553 = Math.sin(d);
		this.field_6555 = 20 + this.field_6556.method_6051().nextInt(20);
	}

	@Override
	public void method_6268() {
		this.field_6555--;
		this.field_6556
			.method_5988()
			.method_20248(
				this.field_6556.field_5987 + this.field_6554,
				this.field_6556.field_6010 + (double)this.field_6556.method_5751(),
				this.field_6556.field_6035 + this.field_6553
			);
	}
}
