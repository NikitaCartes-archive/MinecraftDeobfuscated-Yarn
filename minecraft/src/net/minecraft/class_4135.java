package net.minecraft;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class class_4135 extends class_1352 {
	private final class_1352 field_18416;
	private final int field_18417;
	private boolean field_18418;

	public class_4135(int i, class_1352 arg) {
		this.field_18417 = i;
		this.field_18416 = arg;
	}

	public boolean method_19055(class_4135 arg) {
		return this.method_6267() && arg.method_19057() < this.method_19057();
	}

	@Override
	public boolean method_6264() {
		return this.field_18416.method_6264();
	}

	@Override
	public boolean method_6266() {
		return this.field_18416.method_6266();
	}

	@Override
	public boolean method_6267() {
		return this.field_18416.method_6267();
	}

	@Override
	public void method_6269() {
		if (!this.field_18418) {
			this.field_18418 = true;
			this.field_18416.method_6269();
		}
	}

	@Override
	public void method_6270() {
		if (this.field_18418) {
			this.field_18418 = false;
			this.field_18416.method_6270();
		}
	}

	@Override
	public void method_6268() {
		this.field_18416.method_6268();
	}

	@Override
	public void method_6265(EnumSet<class_1352.class_4134> enumSet) {
		this.field_18416.method_6265(enumSet);
	}

	@Override
	public EnumSet<class_1352.class_4134> method_6271() {
		return this.field_18416.method_6271();
	}

	public boolean method_19056() {
		return this.field_18418;
	}

	public int method_19057() {
		return this.field_18417;
	}

	public class_1352 method_19058() {
		return this.field_18416;
	}

	public boolean equals(@Nullable Object object) {
		if (this == object) {
			return true;
		} else {
			return object != null && this.getClass() == object.getClass() ? this.field_18416.equals(((class_4135)object).field_18416) : false;
		}
	}

	public int hashCode() {
		return this.field_18416.hashCode();
	}
}
