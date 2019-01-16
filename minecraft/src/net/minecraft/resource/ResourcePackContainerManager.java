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

public class ResourcePackContainerManager<T extends ResourcePackContainer> {
	private final Set<ResourcePackCreator> creators = Sets.<ResourcePackCreator>newHashSet();
	private final Map<String, T> nameToContainer = Maps.<String, T>newLinkedHashMap();
	private final List<T> enabledContainers = Lists.<T>newLinkedList();
	private final ResourcePackContainer.Factory<T> factory;

	public ResourcePackContainerManager(ResourcePackContainer.Factory<T> factory) {
		this.factory = factory;
	}

	public void callCreators() {
		Set<String> set = (Set<String>)this.enabledContainers.stream().map(ResourcePackContainer::getName).collect(Collectors.toCollection(LinkedHashSet::new));
		this.nameToContainer.clear();
		this.enabledContainers.clear();

		for (ResourcePackCreator resourcePackCreator : this.creators) {
			resourcePackCreator.registerContainer(this.nameToContainer, this.factory);
		}

		this.sortMapByName();
		this.enabledContainers
			.addAll((Collection)set.stream().map(this.nameToContainer::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));

		for (T resourcePackContainer : this.nameToContainer.values()) {
			if (resourcePackContainer.canBeSorted() && !this.enabledContainers.contains(resourcePackContainer)) {
				resourcePackContainer.getSortingDirection().locate(this.enabledContainers, resourcePackContainer, Functions.identity(), false);
			}
		}
	}

	private void sortMapByName() {
		List<Entry<String, T>> list = Lists.<Entry<String, T>>newArrayList(this.nameToContainer.entrySet());
		this.nameToContainer.clear();
		list.stream().sorted(Entry.comparingByKey()).forEachOrdered(entry -> {
			ResourcePackContainer var10000 = (ResourcePackContainer)this.nameToContainer.put(entry.getKey(), entry.getValue());
		});
	}

	public void setEnabled(Collection<T> collection) {
		this.enabledContainers.clear();
		this.enabledContainers.addAll(collection);

		for (T resourcePackContainer : this.nameToContainer.values()) {
			if (resourcePackContainer.canBeSorted() && !this.enabledContainers.contains(resourcePackContainer)) {
				resourcePackContainer.getSortingDirection().locate(this.enabledContainers, resourcePackContainer, Functions.identity(), false);
			}
		}
	}

	public Collection<T> getAlphabeticallyOrderedContainers() {
		return this.nameToContainer.values();
	}

	public Collection<T> getDisabledContainers() {
		Collection<T> collection = Lists.<T>newArrayList(this.nameToContainer.values());
		collection.removeAll(this.enabledContainers);
		return collection;
	}

	public Collection<T> getEnabledContainers() {
		return this.enabledContainers;
	}

	@Nullable
	public T getContainer(String string) {
		return (T)this.nameToContainer.get(string);
	}

	public void addCreator(ResourcePackCreator resourcePackCreator) {
		this.creators.add(resourcePackCreator);
	}
}
