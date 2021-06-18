/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * A resource pack manager manages a list of {@link ResourcePackProfile}s and
 * builds {@linkplain #createResourcePacks() a list of resource packs} when the
 * resource manager reloads.
 */
public class ResourcePackManager
implements AutoCloseable {
    private final Set<ResourcePackProvider> providers;
    private Map<String, ResourcePackProfile> profiles = ImmutableMap.of();
    private List<ResourcePackProfile> enabled = ImmutableList.of();
    private final ResourcePackProfile.Factory profileFactory;

    public ResourcePackManager(ResourcePackProfile.Factory profileFactory, ResourcePackProvider ... providers) {
        this.profileFactory = profileFactory;
        this.providers = ImmutableSet.copyOf(providers);
    }

    public ResourcePackManager(ResourceType type, ResourcePackProvider ... providers) {
        this((String name, Text displayName, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, PackResourceMetadata metadata, ResourcePackProfile.InsertionPosition direction, ResourcePackSource source) -> new ResourcePackProfile(name, displayName, alwaysEnabled, packFactory, metadata, type, direction, source), providers);
    }

    public void scanPacks() {
        List list = this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
        this.close();
        this.profiles = this.providePackProfiles();
        this.enabled = this.buildEnabledProfiles(list);
    }

    private Map<String, ResourcePackProfile> providePackProfiles() {
        TreeMap map = Maps.newTreeMap();
        for (ResourcePackProvider resourcePackProvider : this.providers) {
            resourcePackProvider.register(profile -> map.put(profile.getName(), profile), this.profileFactory);
        }
        return ImmutableMap.copyOf(map);
    }

    public void setEnabledProfiles(Collection<String> enabled) {
        this.enabled = this.buildEnabledProfiles(enabled);
    }

    private List<ResourcePackProfile> buildEnabledProfiles(Collection<String> enabledNames) {
        List list = this.streamProfilesByName(enabledNames).collect(Collectors.toList());
        for (ResourcePackProfile resourcePackProfile : this.profiles.values()) {
            if (!resourcePackProfile.isAlwaysEnabled() || list.contains(resourcePackProfile)) continue;
            resourcePackProfile.getInitialPosition().insert(list, resourcePackProfile, Functions.identity(), false);
        }
        return ImmutableList.copyOf(list);
    }

    private Stream<ResourcePackProfile> streamProfilesByName(Collection<String> names) {
        return names.stream().map(this.profiles::get).filter(Objects::nonNull);
    }

    public Collection<String> getNames() {
        return this.profiles.keySet();
    }

    public Collection<ResourcePackProfile> getProfiles() {
        return this.profiles.values();
    }

    public Collection<String> getEnabledNames() {
        return this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableSet.toImmutableSet());
    }

    public Collection<ResourcePackProfile> getEnabledProfiles() {
        return this.enabled;
    }

    @Nullable
    public ResourcePackProfile getProfile(String name) {
        return this.profiles.get(name);
    }

    @Override
    public void close() {
        this.profiles.values().forEach(ResourcePackProfile::close);
    }

    public boolean hasProfile(String name) {
        return this.profiles.containsKey(name);
    }

    public List<ResourcePack> createResourcePacks() {
        return this.enabled.stream().map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
    }
}

