package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class AbstractListTag<T extends Tag> extends AbstractList<T> implements Tag {
	public abstract T method_10606(int i, T tag);

	public abstract void method_10531(int i, T tag);

	public abstract T method_10536(int i);

	public abstract boolean method_10535(int i, Tag tag);

	public abstract boolean method_10533(int i, Tag tag);
}
