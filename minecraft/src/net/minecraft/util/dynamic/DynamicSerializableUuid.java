package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.util.Util;

public final class DynamicSerializableUuid {
	public static final Codec<UUID> CODEC = Codec.INT_STREAM
		.comapFlatMap(uuidStream -> Util.toArray(uuidStream, 4).map(DynamicSerializableUuid::toUuid), uuid -> Arrays.stream(toIntArray(uuid)));

	private DynamicSerializableUuid() {
	}

	public static UUID toUuid(int[] array) {
		return new UUID((long)array[0] << 32 | (long)array[1] & 4294967295L, (long)array[2] << 32 | (long)array[3] & 4294967295L);
	}

	public static int[] toIntArray(UUID uuid) {
		long l = uuid.getMostSignificantBits();
		long m = uuid.getLeastSignificantBits();
		return toIntArray(l, m);
	}

	private static int[] toIntArray(long uuidMost, long uuidLeast) {
		return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
	}

	public static UUID toUuid(Dynamic<?> dynamic) {
		int[] is = dynamic.asIntStream().toArray();
		if (is.length != 4) {
			throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + is.length + ".");
		} else {
			return toUuid(is);
		}
	}
}
