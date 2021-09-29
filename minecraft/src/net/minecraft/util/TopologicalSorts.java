package net.minecraft.util;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Utilities to perform a topological sort.
 */
public final class TopologicalSorts {
	private TopologicalSorts() {
	}

	/**
	 * Performs a topological sort recursively through a reverse DFS. The
	 * results are passed to the consumer in reverse order, where each element
	 * is guaranteed to be passed before any of its predecessors (dependencies).
	 * When multiple orders are valid, the results will first trace along earlier
	 * returned successors in the successor set iteration for each element.
	 * 
	 * @return {@code true} if the sort ends up in a loop, or {@code false} for
	 * a successful sort
	 * 
	 * @param successors the map holding information on successor (dependents) of each element
	 * @param visited elements that already iterated all their successors
	 * @param visiting elements that are still iterating their successors
	 * @param reversedOrderConsumer accepts sorted results in reverse order; each element is passed only
	 * before any of its predecessors (dependencies) is, or after all its
	 * successors (dependents) are passed
	 * @param now the starting or current element
	 */
	public static <T> boolean sort(Map<T, Set<T>> successors, Set<T> visited, Set<T> visiting, Consumer<T> reversedOrderConsumer, T now) {
		if (visited.contains(now)) {
			return false;
		} else if (visiting.contains(now)) {
			return true;
		} else {
			visiting.add(now);

			for (T object : (Set)successors.getOrDefault(now, ImmutableSet.of())) {
				if (sort(successors, visited, visiting, reversedOrderConsumer, object)) {
					return true;
				}
			}

			visiting.remove(now);
			visited.add(now);
			reversedOrderConsumer.accept(now);
			return false;
		}
	}
}
