package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;

public abstract class MutableRegistry<T> extends Registry<T> {
	public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
	}

	public abstract <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle);

	public abstract <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle);

	public abstract <V extends T> V method_31062(OptionalInt optionalInt, RegistryKey<T> registryKey, V object, Lifecycle lifecycle);
}
