package net.minecraft.util.dynamic;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public final class DynamicSerializableBoolean implements DynamicSerializable {
	private final boolean value;

	private DynamicSerializableBoolean(boolean value) {
		this.value = value;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createBoolean(this.value);
	}

	public static DynamicSerializableBoolean of(Dynamic<?> dynamic) {
		return new DynamicSerializableBoolean(dynamic.asBoolean(false));
	}

	public static DynamicSerializableBoolean of(boolean value) {
		return new DynamicSerializableBoolean(value);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			DynamicSerializableBoolean dynamicSerializableBoolean = (DynamicSerializableBoolean)o;
			return this.value == dynamicSerializableBoolean.value;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Boolean.hashCode(this.value);
	}

	public String toString() {
		return Boolean.toString(this.value);
	}
}
