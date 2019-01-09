package net.minecraft;

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

public class class_3509<T> extends AbstractCollection<T> {
	private final Map<Class<?>, List<T>> field_15636 = Maps.<Class<?>, List<T>>newHashMap();
	private final Class<T> field_15637;
	private final List<T> field_15635 = Lists.<T>newArrayList();

	public class_3509(Class<T> class_) {
		this.field_15637 = class_;
		this.field_15636.put(class_, this.field_15635);
	}

	public boolean add(T object) {
		boolean bl = false;

		for (Entry<Class<?>, List<T>> entry : this.field_15636.entrySet()) {
			if (((Class)entry.getKey()).isInstance(object)) {
				bl |= ((List)entry.getValue()).add(object);
			}
		}

		return bl;
	}

	public boolean remove(Object object) {
		boolean bl = false;

		for (Entry<Class<?>, List<T>> entry : this.field_15636.entrySet()) {
			if (((Class)entry.getKey()).isInstance(object)) {
				List<T> list = (List<T>)entry.getValue();
				bl |= list.remove(object);
			}
		}

		return bl;
	}

	public boolean contains(Object object) {
		return this.method_15216(object.getClass()).contains(object);
	}

	public <S> Collection<S> method_15216(Class<S> class_) {
		if (!this.field_15637.isAssignableFrom(class_)) {
			throw new IllegalArgumentException("Don't know how to search for " + class_);
		} else {
			List<T> list = (List<T>)this.field_15636
				.computeIfAbsent(class_, class_x -> (List)this.field_15635.stream().filter(class_x::isInstance).collect(Collectors.toList()));
			return Collections.unmodifiableCollection(list);
		}
	}

	public Iterator<T> iterator() {
		return (Iterator<T>)(this.field_15635.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.field_15635.iterator()));
	}

	public int size() {
		return this.field_15635.size();
	}
}
