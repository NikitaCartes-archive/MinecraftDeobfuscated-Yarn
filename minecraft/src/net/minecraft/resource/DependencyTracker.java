package net.minecraft.resource;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DependencyTracker<K, V extends DependencyTracker.Dependencies<K>> {
	private final Map<K, V> underlying = new HashMap();

	public DependencyTracker<K, V> add(K key, V value) {
		this.underlying.put(key, value);
		return this;
	}

	private void traverse(Multimap<K, K> parentChild, Set<K> visited, K rootKey, BiConsumer<K, V> callback) {
		if (visited.add(rootKey)) {
			parentChild.get(rootKey).forEach(child -> this.traverse(parentChild, visited, (K)child, callback));
			V dependencies = (V)this.underlying.get(rootKey);
			if (dependencies != null) {
				callback.accept(rootKey, dependencies);
			}
		}
	}

	private static <K> boolean containsReverseDependency(Multimap<K, K> dependencies, K key, K dependency) {
		Collection<K> collection = dependencies.get(dependency);
		return collection.contains(key) ? true : collection.stream().anyMatch(subdependency -> containsReverseDependency(dependencies, key, (K)subdependency));
	}

	private static <K> void addDependency(Multimap<K, K> dependencies, K key, K dependency) {
		if (!containsReverseDependency(dependencies, key, dependency)) {
			dependencies.put(key, dependency);
		}
	}

	public void traverse(BiConsumer<K, V> callback) {
		Multimap<K, K> multimap = HashMultimap.create();
		this.underlying.forEach((key, value) -> value.forDependencies(dependency -> addDependency(multimap, (K)key, (K)dependency)));
		this.underlying.forEach((key, value) -> value.forOptionalDependencies(dependency -> addDependency(multimap, (K)key, (K)dependency)));
		Set<K> set = new HashSet();
		this.underlying.keySet().forEach(key -> this.traverse(multimap, set, (K)key, callback));
	}

	public interface Dependencies<K> {
		void forDependencies(Consumer<K> callback);

		void forOptionalDependencies(Consumer<K> callback);
	}
}
