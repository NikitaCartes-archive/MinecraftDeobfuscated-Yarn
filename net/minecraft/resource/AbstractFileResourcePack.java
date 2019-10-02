/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFileResourcePack
implements ResourcePack {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final File base;

    public AbstractFileResourcePack(File file) {
        this.base = file;
    }

    private static String getFilename(ResourceType resourceType, Identifier identifier) {
        return String.format("%s/%s/%s", resourceType.getDirectory(), identifier.getNamespace(), identifier.getPath());
    }

    protected static String relativize(File file, File file2) {
        return file.toURI().relativize(file2.toURI()).getPath();
    }

    @Override
    public InputStream open(ResourceType resourceType, Identifier identifier) throws IOException {
        return this.openFile(AbstractFileResourcePack.getFilename(resourceType, identifier));
    }

    @Override
    public boolean contains(ResourceType resourceType, Identifier identifier) {
        return this.containsFile(AbstractFileResourcePack.getFilename(resourceType, identifier));
    }

    protected abstract InputStream openFile(String var1) throws IOException;

    @Override
    @Environment(value=EnvType.CLIENT)
    public InputStream openRoot(String string) throws IOException {
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        return this.openFile(string);
    }

    protected abstract boolean containsFile(String var1);

    protected void warnNonLowercaseNamespace(String string) {
        LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", (Object)string, (Object)this.base);
    }

    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> resourceMetadataReader) throws IOException {
        try (InputStream inputStream = this.openFile("pack.mcmeta");){
            T t = AbstractFileResourcePack.parseMetadata(resourceMetadataReader, inputStream);
            return t;
        }
    }

    @Nullable
    public static <T> T parseMetadata(ResourceMetadataReader<T> resourceMetadataReader, InputStream inputStream) {
        JsonObject jsonObject;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
            jsonObject = JsonHelper.deserialize(bufferedReader);
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Couldn't load {} metadata", (Object)resourceMetadataReader.getKey(), (Object)exception);
            return null;
        }
        if (!jsonObject.has(resourceMetadataReader.getKey())) {
            return null;
        }
        try {
            return resourceMetadataReader.fromJson(JsonHelper.getObject(jsonObject, resourceMetadataReader.getKey()));
        } catch (JsonParseException jsonParseException) {
            LOGGER.error("Couldn't load {} metadata", (Object)resourceMetadataReader.getKey(), (Object)jsonParseException);
            return null;
        }
    }

    @Override
    public String getName() {
        return this.base.getName();
    }
}

