/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;

public interface ResourceMetadataReader<T> {
    public String getKey();

    public T fromJson(JsonObject var1);
}

