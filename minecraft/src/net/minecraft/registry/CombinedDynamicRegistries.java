package net.minecraft.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.util.Util;

public class CombinedDynamicRegistries<T> {
	private final List<T> types;
	private final List<DynamicRegistryManager.Immutable> registryManagers;
	private final DynamicRegistryManager.Immutable combinedRegistryManager;

	public CombinedDynamicRegistries(List<T> types) {
		this(types, Util.make(() -> {
			DynamicRegistryManager.Immutable[] immutables = new DynamicRegistryManager.Immutable[types.size()];
			Arrays.fill(immutables, DynamicRegistryManager.EMPTY);
			return Arrays.asList(immutables);
		}));
	}

	private CombinedDynamicRegistries(List<T> types, List<DynamicRegistryManager.Immutable> registryManagers) {
		this.types = List.copyOf(types);
		this.registryManagers = List.copyOf(registryManagers);
		this.combinedRegistryManager = new DynamicRegistryManager.ImmutableImpl(toRegistryMap(registryManagers.stream())).toImmutable();
	}

	private int getIndex(T type) {
		int i = this.types.indexOf(type);
		if (i == -1) {
			throw new IllegalStateException("Can't find " + type + " inside " + this.types);
		} else {
			return i;
		}
	}

	public DynamicRegistryManager.Immutable get(T index) {
		int i = this.getIndex(index);
		return (DynamicRegistryManager.Immutable)this.registryManagers.get(i);
	}

	public DynamicRegistryManager.Immutable getPrecedingRegistryManagers(T type) {
		int i = this.getIndex(type);
		return this.subset(0, i);
	}

	public DynamicRegistryManager.Immutable getSucceedingRegistryManagers(T type) {
		int i = this.getIndex(type);
		return this.subset(i, this.registryManagers.size());
	}

	private DynamicRegistryManager.Immutable subset(int startIndex, int endIndex) {
		return new DynamicRegistryManager.ImmutableImpl(toRegistryMap(this.registryManagers.subList(startIndex, endIndex).stream())).toImmutable();
	}

	public CombinedDynamicRegistries<T> with(T type, DynamicRegistryManager.Immutable... registryManagers) {
		return this.with(type, Arrays.asList(registryManagers));
	}

	public CombinedDynamicRegistries<T> with(T type, List<DynamicRegistryManager.Immutable> registryManagers) {
		int i = this.getIndex(type);
		if (registryManagers.size() > this.registryManagers.size() - i) {
			throw new IllegalStateException("Too many values to replace");
		} else {
			List<DynamicRegistryManager.Immutable> list = new ArrayList();

			for (int j = 0; j < i; j++) {
				list.add((DynamicRegistryManager.Immutable)this.registryManagers.get(j));
			}

			list.addAll(registryManagers);

			while (list.size() < this.registryManagers.size()) {
				list.add(DynamicRegistryManager.EMPTY);
			}

			return new CombinedDynamicRegistries<>(this.types, list);
		}
	}

	public DynamicRegistryManager.Immutable getCombinedRegistryManager() {
		return this.combinedRegistryManager;
	}

	private static Map<RegistryKey<? extends Registry<?>>, Registry<?>> toRegistryMap(Stream<? extends DynamicRegistryManager> registryManagers) {
		Map<RegistryKey<? extends Registry<?>>, Registry<?>> map = new HashMap();
		registryManagers.forEach(registryManager -> registryManager.streamAllRegistries().forEach(entry -> {
				if (map.put(entry.key(), entry.value()) != null) {
					throw new IllegalStateException("Duplicated registry " + entry.key());
				}
			}));
		return map;
	}
}
