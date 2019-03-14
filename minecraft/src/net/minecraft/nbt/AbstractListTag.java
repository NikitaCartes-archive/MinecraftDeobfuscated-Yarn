package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class AbstractListTag<T extends Tag> extends AbstractList<T> implements Tag {
	public abstract T method_10606(int i, T tag);

	public abstract void method_10531(int i, T tag);

	public abstract T method_10536(int i);

	public abstract boolean setTag(int i, Tag tag);

	public abstract boolean addTag(int i, Tag tag);
}
