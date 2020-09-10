package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.util.Util;

public final class DynamicSerializableUuid {
	public static final Codec<UUID> CODEC = Codec.INT_STREAM
		.comapFlatMap(intStream -> Util.toIntArray(intStream, 4).map(DynamicSerializableUuid::toUuid), uUID -> Arrays.stream(toIntArray(uUID)));

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
}
