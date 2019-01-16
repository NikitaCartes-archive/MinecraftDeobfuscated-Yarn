package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class AbstractListTag<T extends Tag> extends AbstractList<T> implements Tag {
	public abstract T getOrDefault(int i, T tag);

	public abstract void set(int i, T tag);

	public abstract T getTag(int i);

	public abstract boolean setTag(int i, Tag tag);

	public abstract boolean addTag(int i, Tag tag);
}
