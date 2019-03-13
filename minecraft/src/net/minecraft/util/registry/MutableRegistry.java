package net.minecraft.util.registry;

import net.minecraft.util.Identifier;

public abstract class MutableRegistry<T> extends Registry<T> {
	public abstract <V extends T> V method_10273(int i, Identifier identifier, V object);

	public abstract <V extends T> V method_10272(Identifier identifier, V object);

	public abstract boolean isEmpty();
}
