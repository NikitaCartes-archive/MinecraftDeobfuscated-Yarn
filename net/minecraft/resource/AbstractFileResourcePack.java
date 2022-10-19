/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class AbstractFileResourcePack
implements ResourcePack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;

    protected AbstractFileResourcePack(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        InputSupplier<InputStream> inputSupplier = this.openRoot("pack.mcmeta");
        if (inputSupplier == null) {
            return null;
        }
        try (InputStream inputStream = inputSupplier.get();){
            T t = AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
            return t;
        }
    }

    @Nullable
    public static <T> T parseMetadata(ResourceMetadataReader<T> metaReader, InputStream inputStream) {
        JsonObject jsonObject;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
            jsonObject = JsonHelper.deserialize(bufferedReader);
        } catch (Exception exception) {
            LOGGER.error("Couldn't load {} metadata", (Object)metaReader.getKey(), (Object)exception);
            return null;
        }
        if (!jsonObject.has(metaReader.getKey())) {
            return null;
        }
        try {
            return metaReader.fromJson(JsonHelper.getObject(jsonObject, metaReader.getKey()));
        } catch (Exception exception) {
            LOGGER.error("Couldn't load {} metadata", (Object)metaReader.getKey(), (Object)exception);
            return null;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}

