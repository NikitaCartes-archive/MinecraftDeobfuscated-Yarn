package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class AbstractListTag<T extends Tag> extends AbstractList<T> implements Tag {
	public abstract int size();

	public T get(int i) {
		return this.getRaw(i);
	}

	public T set(int i, T tag) {
		T tag2 = this.get(i);
		this.setRaw(i, tag);
		return tag2;
	}

	public abstract T getRaw(int i);

	public abstract void setRaw(int i, Tag tag);

	public abstract void append(int i, Tag tag);

	public abstract void remove(int i);
}
