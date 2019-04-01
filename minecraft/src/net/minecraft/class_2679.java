package net.minecraft;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class class_2679<O, S> implements class_2688<S> {
	private static final Function<Entry<class_2769<?>, Comparable<?>>, String> field_12289 = new Function<Entry<class_2769<?>, Comparable<?>>, String>() {
		public String method_11576(@Nullable Entry<class_2769<?>, Comparable<?>> entry) {
			if (entry == null) {
				return "<NULL>";
			} else {
				class_2769<?> lv = (class_2769<?>)entry.getKey();
				return lv.method_11899() + "=" + this.method_11575(lv, (Comparable<?>)entry.getValue());
			}
		}

		private <T extends Comparable<T>> String method_11575(class_2769<T> arg, Comparable<?> comparable) {
			return arg.method_11901((T)comparable);
		}
	};
	protected final O field_12287;
	private final ImmutableMap<class_2769<?>, Comparable<?>> field_12285;
	private final int field_12286;
	private Table<class_2769<?>, Comparable<?>, S> field_12288;

	protected class_2679(O object, ImmutableMap<class_2769<?>, Comparable<?>> immutableMap) {
		this.field_12287 = object;
		this.field_12285 = immutableMap;
		this.field_12286 = immutableMap.hashCode();
	}

	public <T extends Comparable<T>> S method_11572(class_2769<T> arg) {
		return this.method_11657(arg, method_11574(arg.method_11898(), this.method_11654(arg)));
	}

	protected static <T> T method_11574(Collection<T> collection, T object) {
		Iterator<T> iterator = collection.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(object)) {
				if (iterator.hasNext()) {
					return (T)iterator.next();
				}

				return (T)collection.iterator().next();
			}
		}

		return (T)iterator.next();
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.field_12287);
		if (!this.method_11656().isEmpty()) {
			stringBuilder.append('[');
			stringBuilder.append((String)this.method_11656().entrySet().stream().map(field_12289).collect(Collectors.joining(",")));
			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	public Collection<class_2769<?>> method_11569() {
		return Collections.unmodifiableCollection(this.field_12285.keySet());
	}

	public <T extends Comparable<T>> boolean method_11570(class_2769<T> arg) {
		return this.field_12285.containsKey(arg);
	}

	@Override
	public <T extends Comparable<T>> T method_11654(class_2769<T> arg) {
		Comparable<?> comparable = this.field_12285.get(arg);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot get property " + arg + " as it does not exist in " + this.field_12287);
		} else {
			return (T)arg.method_11902().cast(comparable);
		}
	}

	@Override
	public <T extends Comparable<T>, V extends T> S method_11657(class_2769<T> arg, V comparable) {
		Comparable<?> comparable2 = this.field_12285.get(arg);
		if (comparable2 == null) {
			throw new IllegalArgumentException("Cannot set property " + arg + " as it does not exist in " + this.field_12287);
		} else if (comparable2 == comparable) {
			return (S)this;
		} else {
			S object = this.field_12288.get(arg, comparable);
			if (object == null) {
				throw new IllegalArgumentException("Cannot set property " + arg + " to " + comparable + " on " + this.field_12287 + ", it is not an allowed value");
			} else {
				return object;
			}
		}
	}

	public void method_11571(Map<Map<class_2769<?>, Comparable<?>>, S> map) {
		if (this.field_12288 != null) {
			throw new IllegalStateException();
		} else {
			Table<class_2769<?>, Comparable<?>, S> table = HashBasedTable.create();

			for (Entry<class_2769<?>, Comparable<?>> entry : this.field_12285.entrySet()) {
				class_2769<?> lv = (class_2769<?>)entry.getKey();

				for (Comparable<?> comparable : lv.method_11898()) {
					if (comparable != entry.getValue()) {
						table.put(lv, comparable, (S)map.get(this.method_11573(lv, comparable)));
					}
				}
			}

			this.field_12288 = (Table<class_2769<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
		}
	}

	private Map<class_2769<?>, Comparable<?>> method_11573(class_2769<?> arg, Comparable<?> comparable) {
		Map<class_2769<?>, Comparable<?>> map = Maps.<class_2769<?>, Comparable<?>>newHashMap(this.field_12285);
		map.put(arg, comparable);
		return map;
	}

	@Override
	public ImmutableMap<class_2769<?>, Comparable<?>> method_11656() {
		return this.field_12285;
	}

	public boolean equals(Object object) {
		return this == object;
	}

	public int hashCode() {
		return this.field_12286;
	}
}
