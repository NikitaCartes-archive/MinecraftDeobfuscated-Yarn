package net.minecraft;

import javax.annotation.Nullable;

public class class_1329 extends class_1326 {
	private final double field_6353;
	private final double field_6351;
	private String field_6352;

	public class_1329(@Nullable class_1320 arg, String string, double d, double e, double f) {
		super(arg, string, d);
		this.field_6353 = e;
		this.field_6351 = f;
		if (e > f) {
			throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
		} else if (d < e) {
			throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
		} else if (d > f) {
			throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
		}
	}

	public class_1329 method_6222(String string) {
		this.field_6352 = string;
		return this;
	}

	public String method_6221() {
		return this.field_6352;
	}

	@Override
	public double method_6165(double d) {
		return class_3532.method_15350(d, this.field_6353, this.field_6351);
	}
}
