/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

/**
 * A basic implementation of resource manager with a lifecycle.
 * 
 * <p>It handles resources by namespaces, hoping that most namespaces are
 * defined in only few resource packs.
 * 
 * @see NamespaceResourceManager
 */
public class LifecycledResourceManagerImpl
implements LifecycledResourceManager {
    private final Map<String, NamespaceResourceManager> subManagers;
    private final List<ResourcePack> packs;

    public LifecycledResourceManagerImpl(ResourceType type, List<ResourcePack> packs) {
        this.packs = List.copyOf(packs);
        HashMap<String, NamespaceResourceManager> map = new HashMap<String, NamespaceResourceManager>();
        for (ResourcePack resourcePack : packs) {
            for (String string : resourcePack.getNamespaces(type)) {
                map.computeIfAbsent(string, namespace -> new NamespaceResourceManager(type, (String)namespace)).addPack(resourcePack);
            }
        }
        this.subManagers = map;
    }

    @Override
    public Set<String> getAllNamespaces() {
        return this.subManagers.keySet();
    }

    @Override
    public Resource getResource(Identifier identifier) throws IOException {
        ResourceManager resourceManager = this.subManagers.get(identifier.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getResource(identifier);
        }
        throw new FileNotFoundException(identifier.toString());
    }

    @Override
    public boolean containsResource(Identifier id) {
        ResourceManager resourceManager = this.subManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.containsResource(id);
        }
        return false;
    }

    @Override
    public List<Resource> getAllResources(Identifier id) throws IOException {
        ResourceManager resourceManager = this.subManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getAllResources(id);
        }
        throw new FileNotFoundException(id.toString());
    }

    @Override
    public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
        HashSet<Identifier> set = Sets.newHashSet();
        for (NamespaceResourceManager namespaceResourceManager : this.subManagers.values()) {
            set.addAll(namespaceResourceManager.findResources(startingPath, pathPredicate));
        }
        ArrayList<Identifier> list = Lists.newArrayList(set);
        Collections.sort(list);
        return list;
    }

    @Override
    public Stream<ResourcePack> streamResourcePacks() {
        return this.packs.stream();
    }

    @Override
    public void close() {
        this.packs.forEach(ResourcePack::close);
    }
}

