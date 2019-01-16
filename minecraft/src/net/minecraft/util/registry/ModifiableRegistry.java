package net.minecraft.util.registry;

import net.minecraft.util.Identifier;

public abstract class ModifiableRegistry<T> extends Registry<T> {
	public abstract <V extends T> V set(int i, Identifier identifier, V object);

	public abstract <V extends T> V register(Identifier identifier, V object);

	public abstract boolean isEmpty();
}
