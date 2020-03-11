package net.minecraft.util.collection;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class Int2ObjectBiMap<K> implements IndexedIterable<K> {
	private static final Object empty = null;
	private K[] values;
	private int[] ids;
	private K[] idToValues;
	private int nextId;
	private int size;

	public Int2ObjectBiMap(int size) {
		size = (int)((float)size / 0.8F);
		this.values = (K[])(new Object[size]);
		this.ids = new int[size];
		this.idToValues = (K[])(new Object[size]);
	}

	public int getId(@Nullable K value) {
		return this.getIdFromIndex(this.findIndex(value, this.getIdealIndex(value)));
	}

	@Nullable
	@Override
	public K get(int index) {
		return index >= 0 && index < this.idToValues.length ? this.idToValues[index] : null;
	}

	private int getIdFromIndex(int index) {
		return index == -1 ? -1 : this.ids[index];
	}

	public int add(K value) {
		int i = this.nextId();
		this.put(value, i);
		return i;
	}

	private int nextId() {
		while (this.nextId < this.idToValues.length && this.idToValues[this.nextId] != null) {
			this.nextId++;
		}

		return this.nextId;
	}

	private void resize(int newSize) {
		K[] objects = this.values;
		int[] is = this.ids;
		this.values = (K[])(new Object[newSize]);
		this.ids = new int[newSize];
		this.idToValues = (K[])(new Object[newSize]);
		this.nextId = 0;
		this.size = 0;

		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				this.put(objects[i], is[i]);
			}
		}
	}

	public void put(K value, int id) {
		int i = Math.max(id, this.size + 1);
		if ((float)i >= (float)this.values.length * 0.8F) {
			int j = this.values.length << 1;

			while (j < id) {
				j <<= 1;
			}

			this.resize(j);
		}

		int j = this.findFree(this.getIdealIndex(value));
		this.values[j] = value;
		this.ids[j] = id;
		this.idToValues[id] = value;
		this.size++;
		if (id == this.nextId) {
			this.nextId++;
		}
	}

	private int getIdealIndex(@Nullable K value) {
		return (MathHelper.idealHash(System.identityHashCode(value)) & 2147483647) % this.values.length;
	}

	private int findIndex(@Nullable K value, int id) {
		for (int i = id; i < this.values.length; i++) {
			if (this.values[i] == value) {
				return i;
			}

			if (this.values[i] == empty) {
				return -1;
			}
		}

		for (int i = 0; i < id; i++) {
			if (this.values[i] == value) {
				return i;
			}

			if (this.values[i] == empty) {
				return -1;
			}
		}

		return -1;
	}

	private int findFree(int size) {
		for (int i = size; i < this.values.length; i++) {
			if (this.values[i] == empty) {
				return i;
			}
		}

		for (int ix = 0; ix < size; ix++) {
			if (this.values[ix] == empty) {
				return ix;
			}
		}

		throw new RuntimeException("Overflowed :(");
	}

	public Iterator<K> iterator() {
		return Iterators.filter(Iterators.forArray(this.idToValues), Predicates.notNull());
	}

	public void clear() {
		Arrays.fill(this.values, null);
		Arrays.fill(this.idToValues, null);
		this.nextId = 0;
		this.size = 0;
	}

	public int size() {
		return this.size;
	}
}
