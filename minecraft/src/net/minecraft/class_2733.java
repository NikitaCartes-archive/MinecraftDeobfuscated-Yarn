package net.minecraft;

import com.google.common.base.MoreObjects;

public abstract class class_2733<T extends Comparable<T>> implements class_2769<T> {
	private final Class<T> field_12459;
	private final String field_12460;
	private Integer field_12461;

	protected class_2733(String string, Class<T> class_) {
		this.field_12459 = class_;
		this.field_12460 = string;
	}

	@Override
	public String method_11899() {
		return this.field_12460;
	}

	@Override
	public Class<T> method_11902() {
		return this.field_12459;
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", this.field_12460).add("clazz", this.field_12459).add("values", this.method_11898()).toString();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2733)) {
			return false;
		} else {
			class_2733<?> lv = (class_2733<?>)object;
			return this.field_12459.equals(lv.field_12459) && this.field_12460.equals(lv.field_12460);
		}
	}

	public final int hashCode() {
		if (this.field_12461 == null) {
			this.field_12461 = this.method_11799();
		}

		return this.field_12461;
	}

	public int method_11799() {
		return 31 * this.field_12459.hashCode() + this.field_12460.hashCode();
	}
}
