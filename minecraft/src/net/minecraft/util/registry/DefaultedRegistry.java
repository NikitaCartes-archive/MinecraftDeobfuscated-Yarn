package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class DefaultedRegistry<T> extends SimpleRegistry<T> {
	private final Identifier defaultId;
	private T defaultValue;

	public DefaultedRegistry(String defaultId, RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
		this.defaultId = new Identifier(defaultId);
	}

	@Override
	public <V extends T> V set(int rawId, RegistryKey<T> registryKey, V entry) {
		if (this.defaultId.equals(registryKey.getValue())) {
			this.defaultValue = (T)entry;
		}

		return super.set(rawId, registryKey, entry);
	}

	@Override
	public int getRawId(@Nullable T entry) {
		int i = super.getRawId(entry);
		return i == -1 ? super.getRawId(this.defaultValue) : i;
	}

	@Nonnull
	@Override
	public Identifier getId(T entry) {
		Identifier identifier = super.getId(entry);
		return identifier == null ? this.defaultId : identifier;
	}

	@Nonnull
	@Override
	public T get(@Nullable Identifier id) {
		T object = super.get(id);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public T get(int index) {
		T object = super.get(index);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public T getRandom(Random random) {
		T object = super.getRandom(random);
		return object == null ? this.defaultValue : object;
	}

	public Identifier getDefaultId() {
		return this.defaultId;
	}
}
