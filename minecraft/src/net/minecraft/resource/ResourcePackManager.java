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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ResourcePackManager<T extends ResourcePackProfile> implements AutoCloseable {
	private final Set<ResourcePackProvider> providers;
	private Map<String, T> profiles = ImmutableMap.of();
	private List<T> enabled = ImmutableList.of();
	private final ResourcePackProfile.Factory<T> profileFactory;

	public ResourcePackManager(ResourcePackProfile.Factory<T> profileFactory, ResourcePackProvider... providers) {
		this.profileFactory = profileFactory;
		this.providers = ImmutableSet.copyOf(providers);
	}

	public void scanPacks() {
		List<String> list = (List<String>)this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
		this.close();
		this.profiles = this.providePackProfiles();
		this.enabled = this.buildEnabledProfiles(list);
	}

	private Map<String, T> providePackProfiles() {
		Map<String, T> map = Maps.newTreeMap();

		for (ResourcePackProvider resourcePackProvider : this.providers) {
			resourcePackProvider.register(resourcePackProfile -> {
				ResourcePackProfile var10000 = (ResourcePackProfile)map.put(resourcePackProfile.getName(), resourcePackProfile);
			}, this.profileFactory);
		}

		return ImmutableMap.copyOf(map);
	}

	public void setEnabledProfiles(Collection<String> enabled) {
		this.enabled = this.buildEnabledProfiles(enabled);
	}

	private List<T> buildEnabledProfiles(Collection<String> enabledNames) {
		List<T> list = (List<T>)this.streamProfilesByName(enabledNames).collect(Collectors.toList());

		for (T resourcePackProfile : this.profiles.values()) {
			if (resourcePackProfile.isAlwaysEnabled() && !list.contains(resourcePackProfile)) {
				resourcePackProfile.getInitialPosition().insert(list, resourcePackProfile, Functions.identity(), false);
			}
		}

		return ImmutableList.copyOf(list);
	}

	private Stream<T> streamProfilesByName(Collection<String> names) {
		return names.stream().map(this.profiles::get).filter(Objects::nonNull);
	}

	public Collection<String> getNames() {
		return this.profiles.keySet();
	}

	public Collection<T> getProfiles() {
		return this.profiles.values();
	}

	public Collection<String> getEnabledNames() {
		return (Collection<String>)this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableSet.toImmutableSet());
	}

	public Collection<T> getEnabledProfiles() {
		return this.enabled;
	}

	@Nullable
	public T getProfile(String name) {
		return (T)this.profiles.get(name);
	}

	public void close() {
		this.profiles.values().forEach(ResourcePackProfile::close);
	}

	public boolean hasProfile(String name) {
		return this.profiles.containsKey(name);
	}

	public List<ResourcePack> createResourcePacks() {
		return (List<ResourcePack>)this.enabled.stream().map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
	}
}
