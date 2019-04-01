package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_1326 implements class_1320 {
	private final class_1320 field_6340;
	private final String field_6339;
	private final double field_6337;
	private boolean field_6338;

	protected class_1326(@Nullable class_1320 arg, String string, double d) {
		this.field_6340 = arg;
		this.field_6339 = string;
		this.field_6337 = d;
		if (string == null) {
			throw new IllegalArgumentException("Name cannot be null!");
		}
	}

	@Override
	public String method_6167() {
		return this.field_6339;
	}

	@Override
	public double method_6169() {
		return this.field_6337;
	}

	@Override
	public boolean method_6168() {
		return this.field_6338;
	}

	public class_1326 method_6212(boolean bl) {
		this.field_6338 = bl;
		return this;
	}

	@Nullable
	@Override
	public class_1320 method_6166() {
		return this.field_6340;
	}

	public int hashCode() {
		return this.field_6339.hashCode();
	}

	public boolean equals(Object object) {
		return object instanceof class_1320 && this.field_6339.equals(((class_1320)object).method_6167());
	}
}
