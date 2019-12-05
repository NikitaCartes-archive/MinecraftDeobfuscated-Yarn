/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamespaceResourceManager
implements ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final List<ResourcePack> packList = Lists.newArrayList();
    private final ResourceType type;
    private final String namespace;

    public NamespaceResourceManager(ResourceType resourceType, String string) {
        this.type = resourceType;
        this.namespace = string;
    }

    public void addPack(ResourcePack resourcePack) {
        this.packList.add(resourcePack);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces() {
        return ImmutableSet.of(this.namespace);
    }

    @Override
    public Resource getResource(Identifier identifier) throws IOException {
        this.validate(identifier);
        ResourcePack resourcePack = null;
        Identifier identifier2 = NamespaceResourceManager.getMetadataPath(identifier);
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack resourcePack2 = this.packList.get(i);
            if (resourcePack == null && resourcePack2.contains(this.type, identifier2)) {
                resourcePack = resourcePack2;
            }
            if (!resourcePack2.contains(this.type, identifier)) continue;
            InputStream inputStream = null;
            if (resourcePack != null) {
                inputStream = this.open(identifier2, resourcePack);
            }
            return new ResourceImpl(resourcePack2.getName(), identifier, this.open(identifier, resourcePack2), inputStream);
        }
        throw new FileNotFoundException(identifier.toString());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier identifier) {
        if (!this.isPathAbsolute(identifier)) {
            return false;
        }
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack resourcePack = this.packList.get(i);
            if (!resourcePack.contains(this.type, identifier)) continue;
            return true;
        }
        return false;
    }

    protected InputStream open(Identifier identifier, ResourcePack resourcePack) throws IOException {
        InputStream inputStream = resourcePack.open(this.type, identifier);
        return LOGGER.isDebugEnabled() ? new DebugInputStream(inputStream, identifier, resourcePack.getName()) : inputStream;
    }

    private void validate(Identifier identifier) throws IOException {
        if (!this.isPathAbsolute(identifier)) {
            throw new IOException("Invalid relative path to resource: " + identifier);
        }
    }

    private boolean isPathAbsolute(Identifier identifier) {
        return !identifier.getPath().contains("..");
    }

    @Override
    public List<Resource> getAllResources(Identifier identifier) throws IOException {
        this.validate(identifier);
        ArrayList<Resource> list = Lists.newArrayList();
        Identifier identifier2 = NamespaceResourceManager.getMetadataPath(identifier);
        for (ResourcePack resourcePack : this.packList) {
            if (!resourcePack.contains(this.type, identifier)) continue;
            InputStream inputStream = resourcePack.contains(this.type, identifier2) ? this.open(identifier2, resourcePack) : null;
            list.add(new ResourceImpl(resourcePack.getName(), identifier, this.open(identifier, resourcePack), inputStream));
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(identifier.toString());
        }
        return list;
    }

    @Override
    public Collection<Identifier> findResources(String string, Predicate<String> predicate) {
        ArrayList<Identifier> list = Lists.newArrayList();
        for (ResourcePack resourcePack : this.packList) {
            list.addAll(resourcePack.findResources(this.type, this.namespace, string, Integer.MAX_VALUE, predicate));
        }
        Collections.sort(list);
        return list;
    }

    static Identifier getMetadataPath(Identifier identifier) {
        return new Identifier(identifier.getNamespace(), identifier.getPath() + ".mcmeta");
    }

    static class DebugInputStream
    extends FilterInputStream {
        private final String leakMessage;
        private boolean closed;

        public DebugInputStream(InputStream inputStream, Identifier identifier, String string) {
            super(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
            this.leakMessage = "Leaked resource: '" + identifier + "' loaded from pack: '" + string + "'\n" + byteArrayOutputStream;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                LOGGER.warn(this.leakMessage);
            }
            super.finalize();
        }
    }
}

