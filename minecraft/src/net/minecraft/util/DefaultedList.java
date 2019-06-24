package net.minecraft.util;

import java.util.AbstractList;
import java.util.ArrayList;
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

	public static <E> DefaultedList<E> ofSize(int i, E object) {
		Validate.notNull(object);
		Object[] objects = new Object[i];
		Arrays.fill(objects, object);
		return new DefaultedList<>(Arrays.asList(objects), object);
	}

	@SafeVarargs
	public static <E> DefaultedList<E> copyOf(E object, E... objects) {
		return new DefaultedList<>(Arrays.asList(objects), object);
	}

	protected DefaultedList() {
		this(new ArrayList(), null);
	}

	protected DefaultedList(List<E> list, @Nullable E object) {
		this.delegate = list;
		this.initialElement = object;
	}

	@Nonnull
	public E get(int i) {
		return (E)this.delegate.get(i);
	}

	public E set(int i, E object) {
		Validate.notNull(object);
		return (E)this.delegate.set(i, object);
	}

	public void add(int i, E object) {
		Validate.notNull(object);
		this.delegate.add(i, object);
	}

	public E remove(int i) {
		return (E)this.delegate.remove(i);
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
