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
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DefaultClientResourcePack
extends DefaultResourcePack {
    private final ResourceIndex index;

    public DefaultClientResourcePack(ResourceIndex resourceIndex) {
        super("minecraft", "realms");
        this.index = resourceIndex;
    }

    @Override
    @Nullable
    protected InputStream findInputStream(ResourceType resourceType, Identifier identifier) {
        File file;
        if (resourceType == ResourceType.CLIENT_RESOURCES && (file = this.index.getResource(identifier)) != null && file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.findInputStream(resourceType, identifier);
    }

    @Override
    @Nullable
    protected InputStream getInputStream(String string) {
        File file = this.index.findFile(string);
        if (file != null && file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.getInputStream(string);
    }

    @Override
    public Collection<Identifier> findResources(ResourceType resourceType, String string, int i, Predicate<String> predicate) {
        Collection<Identifier> collection = super.findResources(resourceType, string, i, predicate);
        collection.addAll(this.index.getFilesRecursively(string, i, predicate).stream().map(Identifier::new).collect(Collectors.toList()));
        return collection;
    }
}

