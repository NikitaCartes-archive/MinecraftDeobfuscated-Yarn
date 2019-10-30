package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class DefaultedList<E> extends AbstractList<E> {
	private final List<E> delegate;
	private final E initialElement;

	public static <E> DefaultedList<E> of() {
		return new DefaultedList<>();
	}

	public static <E> DefaultedList<E> ofSize(int size, E defaultValue) {
		Validate.notNull(defaultValue);
		Object[] objects = new Object[size];
		Arrays.fill(objects, defaultValue);
		return new DefaultedList<>(Arrays.asList(objects), defaultValue);
	}

	@SafeVarargs
	public static <E> DefaultedList<E> copyOf(E defaultValue, E... values) {
		return new DefaultedList<>(Arrays.asList(values), defaultValue);
	}

	protected DefaultedList() {
		this(Lists.<E>newArrayList(), null);
	}

	protected DefaultedList(List<E> delegate, @Nullable E initialElement) {
		this.delegate = delegate;
		this.initialElement = initialElement;
	}

	@Nonnull
	public E get(int index) {
		return (E)this.delegate.get(index);
	}

	public E set(int index, E element) {
		Validate.notNull(element);
		return (E)this.delegate.set(index, element);
	}

	public void add(int value, E element) {
		Validate.notNull(element);
		this.delegate.add(value, element);
	}

	public E remove(int index) {
		return (E)this.delegate.remove(index);
	}

	public int size() {
		return this.delegate.size();
	}

	public void clear() {
		if (this.initialElement == null) {
			super.clear();
		} else {
			for (int i = 0; i < this.size(); i++) {
				this.set(i, this.initialElement);
			}
		}
	}
}
