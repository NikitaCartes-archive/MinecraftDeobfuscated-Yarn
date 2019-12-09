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

    public AbstractFileResourcePack(File base) {
        this.base = base;
    }

    private static String getFilename(ResourceType type, Identifier id) {
        return String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
    }

    protected static String relativize(File base, File target) {
        return base.toURI().relativize(target.toURI()).getPath();
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        return this.openFile(AbstractFileResourcePack.getFilename(type, id));
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        return this.containsFile(AbstractFileResourcePack.getFilename(type, id));
    }

    protected abstract InputStream openFile(String var1) throws IOException;

    @Override
    @Environment(value=EnvType.CLIENT)
    public InputStream openRoot(String fileName) throws IOException {
        if (fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        return this.openFile(fileName);
    }

    protected abstract boolean containsFile(String var1);

    protected void warnNonLowercaseNamespace(String namespace) {
        LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", (Object)namespace, (Object)this.base);
    }

    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        try (InputStream inputStream = this.openFile("pack.mcmeta");){
            T t = AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
            return t;
        }
    }

    @Nullable
    public static <T> T parseMetadata(ResourceMetadataReader<T> metaReader, InputStream inputStream) {
        JsonObject jsonObject;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
            jsonObject = JsonHelper.deserialize(bufferedReader);
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Couldn't load {} metadata", (Object)metaReader.getKey(), (Object)exception);
            return null;
        }
        if (!jsonObject.has(metaReader.getKey())) {
            return null;
        }
        try {
            return metaReader.fromJson(JsonHelper.getObject(jsonObject, metaReader.getKey()));
        } catch (JsonParseException jsonParseException) {
            LOGGER.error("Couldn't load {} metadata", (Object)metaReader.getKey(), (Object)jsonParseException);
            return null;
        }
    }

    @Override
    public String getName() {
        return this.base.getName();
    }
}

