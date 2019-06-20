package net.minecraft;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class class_2371<E> extends AbstractList<E> {
	private final List<E> field_11115;
	private final E field_11116;

	public static <E> class_2371<E> method_10211() {
		return new class_2371<>();
	}

	public static <E> class_2371<E> method_10213(int i, E object) {
		Validate.notNull(object);
		Object[] objects = new Object[i];
		Arrays.fill(objects, object);
		return new class_2371<>(Arrays.asList(objects), object);
	}

	@SafeVarargs
	public static <E> class_2371<E> method_10212(E object, E... objects) {
		return new class_2371<>(Arrays.asList(objects), object);
	}

	protected class_2371() {
		this(new ArrayList(), null);
	}

	protected class_2371(List<E> list, @Nullable E object) {
		this.field_11115 = list;
		this.field_11116 = object;
	}

	@Nonnull
	public E get(int i) {
		return (E)this.field_11115.get(i);
	}

	public E set(int i, E object) {
		Validate.notNull(object);
		return (E)this.field_11115.set(i, object);
	}

	public void add(int i, E object) {
		Validate.notNull(object);
		this.field_11115.add(i, object);
	}

	public E remove(int i) {
		return (E)this.field_11115.remove(i);
	}

	public int size() {
		return this.field_11115.size();
	}

	public void clear() {
		if (this.field_11116 == null) {
			super.clear();
		} else {
			for (int i = 0; i < this.size(); i++) {
				this.set(i, this.field_11116);
			}
		}
	}
}
