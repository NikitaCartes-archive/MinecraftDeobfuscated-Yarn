package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class IdList<T> implements IndexedIterable<T> {
	private int nextId;
	private final IdentityHashMap<T, Integer> idMap;
	private final List<T> list;

	public IdList() {
		this(512);
	}

	public IdList(int i) {
		this.list = Lists.<T>newArrayListWithExpectedSize(i);
		this.idMap = new IdentityHashMap(i);
	}

	public void set(T value, int i) {
		this.idMap.put(value, i);

		while (this.list.size() <= i) {
			this.list.add(null);
		}

		this.list.set(i, value);
		if (this.nextId <= i) {
			this.nextId = i + 1;
		}
	}

	public void add(T object) {
		this.set(object, this.nextId);
	}

	public int getId(T object) {
		Integer integer = (Integer)this.idMap.get(object);
		return integer == null ? -1 : integer;
	}

	@Nullable
	@Override
	public final T get(int index) {
		return (T)(index >= 0 && index < this.list.size() ? this.list.get(index) : null);
	}

	public Iterator<T> iterator() {
		return Iterators.filter(this.list.iterator(), Predicates.notNull());
	}

	public int size() {
		return this.idMap.size();
	}
}
