package net.minecraft.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class DefaultedList<E> extends AbstractList<E> {
	private final List<E> underlying;
	private final E defaultValue;

	public static <E> DefaultedList<E> create() {
		return new DefaultedList<>();
	}

	public static <E> DefaultedList<E> create(int i, E object) {
		Validate.notNull(object);
		Object[] objects = new Object[i];
		Arrays.fill(objects, object);
		return new DefaultedList<>(Arrays.asList(objects), object);
	}

	@SafeVarargs
	public static <E> DefaultedList<E> create(E object, E... objects) {
		return new DefaultedList<>(Arrays.asList(objects), object);
	}

	protected DefaultedList() {
		this(new ArrayList(), null);
	}

	protected DefaultedList(List<E> list, @Nullable E object) {
		this.underlying = list;
		this.defaultValue = object;
	}

	@Nonnull
	public E get(int i) {
		return (E)this.underlying.get(i);
	}

	public E set(int i, E object) {
		Validate.notNull(object);
		return (E)this.underlying.set(i, object);
	}

	public void add(int i, E object) {
		Validate.notNull(object);
		this.underlying.add(i, object);
	}

	public E remove(int i) {
		return (E)this.underlying.remove(i);
	}

	public int size() {
		return this.underlying.size();
	}

	public void clear() {
		if (this.defaultValue == null) {
			super.clear();
		} else {
			for (int i = 0; i < this.size(); i++) {
				this.set(i, this.defaultValue);
			}
		}
	}
}
