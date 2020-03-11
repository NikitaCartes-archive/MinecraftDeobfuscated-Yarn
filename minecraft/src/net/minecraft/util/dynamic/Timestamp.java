package net.minecraft.util.dynamic;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public final class Timestamp implements DynamicSerializable {
	private final long time;

	private Timestamp(long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createLong(this.time);
	}

	public static Timestamp of(Dynamic<?> dynamic) {
		return new Timestamp(dynamic.asNumber(Integer.valueOf(0)).longValue());
	}

	public static Timestamp of(long time) {
		return new Timestamp(time);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			Timestamp timestamp = (Timestamp)obj;
			return this.time == timestamp.time;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Long.hashCode(this.time);
	}

	public String toString() {
		return Long.toString(this.time);
	}
}
