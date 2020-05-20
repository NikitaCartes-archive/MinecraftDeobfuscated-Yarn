package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;

public final class Timestamp {
	public static final Codec<Timestamp> field_25121 = Codec.LONG.xmap(Timestamp::new, timestamp -> timestamp.time);
	private final long time;

	private Timestamp(long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
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
