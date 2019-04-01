package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class class_3522<V> implements Map<String, V> {
	private final Map<String, V> field_15666 = Maps.<String, V>newLinkedHashMap();

	public int size() {
		return this.field_15666.size();
	}

	public boolean isEmpty() {
		return this.field_15666.isEmpty();
	}

	public boolean containsKey(Object object) {
		return this.field_15666.containsKey(object.toString().toLowerCase(Locale.ROOT));
	}

	public boolean containsValue(Object object) {
		return this.field_15666.containsValue(object);
	}

	public V get(Object object) {
		return (V)this.field_15666.get(object.toString().toLowerCase(Locale.ROOT));
	}

	public V method_15304(String string, V object) {
		return (V)this.field_15666.put(string.toLowerCase(Locale.ROOT), object);
	}

	public V remove(Object object) {
		return (V)this.field_15666.remove(object.toString().toLowerCase(Locale.ROOT));
	}

	public void putAll(Map<? extends String, ? extends V> map) {
		for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
			this.method_15304((String)entry.getKey(), (V)entry.getValue());
		}
	}

	public void clear() {
		this.field_15666.clear();
	}

	public Set<String> keySet() {
		return this.field_15666.keySet();
	}

	public Collection<V> values() {
		return this.field_15666.values();
	}

	public Set<Entry<String, V>> entrySet() {
		return this.field_15666.entrySet();
	}
}
