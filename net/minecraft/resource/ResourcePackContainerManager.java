/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;
import org.jetbrains.annotations.Nullable;

public class ResourcePackContainerManager<T extends ResourcePackContainer>
implements AutoCloseable {
    private final Set<ResourcePackCreator> creators = Sets.newHashSet();
    private final Map<String, T> nameToContainer = Maps.newLinkedHashMap();
    private final List<T> enabledContainers = Lists.newLinkedList();
    private final ResourcePackContainer.Factory<T> factory;

    public ResourcePackContainerManager(ResourcePackContainer.Factory<T> factory) {
        this.factory = factory;
    }

    public void callCreators() {
        this.close();
        Set set = this.enabledContainers.stream().map(ResourcePackContainer::getName).collect(Collectors.toCollection(LinkedHashSet::new));
        this.nameToContainer.clear();
        this.enabledContainers.clear();
        for (ResourcePackCreator resourcePackCreator : this.creators) {
            resourcePackCreator.registerContainer(this.nameToContainer, this.factory);
        }
        this.sortMapByName();
        this.enabledContainers.addAll(set.stream().map(this.nameToContainer::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
        for (ResourcePackContainer resourcePackContainer : this.nameToContainer.values()) {
            if (!resourcePackContainer.canBeSorted() || this.enabledContainers.contains(resourcePackContainer)) continue;
            resourcePackContainer.getInitialPosition().insert(this.enabledContainers, resourcePackContainer, Functions.identity(), false);
        }
    }

    private void sortMapByName() {
        ArrayList<Map.Entry<String, T>> list = Lists.newArrayList(this.nameToContainer.entrySet());
        this.nameToContainer.clear();
        list.stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> {
            ResourcePackContainer cfr_ignored_0 = (ResourcePackContainer)this.nameToContainer.put((String)entry.getKey(), (T)entry.getValue());
        });
    }

    public void setEnabled(Collection<T> collection) {
        this.enabledContainers.clear();
        this.enabledContainers.addAll(collection);
        for (ResourcePackContainer resourcePackContainer : this.nameToContainer.values()) {
            if (!resourcePackContainer.canBeSorted() || this.enabledContainers.contains(resourcePackContainer)) continue;
            resourcePackContainer.getInitialPosition().insert(this.enabledContainers, resourcePackContainer, Functions.identity(), false);
        }
    }

    public Collection<T> getAlphabeticallyOrderedContainers() {
        return this.nameToContainer.values();
    }

    public Collection<T> getDisabledContainers() {
        ArrayList<T> collection = Lists.newArrayList(this.nameToContainer.values());
        collection.removeAll(this.enabledContainers);
        return collection;
    }

    public Collection<T> getEnabledContainers() {
        return this.enabledContainers;
    }

    @Nullable
    public T getContainer(String string) {
        return (T)((ResourcePackContainer)this.nameToContainer.get(string));
    }

    public void addCreator(ResourcePackCreator resourcePackCreator) {
        this.creators.add(resourcePackCreator);
    }

    @Override
    public void close() {
        this.nameToContainer.values().forEach(ResourcePackContainer::close);
    }
}

