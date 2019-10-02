package net.minecraft.resource;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class ResourcePackManager<T extends ResourcePackProfile> implements AutoCloseable {
	private final Set<ResourcePackProvider> providers = Sets.<ResourcePackProvider>newHashSet();
	private final Map<String, T> profiles = Maps.<String, T>newLinkedHashMap();
	private final List<T> enabled = Lists.<T>newLinkedList();
	private final ResourcePackProfile.Factory<T> profileFactory;

	public ResourcePackManager(ResourcePackProfile.Factory<T> factory) {
		this.profileFactory = factory;
	}

	public void scanPacks() {
		this.close();
		Set<String> set = (Set<String>)this.enabled.stream().map(ResourcePackProfile::getName).collect(Collectors.toCollection(LinkedHashSet::new));
		this.profiles.clear();
		this.enabled.clear();

		for (ResourcePackProvider resourcePackProvider : this.providers) {
			resourcePackProvider.register(this.profiles, this.profileFactory);
		}

		this.sort();
		this.enabled.addAll((Collection)set.stream().map(this.profiles::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));

		for (T resourcePackProfile : this.profiles.values()) {
			if (resourcePackProfile.isAlwaysEnabled() && !this.enabled.contains(resourcePackProfile)) {
				resourcePackProfile.getInitialPosition().insert(this.enabled, resourcePackProfile, Functions.identity(), false);
			}
		}
	}

	private void sort() {
		List<Entry<String, T>> list = Lists.<Entry<String, T>>newArrayList(this.profiles.entrySet());
		this.profiles.clear();
		list.stream().sorted(Entry.comparingByKey()).forEachOrdered(entry -> {
			ResourcePackProfile var10000 = (ResourcePackProfile)this.profiles.put(entry.getKey(), entry.getValue());
		});
	}

	public void setEnabledProfiles(Collection<T> collection) {
		this.enabled.clear();
		this.enabled.addAll(collection);

		for (T resourcePackProfile : this.profiles.values()) {
			if (resourcePackProfile.isAlwaysEnabled() && !this.enabled.contains(resourcePackProfile)) {
				resourcePackProfile.getInitialPosition().insert(this.enabled, resourcePackProfile, Functions.identity(), false);
			}
		}
	}

	public Collection<T> getProfiles() {
		return this.profiles.values();
	}

	public Collection<T> getDisabledProfiles() {
		Collection<T> collection = Lists.<T>newArrayList(this.profiles.values());
		collection.removeAll(this.enabled);
		return collection;
	}

	public Collection<T> getEnabledProfiles() {
		return this.enabled;
	}

	@Nullable
	public T getProfile(String string) {
		return (T)this.profiles.get(string);
	}

	public void registerProvider(ResourcePackProvider resourcePackProvider) {
		this.providers.add(resourcePackProvider);
	}

	public void close() {
		this.profiles.values().forEach(ResourcePackProfile::close);
	}
}
