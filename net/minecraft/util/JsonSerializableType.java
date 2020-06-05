/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.util.JsonSerializer;

public class JsonSerializableType<T> {
    private final JsonSerializer<? extends T> jsonSerializer;

    public JsonSerializableType(JsonSerializer<? extends T> jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    public JsonSerializer<? extends T> getJsonSerializer() {
        return this.jsonSerializer;
    }
}

