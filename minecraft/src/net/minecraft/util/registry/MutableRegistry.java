package net.minecraft.util.registry;

import net.minecraft.util.Identifier;

public abstract class MutableRegistry<T> extends Registry<T> {
	public abstract <V extends T> V set(int rawId, Identifier id, V entry);

	public abstract <V extends T> V add(Identifier id, V entry);

	public abstract boolean isEmpty();
}
