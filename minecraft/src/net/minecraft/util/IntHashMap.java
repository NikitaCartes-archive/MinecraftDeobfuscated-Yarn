package net.minecraft.util;

import javax.annotation.Nullable;

public class IntHashMap<V> {
	private transient IntHashMap.Entry<V>[] entries;
	private transient int field_15709;
	private int field_15708;
	private final float field_15707 = 0.75F;

	public IntHashMap() {
		this.field_15708 = 12;
		this.entries = new IntHashMap.Entry[16];
	}

	private static int hash(int i) {
		i ^= i >>> 20 ^ i >>> 12;
		return i ^ i >>> 7 ^ i >>> 4;
	}

	private static int modulo2(int i, int j) {
		return i & j - 1;
	}

	@Nullable
	public V get(int i) {
		int j = hash(i);

		for (IntHashMap.Entry<V> entry = this.entries[modulo2(j, this.entries.length)]; entry != null; entry = entry.next) {
			if (entry.key == i) {
				return entry.value;
			}
		}

		return null;
	}

	public boolean containsKey(int i) {
		return this.getEntry(i) != null;
	}

	@Nullable
	final IntHashMap.Entry<V> getEntry(int i) {
		int j = hash(i);

		for (IntHashMap.Entry<V> entry = this.entries[modulo2(j, this.entries.length)]; entry != null; entry = entry.next) {
			if (entry.key == i) {
				return entry;
			}
		}

		return null;
	}

	public void put(int i, V object) {
		int j = hash(i);
		int k = modulo2(j, this.entries.length);

		for (IntHashMap.Entry<V> entry = this.entries[k]; entry != null; entry = entry.next) {
			if (entry.key == i) {
				entry.value = object;
				return;
			}
		}

		this.method_15317(j, i, object, k);
	}

	private void resize(int i) {
		IntHashMap.Entry<V>[] entrys = this.entries;
		int j = entrys.length;
		if (j == 1073741824) {
			this.field_15708 = Integer.MAX_VALUE;
		} else {
			IntHashMap.Entry<V>[] entrys2 = new IntHashMap.Entry[i];
			this.copyEntriesTo(entrys2);
			this.entries = entrys2;
			this.field_15708 = (int)((float)i * this.field_15707);
		}
	}

	private void copyEntriesTo(IntHashMap.Entry<V>[] entrys) {
		IntHashMap.Entry<V>[] entrys2 = this.entries;
		int i = entrys.length;

		for (int j = 0; j < entrys2.length; j++) {
			IntHashMap.Entry<V> entry = entrys2[j];
			if (entry != null) {
				entrys2[j] = null;

				while (true) {
					IntHashMap.Entry<V> entry2 = entry.next;
					int k = modulo2(entry.field_15712, i);
					entry.next = entrys[k];
					entrys[k] = entry;
					entry = entry2;
					if (entry2 == null) {
						break;
					}
				}
			}
		}
	}

	@Nullable
	public V method_15312(int i) {
		IntHashMap.Entry<V> entry = this.method_15310(i);
		return entry == null ? null : entry.value;
	}

	@Nullable
	final IntHashMap.Entry<V> method_15310(int i) {
		int j = hash(i);
		int k = modulo2(j, this.entries.length);
		IntHashMap.Entry<V> entry = this.entries[k];
		IntHashMap.Entry<V> entry2 = entry;

		while (entry2 != null) {
			IntHashMap.Entry<V> entry3 = entry2.next;
			if (entry2.key == i) {
				this.field_15709--;
				if (entry == entry2) {
					this.entries[k] = entry3;
				} else {
					entry.next = entry3;
				}

				return entry2;
			}

			entry = entry2;
			entry2 = entry3;
		}

		return entry2;
	}

	public void clear() {
		IntHashMap.Entry<V>[] entrys = this.entries;

		for (int i = 0; i < entrys.length; i++) {
			entrys[i] = null;
		}

		this.field_15709 = 0;
	}

	private void method_15317(int i, int j, V object, int k) {
		IntHashMap.Entry<V> entry = this.entries[k];
		this.entries[k] = new IntHashMap.Entry<>(i, j, object, entry);
		if (this.field_15709++ >= this.field_15708) {
			this.resize(2 * this.entries.length);
		}
	}

	static class Entry<V> {
		private final int key;
		private V value;
		private IntHashMap.Entry<V> next;
		private final int field_15712;

		Entry(int i, int j, V object, IntHashMap.Entry<V> entry) {
			this.value = object;
			this.next = entry;
			this.key = j;
			this.field_15712 = i;
		}

		public final int getKey() {
			return this.key;
		}

		public final V getValue() {
			return this.value;
		}

		public final boolean equals(Object object) {
			if (!(object instanceof IntHashMap.Entry)) {
				return false;
			} else {
				IntHashMap.Entry<V> entry = (IntHashMap.Entry<V>)object;
				if (this.key == entry.key) {
					Object object2 = this.getValue();
					Object object3 = entry.getValue();
					if (object2 == object3 || object2 != null && object2.equals(object3)) {
						return true;
					}
				}

				return false;
			}
		}

		public final int hashCode() {
			return IntHashMap.hash(this.key);
		}

		public final String toString() {
			return this.getKey() + "=" + this.getValue();
		}
	}
}
