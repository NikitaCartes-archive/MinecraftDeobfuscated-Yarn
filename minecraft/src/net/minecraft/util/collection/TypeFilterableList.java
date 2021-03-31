package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
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

/**
 * A collection allowing getting all elements of a specific type. Backed
 * by {@link java.util.ArrayList}s.
 * 
 * <p>This implementation is not efficient for frequent modifications. You
 * shouldn't use this if you don't call {@link #getAllOfType(Class)}.
 * 
 * @see #getAllOfType(Class)
 * @param <T> the {@link #elementType common element type}
 */
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

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException if {@code o} is not an instance of
	 * {@link #elementType}
	 */
	public boolean contains(Object o) {
		return this.getAllOfType(o.getClass()).contains(o);
	}

	/**
	 * Returns all elements in this collection that are instances of {@code type}.
	 * The result is unmodifiable.
	 * 
	 * <p>The {@code type}, or {@code S}, must extend the class' type parameter {@code T}.
	 * 
	 * @param <S> the specialized type, effectively {@code S extends T}
	 * @throws IllegalArgumentException when {@code type} does not extend
	 * {@link #elementType}
	 * @return this collection's elements that are instances of {@code type}
	 * 
	 * @param type the specialized type, must extend {@link #elementType}
	 */
	public <S> Collection<S> getAllOfType(Class<S> type) {
		if (!this.elementType.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Don't know how to search for " + type);
		} else {
			List<? extends T> list = (List<? extends T>)this.elementsByType
				.computeIfAbsent(type, typeClass -> (List)this.allElements.stream().filter(typeClass::isInstance).collect(Collectors.toList()));
			return Collections.unmodifiableCollection(list);
		}
	}

	public Iterator<T> iterator() {
		return (Iterator<T>)(this.allElements.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allElements.iterator()));
	}

	public List<T> copy() {
		return ImmutableList.copyOf(this.allElements);
	}

	public int size() {
		return this.allElements.size();
	}
}
