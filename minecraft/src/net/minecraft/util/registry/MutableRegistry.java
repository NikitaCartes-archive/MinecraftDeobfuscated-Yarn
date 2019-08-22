package net.minecraft.util.registry;

import net.minecraft.util.Identifier;

public abstract class MutableRegistry<T> extends Registry<T> {
	public abstract <V extends T> V set(int i, Identifier identifier, V object);

	public abstract <V extends T> V add(Identifier identifier, V object);

	public abstract boolean isEmpty();
}
