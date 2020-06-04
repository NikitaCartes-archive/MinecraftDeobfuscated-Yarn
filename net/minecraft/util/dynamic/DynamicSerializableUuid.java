/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.util.Util;

public final class DynamicSerializableUuid {
    public static final Codec<UUID> field_25122 = Codec.INT_STREAM.comapFlatMap(intStream -> Util.method_29190(intStream, 4).map(DynamicSerializableUuid::method_26276), uUID -> Arrays.stream(DynamicSerializableUuid.method_26275(uUID)));

    public static UUID method_26276(int[] is) {
        return new UUID((long)is[0] << 32 | (long)is[1] & 0xFFFFFFFFL, (long)is[2] << 32 | (long)is[3] & 0xFFFFFFFFL);
    }

    public static int[] method_26275(UUID uUID) {
        long l = uUID.getMostSignificantBits();
        long m = uUID.getLeastSignificantBits();
        return DynamicSerializableUuid.method_26274(l, m);
    }

    private static int[] method_26274(long l, long m) {
        return new int[]{(int)(l >> 32), (int)l, (int)(m >> 32), (int)m};
    }
}

