/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.UUID;
import net.minecraft.util.DynamicSerializable;

public final class DynamicSerializableUuid
implements DynamicSerializable {
    private final UUID uuid;

    public DynamicSerializableUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return ops.createString(this.uuid.toString());
    }

    public static DynamicSerializableUuid of(Dynamic<?> dynamic) {
        String string = dynamic.asString().orElseThrow(() -> new IllegalArgumentException("Could not parse UUID"));
        return new DynamicSerializableUuid(UUID.fromString(string));
    }

    public String toString() {
        return this.uuid.toString();
    }
}

