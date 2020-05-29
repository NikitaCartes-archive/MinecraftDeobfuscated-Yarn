/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface JsonSerializable<T> {
    public void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

    public T fromJson(JsonObject var1, JsonDeserializationContext var2);
}

