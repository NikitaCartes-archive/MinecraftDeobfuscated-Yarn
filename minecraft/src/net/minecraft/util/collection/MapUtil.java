package net.minecraft.util.collection;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapUtil {
	public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values) {
		return createMap(keys, values, Maps.<K, V>newLinkedHashMap());
	}

	public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values, Map<K, V> result) {
		Iterator<V> iterator = values.iterator();

		for (K object : keys) {
			result.put(object, iterator.next());
		}

		if (iterator.hasNext()) {
			throw new NoSuchElementException();
		} else {
			return result;
		}
	}
}
