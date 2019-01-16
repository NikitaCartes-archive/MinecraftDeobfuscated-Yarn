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

	public TypeFilterableList(Class<T> class_) {
		this.elementType = class_;
		this.elementsByType.put(class_, this.allElements);
	}

	public boolean add(T object) {
		boolean bl = false;

		for (Entry<Class<?>, List<T>> entry : this.elementsByType.entrySet()) {
			if (((Class)entry.getKey()).isInstance(object)) {
				bl |= ((List)entry.getValue()).add(object);
			}
		}

		return bl;
	}

	public boolean remove(Object object) {
		boolean bl = false;

		for (Entry<Class<?>, List<T>> entry : this.elementsByType.entrySet()) {
			if (((Class)entry.getKey()).isInstance(object)) {
				List<T> list = (List<T>)entry.getValue();
				bl |= list.remove(object);
			}
		}

		return bl;
	}

	public boolean contains(Object object) {
		return this.getAllOfType(object.getClass()).contains(object);
	}

	public <S> Collection<S> getAllOfType(Class<S> class_) {
		if (!this.elementType.isAssignableFrom(class_)) {
			throw new IllegalArgumentException("Don't know how to search for " + class_);
		} else {
			List<T> list = (List<T>)this.elementsByType
				.computeIfAbsent(class_, class_x -> (List)this.allElements.stream().filter(class_x::isInstance).collect(Collectors.toList()));
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
