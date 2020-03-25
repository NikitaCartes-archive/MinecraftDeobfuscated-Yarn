package net.minecraft.util.dynamic;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Arrays;
import java.util.UUID;

public final class DynamicSerializableUuid implements DynamicSerializable {
	private final UUID uuid;

	public DynamicSerializableUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return method_26430(ops, this.uuid);
	}

	public static DynamicSerializableUuid of(Dynamic<?> dynamic) {
		return new DynamicSerializableUuid(method_26431(dynamic));
	}

	public String toString() {
		return this.uuid.toString();
	}

	public static UUID method_26276(int[] is) {
		return new UUID((long)is[0] << 32 | (long)is[1] & 4294967295L, (long)is[2] << 32 | (long)is[3] & 4294967295L);
	}

	public static int[] method_26275(UUID uUID) {
		long l = uUID.getMostSignificantBits();
		long m = uUID.getLeastSignificantBits();
		return method_26274(l, m);
	}

	public static int[] method_26274(long l, long m) {
		return new int[]{(int)(l >> 32), (int)l, (int)(m >> 32), (int)m};
	}

	public static UUID method_26431(Dynamic<?> dynamic) {
		int[] is = dynamic.asIntStream().toArray();
		if (is.length != 4) {
			throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + is.length + ".");
		} else {
			return method_26276(is);
		}
	}

	public static <T> T method_26430(DynamicOps<T> dynamicOps, UUID uUID) {
		return dynamicOps.createIntList(Arrays.stream(method_26275(uUID)));
	}
}
