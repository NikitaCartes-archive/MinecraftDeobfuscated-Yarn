/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.util.UUIDTypeAdapter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.util.Util;

public final class Uuids {
    public static final Codec<UUID> INT_STREAM_CODEC = Codec.INT_STREAM.comapFlatMap(uuidStream -> Util.toArray(uuidStream, 4).map(Uuids::toUuid), uuid -> Arrays.stream(Uuids.toIntArray(uuid)));
    public static final Codec<UUID> STRING_CODEC = Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(UUID.fromString(string), Lifecycle.stable());
        } catch (IllegalArgumentException illegalArgumentException) {
            return DataResult.error(() -> "Invalid UUID " + string + ": " + illegalArgumentException.getMessage());
        }
    }, UUID::toString);
    public static Codec<UUID> CODEC = Codec.either(INT_STREAM_CODEC, Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(UUIDTypeAdapter.fromString(string), Lifecycle.stable());
        } catch (IllegalArgumentException illegalArgumentException) {
            return DataResult.error(() -> "Invalid UUID " + string + ": " + illegalArgumentException.getMessage());
        }
    }, UUIDTypeAdapter::fromUUID)).xmap(either -> either.map(uuid -> uuid, uuid -> uuid), Either::right);
    public static final int BYTE_ARRAY_SIZE = 16;
    private static final String OFFLINE_PLAYER_UUID_PREFIX = "OfflinePlayer:";

    private Uuids() {
    }

    public static UUID toUuid(int[] array) {
        return new UUID((long)array[0] << 32 | (long)array[1] & 0xFFFFFFFFL, (long)array[2] << 32 | (long)array[3] & 0xFFFFFFFFL);
    }

    public static int[] toIntArray(UUID uuid) {
        long l = uuid.getMostSignificantBits();
        long m = uuid.getLeastSignificantBits();
        return Uuids.toIntArray(l, m);
    }

    private static int[] toIntArray(long uuidMost, long uuidLeast) {
        return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
    }

    public static byte[] toByteArray(UUID uuid) {
        byte[] bs = new byte[16];
        ByteBuffer.wrap(bs).order(ByteOrder.BIG_ENDIAN).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return bs;
    }

    public static UUID toUuid(Dynamic<?> dynamic) {
        int[] is = dynamic.asIntStream().toArray();
        if (is.length != 4) {
            throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + is.length + ".");
        }
        return Uuids.toUuid(is);
    }

    public static UUID getUuidFromProfile(GameProfile profile) {
        UUID uUID = profile.getId();
        if (uUID == null) {
            uUID = Uuids.getOfflinePlayerUuid(profile.getName());
        }
        return uUID;
    }

    public static UUID getOfflinePlayerUuid(String nickname) {
        return UUID.nameUUIDFromBytes((OFFLINE_PLAYER_UUID_PREFIX + nickname).getBytes(StandardCharsets.UTF_8));
    }
}

