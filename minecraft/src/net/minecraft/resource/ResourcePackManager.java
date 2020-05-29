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

	public ResourcePackManager(ResourcePackProfile.Factory<T> factory, ResourcePackProvider... resourcePackProviders) {
		this.profileFactory = factory;
		this.providers = ImmutableSet.copyOf(resourcePackProviders);
	}

	public void scanPacks() {
		List<String> list = (List<String>)this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
		this.close();
		this.profiles = this.method_29212();
		this.enabled = this.method_29208(list);
	}

	private Map<String, T> method_29212() {
		Map<String, T> map = Maps.newTreeMap();

		for (ResourcePackProvider resourcePackProvider : this.providers) {
			resourcePackProvider.register(map, this.profileFactory);
		}

		return ImmutableMap.copyOf(map);
	}

	public void setEnabledProfiles(Collection<String> enabled) {
		this.enabled = this.method_29208(enabled);
	}

	private List<T> method_29208(Collection<String> collection) {
		List<T> list = (List<T>)this.method_29209(collection).collect(Collectors.toList());

		for (T resourcePackProfile : this.profiles.values()) {
			if (resourcePackProfile.isAlwaysEnabled() && !list.contains(resourcePackProfile)) {
				resourcePackProfile.getInitialPosition().insert(list, resourcePackProfile, Functions.identity(), false);
			}
		}

		return ImmutableList.copyOf(list);
	}

	private Stream<T> method_29209(Collection<String> collection) {
		return collection.stream().map(this.profiles::get).filter(Objects::nonNull);
	}

	public Collection<String> method_29206() {
		return this.profiles.keySet();
	}

	public Collection<T> getProfiles() {
		return this.profiles.values();
	}

	public Collection<String> method_29210() {
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

	public boolean method_29207(String string) {
		return this.profiles.containsKey(string);
	}

	public List<ResourcePack> method_29211() {
		return (List<ResourcePack>)this.enabled.stream().map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
	}
}
