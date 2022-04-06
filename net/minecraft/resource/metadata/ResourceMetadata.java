/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

public interface ResourceMetadata {
    public static final ResourceMetadata NONE = new ResourceMetadata(){

        @Override
        public <T> Optional<T> decode(ResourceMetadataReader<T> reader) {
            return Optional.empty();
        }
    };

    public static ResourceMetadata create(InputStream stream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));){
            final JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
            ResourceMetadata resourceMetadata = new ResourceMetadata(){

                @Override
                public <T> Optional<T> decode(ResourceMetadataReader<T> reader) {
                    String string = reader.getKey();
                    return jsonObject.has(string) ? Optional.of(reader.fromJson(JsonHelper.getObject(jsonObject, string))) : Optional.empty();
                }
            };
            return resourceMetadata;
        }
    }

    public <T> Optional<T> decode(ResourceMetadataReader<T> var1);
}

