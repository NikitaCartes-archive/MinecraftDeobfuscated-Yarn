package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TypeFilterableList<T> extends AbstractCollection<T> {
	private final Map<Class<?>, List<T>> elementsByType = Maps.<Class<?>, List<T>>newHashMap();
	private final Class<T> elementType;
	private final List<T> allElements = Lists.<T>newArrayList();

	public TypeFilterableList(Class<T> elementType) {
		this.elementType = elementType;
		this.elementsByType.put(elementType, this.allElements);
	}

	public boolean add(T e) {
		boolean bl = false;

		for (Entry<Class<?>, List<T>> entry : this.elementsByType.entrySet()) {
			if (((Class)entry.getKey()).isInstance(e)) {
				bl |= ((List)entry.getValue()).add(e);
			}
		}

		return bl;
	}

	public boolean remove(Object o) {
		boolean bl = false;

		for (Entry<Class<?>, List<T>> entry : this.elementsByType.entrySet()) {
			if (((Class)entry.getKey()).isInstance(o)) {
				List<T> list = (List<T>)entry.getValue();
				bl |= list.remove(o);
			}
		}

		return bl;
	}

	public boolean contains(Object o) {
		return this.getAllOfType(o.getClass()).contains(o);
	}

	public <S> Collection<S> getAllOfType(Class<S> type) {
		if (!this.elementType.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Don't know how to search for " + type);
		} else {
			List<T> list = (List<T>)this.elementsByType
				.computeIfAbsent(type, class_ -> (List)this.allElements.stream().filter(class_::isInstance).collect(Collectors.toList()));
			return Collections.unmodifiableCollection(list);
		}
	}

	public Iterator<T> iterator() {
		return (Iterator<T>)(this.allElements.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allElements.iterator()));
	}

	public int size() {
		return this.allElements.size();
	}
}
