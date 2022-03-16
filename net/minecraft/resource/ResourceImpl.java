/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public class ResourceImpl
implements Resource {
    private final String packName;
    private final Identifier id;
    private final InputStream inputStream;
    @Nullable
    private InputStream metaInputStream;
    @Nullable
    private JsonObject metadata;

    public ResourceImpl(String packName, Identifier id, InputStream inputStream, @Nullable InputStream metaInputStream) {
        this.packName = packName;
        this.id = id;
        this.inputStream = inputStream;
        this.metaInputStream = metaInputStream;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public boolean hasMetadata() {
        return this.metadata != null || this.metaInputStream != null;
    }

    @Override
    @Nullable
    public <T> T getMetadata(ResourceMetadataReader<T> metaReader) {
        block3: {
            if (this.metadata != null || this.metaInputStream == null) break block3;
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(this.metaInputStream, StandardCharsets.UTF_8));
                this.metadata = JsonHelper.deserialize(bufferedReader);
            } catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedReader);
                throw throwable;
            }
            IOUtils.closeQuietly(bufferedReader);
            this.metaInputStream = null;
        }
        if (this.metadata == null) {
            return null;
        }
        String string = metaReader.getKey();
        return this.metadata.has(string) ? (T)metaReader.fromJson(JsonHelper.getObject(this.metadata, string)) : null;
    }

    @Override
    public String getResourcePackName() {
        return this.packName;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
        if (this.metaInputStream != null) {
            this.metaInputStream.close();
        }
    }
}

