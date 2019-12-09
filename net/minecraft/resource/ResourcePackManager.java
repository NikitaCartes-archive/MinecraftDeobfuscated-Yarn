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
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import org.jetbrains.annotations.Nullable;

public class ResourcePackManager<T extends ResourcePackProfile>
implements AutoCloseable {
    private final Set<ResourcePackProvider> providers = Sets.newHashSet();
    private final Map<String, T> profiles = Maps.newLinkedHashMap();
    private final List<T> enabled = Lists.newLinkedList();
    private final ResourcePackProfile.Factory<T> profileFactory;

    public ResourcePackManager(ResourcePackProfile.Factory<T> factory) {
        this.profileFactory = factory;
    }

    public void scanPacks() {
        this.close();
        Set set = this.enabled.stream().map(ResourcePackProfile::getName).collect(Collectors.toCollection(LinkedHashSet::new));
        this.profiles.clear();
        this.enabled.clear();
        for (ResourcePackProvider resourcePackProvider : this.providers) {
            resourcePackProvider.register(this.profiles, this.profileFactory);
        }
        this.sort();
        this.enabled.addAll(set.stream().map(this.profiles::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
        for (ResourcePackProfile resourcePackProfile : this.profiles.values()) {
            if (!resourcePackProfile.isAlwaysEnabled() || this.enabled.contains(resourcePackProfile)) continue;
            resourcePackProfile.getInitialPosition().insert(this.enabled, resourcePackProfile, Functions.identity(), false);
        }
    }

    private void sort() {
        ArrayList<Map.Entry<String, T>> list = Lists.newArrayList(this.profiles.entrySet());
        this.profiles.clear();
        list.stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> {
            ResourcePackProfile cfr_ignored_0 = (ResourcePackProfile)this.profiles.put((String)entry.getKey(), (T)entry.getValue());
        });
    }

    public void setEnabledProfiles(Collection<T> enabled) {
        this.enabled.clear();
        this.enabled.addAll(enabled);
        for (ResourcePackProfile resourcePackProfile : this.profiles.values()) {
            if (!resourcePackProfile.isAlwaysEnabled() || this.enabled.contains(resourcePackProfile)) continue;
            resourcePackProfile.getInitialPosition().insert(this.enabled, resourcePackProfile, Functions.identity(), false);
        }
    }

    public Collection<T> getProfiles() {
        return this.profiles.values();
    }

    public Collection<T> getDisabledProfiles() {
        ArrayList<T> collection = Lists.newArrayList(this.profiles.values());
        collection.removeAll(this.enabled);
        return collection;
    }

    public Collection<T> getEnabledProfiles() {
        return this.enabled;
    }

    @Nullable
    public T getProfile(String name) {
        return (T)((ResourcePackProfile)this.profiles.get(name));
    }

    public void registerProvider(ResourcePackProvider provider) {
        this.providers.add(provider);
    }

    @Override
    public void close() {
        this.profiles.values().forEach(ResourcePackProfile::close);
    }
}

