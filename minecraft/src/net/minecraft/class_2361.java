package net.minecraft;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class class_2361<T> implements class_2359<T> {
	private int field_11099;
	private final IdentityHashMap<T, Integer> field_11100;
	private final List<T> field_11098;

	public class_2361() {
		this(512);
	}

	public class_2361(int i) {
		this.field_11098 = Lists.<T>newArrayListWithExpectedSize(i);
		this.field_11100 = new IdentityHashMap(i);
	}

	public void method_10203(T object, int i) {
		this.field_11100.put(object, i);

		while (this.field_11098.size() <= i) {
			this.field_11098.add(null);
		}

		this.field_11098.set(i, object);
		if (this.field_11099 <= i) {
			this.field_11099 = i + 1;
		}
	}

	public void method_10205(T object) {
		this.method_10203(object, this.field_11099);
	}

	public int method_10206(T object) {
		Integer integer = (Integer)this.field_11100.get(object);
		return integer == null ? -1 : integer;
	}

	@Nullable
	@Override
	public final T method_10200(int i) {
		return (T)(i >= 0 && i < this.field_11098.size() ? this.field_11098.get(i) : null);
	}

	public Iterator<T> iterator() {
		return Iterators.filter(this.field_11098.iterator(), Predicates.notNull());
	}

	public int method_10204() {
		return this.field_11100.size();
	}
}
