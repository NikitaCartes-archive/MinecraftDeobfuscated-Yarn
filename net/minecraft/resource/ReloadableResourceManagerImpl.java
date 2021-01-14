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
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ProfiledResourceReload;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl
implements ReloadableResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.newHashMap();
    private final List<ResourceReloader> reloaders = Lists.newArrayList();
    private final List<ResourceReloader> initialListeners = Lists.newArrayList();
    private final Set<String> namespaces = Sets.newLinkedHashSet();
    private final List<ResourcePack> field_25145 = Lists.newArrayList();
    private final ResourceType type;

    public ReloadableResourceManagerImpl(ResourceType type) {
        this.type = type;
    }

    public void addPack(ResourcePack pack) {
        this.field_25145.add(pack);
        for (String string : pack.getNamespaces(this.type)) {
            this.namespaces.add(string);
            NamespaceResourceManager namespaceResourceManager = this.namespaceManagers.get(string);
            if (namespaceResourceManager == null) {
                namespaceResourceManager = new NamespaceResourceManager(this.type, string);
                this.namespaceManagers.put(string, namespaceResourceManager);
            }
            namespaceResourceManager.addPack(pack);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces() {
        return this.namespaces;
    }

    @Override
    public Resource getResource(Identifier id) throws IOException {
        ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getResource(id);
        }
        throw new FileNotFoundException(id.toString());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier id) {
        ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.containsResource(id);
        }
        return false;
    }

    @Override
    public List<Resource> getAllResources(Identifier id) throws IOException {
        ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
        if (resourceManager != null) {
            return resourceManager.getAllResources(id);
        }
        throw new FileNotFoundException(id.toString());
    }

    @Override
    public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
        HashSet<Identifier> set = Sets.newHashSet();
        for (NamespaceResourceManager namespaceResourceManager : this.namespaceManagers.values()) {
            set.addAll(namespaceResourceManager.findResources(startingPath, pathPredicate));
        }
        ArrayList<Identifier> list = Lists.newArrayList(set);
        Collections.sort(list);
        return list;
    }

    private void clear() {
        this.namespaceManagers.clear();
        this.namespaces.clear();
        this.field_25145.forEach(ResourcePack::close);
        this.field_25145.clear();
    }

    @Override
    public void close() {
        this.clear();
    }

    @Override
    public void registerReloader(ResourceReloader reloader) {
        this.reloaders.add(reloader);
        this.initialListeners.add(reloader);
    }

    protected ResourceReload beginReloadInner(Executor prepareExecutor, Executor applyExecutor, List<ResourceReloader> listeners, CompletableFuture<Unit> initialStage) {
        ProfiledResourceReload resourceReload = LOGGER.isDebugEnabled() ? new ProfiledResourceReload(this, Lists.newArrayList(listeners), prepareExecutor, applyExecutor, initialStage) : SimpleResourceReload.create(this, Lists.newArrayList(listeners), prepareExecutor, applyExecutor, initialStage);
        this.initialListeners.clear();
        return resourceReload;
    }

    @Override
    public ResourceReload reload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs) {
        this.clear();
        LOGGER.info("Reloading ResourceManager: {}", () -> packs.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));
        for (ResourcePack resourcePack : packs) {
            try {
                this.addPack(resourcePack);
            } catch (Exception exception) {
                LOGGER.error("Failed to add resource pack {}", (Object)resourcePack.getName(), (Object)exception);
                return new FailedResourceReloadMonitor(new PackAdditionFailedException(resourcePack, (Throwable)exception));
            }
        }
        return this.beginReloadInner(prepareExecutor, applyExecutor, this.reloaders, initialStage);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Stream<ResourcePack> streamResourcePacks() {
        return this.field_25145.stream();
    }

    static class FailedResourceReloadMonitor
    implements ResourceReload {
        private final PackAdditionFailedException exception;
        private final CompletableFuture<Unit> future;

        public FailedResourceReloadMonitor(PackAdditionFailedException exception) {
            this.exception = exception;
            this.future = new CompletableFuture();
            this.future.completeExceptionally(exception);
        }

        @Override
        public CompletableFuture<Unit> whenComplete() {
            return this.future;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public float getProgress() {
            return 0.0f;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean isPrepareStageComplete() {
            return false;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean isComplete() {
            return true;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void throwException() {
            throw this.exception;
        }
    }

    public static class PackAdditionFailedException
    extends RuntimeException {
        private final ResourcePack pack;

        public PackAdditionFailedException(ResourcePack pack, Throwable cause) {
            super(pack.getName(), cause);
            this.pack = pack;
        }

        @Environment(value=EnvType.CLIENT)
        public ResourcePack getPack() {
            return this.pack;
        }
    }
}

