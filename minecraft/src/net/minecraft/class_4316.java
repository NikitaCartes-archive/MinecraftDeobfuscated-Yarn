package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.DynamicSerializable;

public final class class_4316 implements DynamicSerializable {
	private final long field_19418;

	private class_4316(long l) {
		this.field_19418 = l;
	}

	public long method_20790() {
		return this.field_19418;
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return dynamicOps.createLong(this.field_19418);
	}

	public static class_4316 method_20792(Dynamic<?> dynamic) {
		return new class_4316(dynamic.asNumber(Integer.valueOf(0)).longValue());
	}

	public static class_4316 method_20791(long l) {
		return new class_4316(l);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4316 lv = (class_4316)object;
			return this.field_19418 == lv.field_19418;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Long.hashCode(this.field_19418);
	}

	public String toString() {
		return Long.toString(this.field_19418);
	}
}
