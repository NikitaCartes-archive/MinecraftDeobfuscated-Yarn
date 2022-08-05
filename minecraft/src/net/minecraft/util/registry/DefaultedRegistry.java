package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

/**
 * An implementation of {@link Registry} with a default ID and value for unknown lookups.
 */
public class DefaultedRegistry<T> extends SimpleRegistry<T> {
	private final Identifier defaultId;
	private RegistryEntry<T> defaultEntry;

	public DefaultedRegistry(
		String defaultId, RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, @Nullable Function<T, RegistryEntry.Reference<T>> valueToEntryFunction
	) {
		super(key, lifecycle, valueToEntryFunction);
		this.defaultId = new Identifier(defaultId);
	}

	@Override
	public RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle) {
		RegistryEntry<T> registryEntry = super.set(rawId, key, value, lifecycle);
		if (this.defaultId.equals(key.getValue())) {
			this.defaultEntry = registryEntry;
		}

		return registryEntry;
	}

	@Override
	public int getRawId(@Nullable T value) {
		int i = super.getRawId(value);
		return i == -1 ? super.getRawId(this.defaultEntry.value()) : i;
	}

	@Nonnull
	@Override
	public Identifier getId(T value) {
		Identifier identifier = super.getId(value);
		return identifier == null ? this.defaultId : identifier;
	}

	@Nonnull
	@Override
	public T get(@Nullable Identifier id) {
		T object = super.get(id);
		return object == null ? this.defaultEntry.value() : object;
	}

	@Override
	public Optional<T> getOrEmpty(@Nullable Identifier id) {
		return Optional.ofNullable(super.get(id));
	}

	@Nonnull
	@Override
	public T get(int index) {
		T object = super.get(index);
		return object == null ? this.defaultEntry.value() : object;
	}

	@Override
	public Optional<RegistryEntry<T>> getRandom(Random random) {
		return super.getRandom(random).or(() -> Optional.of(this.defaultEntry));
	}

	/**
	 * {@return the ID of the default value}
	 */
	public Identifier getDefaultId() {
		return this.defaultId;
	}
}
