/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DefaultClientResourcePack
extends DefaultResourcePack {
    private final ResourceIndex index;

    public DefaultClientResourcePack(PackResourceMetadata metadata, ResourceIndex index) {
        super(metadata, "minecraft", "realms");
        this.index = index;
    }

    @Override
    @Nullable
    protected InputStream findInputStream(ResourceType type, Identifier id) {
        File file;
        if (type == ResourceType.CLIENT_RESOURCES && (file = this.index.getResource(id)) != null && file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.findInputStream(type, id);
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        File file;
        if (type == ResourceType.CLIENT_RESOURCES && (file = this.index.getResource(id)) != null && file.exists()) {
            return true;
        }
        return super.contains(type, id);
    }

    @Override
    @Nullable
    protected InputStream getInputStream(String path) {
        File file = this.index.findFile(path);
        if (file != null && file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.getInputStream(path);
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, Predicate<Identifier> allowedPathPredicate) {
        Collection<Identifier> collection = super.findResources(type, namespace, prefix, allowedPathPredicate);
        collection.addAll(this.index.getFilesRecursively(prefix, namespace, allowedPathPredicate));
        return collection;
    }
}

