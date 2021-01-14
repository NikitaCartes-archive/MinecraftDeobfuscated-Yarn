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

/**
 * A resource of binary data.
 * 
 * <p>The resource must be closed before disposal to avoid resource leaks.
 * 
 * @see ResourceManager#getResource(Identifier)
 * @see ResourceManager#getAllResources(Identifier)
 */
public interface Resource
extends Closeable {
    /**
     * Returns the location of this resource.
     * 
     * <p>Within each resource pack, this location is a unique identifer for a
     * resource; however, in a resource manager, there may be multiple resources
     * with the same location available.
     */
    @Environment(value=EnvType.CLIENT)
    public Identifier getId();

    /**
     * Returns the input stream of this resource.
     * 
     * <p>This input stream is closed when this resource is closed.
     */
    public InputStream getInputStream();

    /**
     * Returns a metadata of this resource by the {@code metaReader}, or {@code null}
     * if no such metadata exists.
     * 
     * @param metaReader the metadata reader
     */
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public <T> T getMetadata(ResourceMetadataReader<T> var1);

    /**
     * Returns the user-friendly name of the pack this resource is from.
     */
    public String getResourcePackName();
}

