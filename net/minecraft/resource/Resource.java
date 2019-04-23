/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.io.Closeable;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface Resource
extends Closeable {
    @Environment(value=EnvType.CLIENT)
    public Identifier getId();

    public InputStream getInputStream();

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public <T> T getMetadata(ResourceMetadataReader<T> var1);

    public String getResourcePackName();
}

