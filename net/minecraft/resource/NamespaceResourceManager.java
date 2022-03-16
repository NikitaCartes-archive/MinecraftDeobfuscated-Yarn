/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceRef;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * A resource manager that only loads resources for a specific namespace.
 * This is an implementation detail for {@link LifecycledResourceManagerImpl}, based
 * on the heuristic that most namespaces are only defined in few resource
 * packs, making loading in those namespaces faster.
 * 
 * @see LifecycledResourceManagerImpl
 */
public class NamespaceResourceManager
implements ResourceManager {
    static final Logger LOGGER = LogUtils.getLogger();
    protected final List<FilterablePack> packList = Lists.newArrayList();
    final ResourceType type;
    private final String namespace;

    public NamespaceResourceManager(ResourceType type, String namespace) {
        this.type = type;
        this.namespace = namespace;
    }

    public void addPack(ResourcePack pack) {
        this.addPack(pack.getName(), pack, null);
    }

    public void addPack(ResourcePack pack, Predicate<Identifier> filter) {
        this.addPack(pack.getName(), pack, filter);
    }

    public void addPack(String name, Predicate<Identifier> filter) {
        this.addPack(name, null, filter);
    }

    private void addPack(String name, @Nullable ResourcePack underlyingPack, @Nullable Predicate<Identifier> filter) {
        this.packList.add(new FilterablePack(name, underlyingPack, filter));
    }

    @Override
    public Set<String> getAllNamespaces() {
        return ImmutableSet.of(this.namespace);
    }

    @Override
    public Resource getResource(Identifier identifier) throws IOException {
        this.validate(identifier);
        ResourcePack resourcePack = null;
        Identifier identifier2 = NamespaceResourceManager.getMetadataPath(identifier);
        boolean bl = false;
        for (FilterablePack filterablePack : Lists.reverse(this.packList)) {
            ResourcePack resourcePack2 = filterablePack.underlying;
            if (resourcePack2 != null) {
                if (!bl) {
                    if (resourcePack2.contains(this.type, identifier2)) {
                        resourcePack = resourcePack2;
                        bl = true;
                    } else {
                        bl = filterablePack.isFiltered(identifier2);
                    }
                }
                if (resourcePack2.contains(this.type, identifier)) {
                    InputStream inputStream = null;
                    if (resourcePack != null) {
                        inputStream = this.open(identifier2, resourcePack);
                    }
                    return new ResourceImpl(resourcePack2.getName(), identifier, this.open(identifier, resourcePack2), inputStream);
                }
            }
            if (!filterablePack.isFiltered(identifier)) continue;
            throw new FileNotFoundException(identifier + " (filtered by: " + filterablePack.name + ")");
        }
        throw new FileNotFoundException(identifier.toString());
    }

    @Override
    public boolean containsResource(Identifier id) {
        if (!this.isPathAbsolute(id)) {
            return false;
        }
        for (FilterablePack filterablePack : Lists.reverse(this.packList)) {
            if (filterablePack.contains(this.type, id)) {
                return true;
            }
            if (!filterablePack.isFiltered(id)) continue;
            return false;
        }
        return false;
    }

    protected InputStream open(Identifier id, ResourcePack pack) throws IOException {
        InputStream inputStream = pack.open(this.type, id);
        return LOGGER.isDebugEnabled() ? new DebugInputStream(inputStream, id, pack.getName()) : inputStream;
    }

    private void validate(Identifier id) throws IOException {
        if (!this.isPathAbsolute(id)) {
            throw new IOException("Invalid relative path to resource: " + id);
        }
    }

    private boolean isPathAbsolute(Identifier id) {
        return !id.getPath().contains("..");
    }

    @Override
    public List<ResourceRef> getAllResources(Identifier id) throws IOException {
        this.validate(id);
        ArrayList<Entry> list = Lists.newArrayList();
        Identifier identifier = NamespaceResourceManager.getMetadataPath(id);
        String string = null;
        for (FilterablePack filterablePack : this.packList) {
            ResourcePack resourcePack;
            if (filterablePack.isFiltered(id)) {
                list.clear();
                string = filterablePack.name;
            } else if (filterablePack.isFiltered(identifier)) {
                list.forEach(Entry::ignoreMetadata);
            }
            if ((resourcePack = filterablePack.underlying) == null || !resourcePack.contains(this.type, id)) continue;
            list.add(new Entry(id, identifier, resourcePack));
        }
        if (list.isEmpty()) {
            if (string != null) {
                throw new FileNotFoundException(id + " (filtered by: " + string + ")");
            }
            throw new FileNotFoundException(id.toString());
        }
        return list.stream().map(Entry::toReference).toList();
    }

    @Override
    public Map<Identifier, ResourceRef> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        Object2IntOpenHashMap<Identifier> object2IntMap = new Object2IntOpenHashMap<Identifier>();
        int i = this.packList.size();
        for (int j = 0; j < i; ++j) {
            FilterablePack filterablePack = this.packList.get(j);
            filterablePack.removeFiltered(object2IntMap.keySet());
            if (filterablePack.underlying == null) continue;
            for (Identifier identifier : filterablePack.underlying.findResources(this.type, this.namespace, startingPath, allowedPathPredicate)) {
                object2IntMap.put(identifier, j);
            }
        }
        TreeMap<Identifier, ResourceRef> map = Maps.newTreeMap();
        for (Object2IntMap.Entry entry : Object2IntMaps.fastIterable(object2IntMap)) {
            int k = entry.getIntValue();
            Identifier identifier2 = (Identifier)entry.getKey();
            ResourcePack resourcePack = this.packList.get((int)k).underlying;
            String string = resourcePack.getName();
            map.put(identifier2, new ResourceRef(string, () -> {
                Identifier identifier2 = NamespaceResourceManager.getMetadataPath(identifier2);
                InputStream inputStream = null;
                for (int j = this.packList.size() - 1; j >= k; --j) {
                    FilterablePack filterablePack = this.packList.get(j);
                    ResourcePack resourcePack2 = filterablePack.underlying;
                    if (resourcePack2 != null && resourcePack2.contains(this.type, identifier2)) {
                        inputStream = this.open(identifier2, resourcePack2);
                        break;
                    }
                    if (filterablePack.isFiltered(identifier2)) break;
                }
                return new ResourceImpl(string, identifier2, this.open(identifier2, resourcePack), inputStream);
            }));
        }
        return map;
    }

    private static void applyFilter(FilterablePack pack, Map<Identifier, EntryList> idToEntryList) {
        Iterator<Map.Entry<Identifier, EntryList>> iterator = idToEntryList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Identifier, EntryList> entry = iterator.next();
            Identifier identifier = entry.getKey();
            EntryList entryList = entry.getValue();
            if (pack.isFiltered(identifier)) {
                iterator.remove();
                continue;
            }
            if (!pack.isFiltered(entryList.metadataId())) continue;
            entryList.entries.forEach(Entry::ignoreMetadata);
        }
    }

    private void findAndAdd(FilterablePack pack, String startingPath, Predicate<Identifier> allowedPathPredicate, Map<Identifier, EntryList> idToEntryList) {
        ResourcePack resourcePack = pack.underlying;
        if (resourcePack == null) {
            return;
        }
        for (Identifier identifier : resourcePack.findResources(this.type, this.namespace, startingPath, allowedPathPredicate)) {
            Identifier identifier2 = NamespaceResourceManager.getMetadataPath(identifier);
            idToEntryList.computeIfAbsent(identifier, id -> new EntryList(identifier2, Lists.newArrayList())).entries().add(new Entry(identifier, identifier2, resourcePack));
        }
    }

    @Override
    public Map<Identifier, List<ResourceRef>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
        HashMap<Identifier, EntryList> map = Maps.newHashMap();
        for (FilterablePack filterablePack : this.packList) {
            NamespaceResourceManager.applyFilter(filterablePack, map);
            this.findAndAdd(filterablePack, startingPath, allowedPathPredicate, map);
        }
        TreeMap<Identifier, List<ResourceRef>> treeMap = Maps.newTreeMap();
        map.forEach((id, entryList) -> treeMap.put((Identifier)id, entryList.toReferenceList()));
        return treeMap;
    }

    @Override
    public Stream<ResourcePack> streamResourcePacks() {
        return this.packList.stream().map(pack -> pack.underlying).filter(Objects::nonNull);
    }

    static Identifier getMetadataPath(Identifier id) {
        return new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
    }

    record FilterablePack(String name, @Nullable ResourcePack underlying, @Nullable Predicate<Identifier> filter) {
        public void removeFiltered(Collection<Identifier> ids) {
            if (this.filter != null) {
                ids.removeIf(this.filter);
            }
        }

        public boolean isFiltered(Identifier id) {
            return this.filter != null && this.filter.test(id);
        }

        boolean contains(ResourceType type, Identifier id) {
            return this.underlying != null && this.underlying.contains(type, id);
        }

        @Nullable
        public ResourcePack underlying() {
            return this.underlying;
        }

        @Nullable
        public Predicate<Identifier> filter() {
            return this.filter;
        }
    }

    static class DebugInputStream
    extends FilterInputStream {
        private final String leakMessage;
        private boolean closed;

        public DebugInputStream(InputStream parent, Identifier id, String packName) {
            super(parent);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
            this.leakMessage = "Leaked resource: '" + id + "' loaded from pack: '" + packName + "'\n" + byteArrayOutputStream;
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

    class Entry {
        private final Identifier id;
        private final Identifier metadataId;
        private final ResourcePack pack;
        private boolean checksMetadata = true;

        Entry(Identifier id, Identifier metadataId, ResourcePack pack) {
            this.pack = pack;
            this.id = id;
            this.metadataId = metadataId;
        }

        public void ignoreMetadata() {
            this.checksMetadata = false;
        }

        public ResourceRef toReference() {
            String string = this.pack.getName();
            if (this.checksMetadata) {
                return new ResourceRef(string, () -> {
                    InputStream inputStream = this.pack.contains(NamespaceResourceManager.this.type, this.metadataId) ? NamespaceResourceManager.this.open(this.metadataId, this.pack) : null;
                    return new ResourceImpl(string, this.id, NamespaceResourceManager.this.open(this.id, this.pack), inputStream);
                });
            }
            return new ResourceRef(string, () -> new ResourceImpl(string, this.id, NamespaceResourceManager.this.open(this.id, this.pack), null));
        }
    }

    record EntryList(Identifier metadataId, List<Entry> entries) {
        List<ResourceRef> toReferenceList() {
            return this.entries().stream().map(Entry::toReference).toList();
        }
    }
}

