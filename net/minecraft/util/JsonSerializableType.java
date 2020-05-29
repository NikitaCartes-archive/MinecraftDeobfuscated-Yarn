/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.util.JsonSerializable;

public class JsonSerializableType<T> {
    private final JsonSerializable<? extends T> jsonSerializer;

    public JsonSerializableType(JsonSerializable<? extends T> jsonSerializable) {
        this.jsonSerializer = jsonSerializable;
    }

    public JsonSerializable<? extends T> getJsonSerializer() {
        return this.jsonSerializer;
    }
}

