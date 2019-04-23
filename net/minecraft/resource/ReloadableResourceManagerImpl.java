/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ProfilingResourceReloader;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl
implements ReloadableResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.newHashMap();
    private final List<ResourceReloadListener> listeners = Lists.newArrayList();
    private final List<ResourceReloadListener> initialListeners = Lists.newArrayList();
    private final Set<String> namespaces = Sets.newLinkedHashSet();
    private final ResourceType type;
    private final Thread mainThread;

    public ReloadableResourceManagerImpl(ResourceType resourceType, Thread thread) {
        this.type = resourceType;
        this.mainThread = thread;
    }

    @Override
    public void addPack(ResourcePack resourcePack) {
        for (String string : resourcePack.getNamespaces(this.type)) {
            this.namespaces.add(string);
            NamespaceResourceManager namespaceResourceManager = this.namespaceManagers.get(string);
            if (namespaceResourceManager == null) {
                namespaceResourceManager = new NamespaceResourceManager(this.type);
                this.namespaceManagers.put(string, namespaceResourceManager);
            }
            namespaceResourceManager.addPack(resourcePack);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces() {
        return this.namespaces;
    }

    @Override
    public Resource getResource(Identifier identifier) throws IOException {
        ResourceManager resourceManager = this.namespaceManagers.get(identifier.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getResource(identifier);
        }
        throw new FileNotFoundException(identifier.toString());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier identifier) {
        ResourceManager resourceManager = this.namespaceManagers.get(identifier.getNamespace());
        if (resourceManager != null) {
            return resourceManager.containsResource(identifier);
        }
        return false;
    }

    @Override
    public List<Resource> getAllResources(Identifier identifier) throws IOException {
        ResourceManager resourceManager = this.namespaceManagers.get(identifier.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getAllResources(identifier);
        }
        throw new FileNotFoundException(identifier.toString());
    }

    @Override
    public Collection<Identifier> findResources(String string, Predicate<String> predicate) {
        HashSet<Identifier> set = Sets.newHashSet();
        for (NamespaceResourceManager namespaceResourceManager : this.namespaceManagers.values()) {
            set.addAll(namespaceResourceManager.findResources(string, predicate));
        }
        ArrayList<Identifier> list = Lists.newArrayList(set);
        Collections.sort(list);
        return list;
    }

    private void clear() {
        this.namespaceManagers.clear();
        this.namespaces.clear();
    }

    @Override
    public CompletableFuture<Unit> beginReload(Executor executor, Executor executor2, List<ResourcePack> list, CompletableFuture<Unit> completableFuture) {
        ResourceReloadMonitor resourceReloadMonitor = this.beginMonitoredReload(executor, executor2, completableFuture, list);
        return resourceReloadMonitor.whenComplete();
    }

    @Override
    public void registerListener(ResourceReloadListener resourceReloadListener) {
        this.listeners.add(resourceReloadListener);
        this.initialListeners.add(resourceReloadListener);
    }

    protected ResourceReloadMonitor beginReloadInner(Executor executor, Executor executor2, List<ResourceReloadListener> list, CompletableFuture<Unit> completableFuture) {
        ProfilingResourceReloader resourceReloadMonitor = LOGGER.isDebugEnabled() ? new ProfilingResourceReloader(this, new ArrayList<ResourceReloadListener>(list), executor, executor2, completableFuture) : ResourceReloader.create(this, new ArrayList<ResourceReloadListener>(list), executor, executor2, completableFuture);
        this.initialListeners.clear();
        return resourceReloadMonitor;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ResourceReloadMonitor beginInitialMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture) {
        return this.beginReloadInner(executor, executor2, this.initialListeners, completableFuture);
    }

    @Override
    public ResourceReloadMonitor beginMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<ResourcePack> list) {
        this.clear();
        LOGGER.info("Reloading ResourceManager: {}", (Object)list.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));
        for (ResourcePack resourcePack : list) {
            this.addPack(resourcePack);
        }
        return this.beginReloadInner(executor, executor2, this.listeners, completableFuture);
    }
}

