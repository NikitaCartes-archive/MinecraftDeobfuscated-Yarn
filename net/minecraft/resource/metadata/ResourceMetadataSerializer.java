/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.metadata.ResourceMetadataReader;

public interface ResourceMetadataSerializer<T>
extends ResourceMetadataReader<T> {
    public JsonObject toJson(T var1);

    public static <T> ResourceMetadataSerializer<T> fromCodec(final String key, final Codec<T> codec) {
        return new ResourceMetadataSerializer<T>(){

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public T fromJson(JsonObject json) {
                return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, error -> {});
            }

            @Override
            public JsonObject toJson(T metadata) {
                return codec.encodeStart(JsonOps.INSTANCE, metadata).getOrThrow(false, error -> {}).getAsJsonObject();
            }
        };
    }
}

