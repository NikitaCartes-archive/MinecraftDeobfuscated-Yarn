package net.minecraft.util.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;

public final class RegistryFixedCodec<E> implements Codec<RegistryEntry<E>> {
	private final RegistryKey<? extends Registry<E>> registry;

	public static <E> RegistryFixedCodec<E> of(RegistryKey<? extends Registry<E>> registry) {
		return new RegistryFixedCodec<>(registry);
	}

	private RegistryFixedCodec(RegistryKey<? extends Registry<E>> registry) {
		this.registry = registry;
	}

	public <T> DataResult<T> encode(RegistryEntry<E> registryEntry, DynamicOps<T> dynamicOps, T object) {
		if (dynamicOps instanceof RegistryOps<?> registryOps) {
			Optional<? extends Registry<E>> optional = registryOps.getRegistry(this.registry);
			if (optional.isPresent()) {
				if (!registryEntry.matchesRegistry((Registry<E>)optional.get())) {
					return DataResult.error("Element " + registryEntry + " is not valid in current registry set");
				}

				return registryEntry.getKeyOrValue()
					.map(
						registryKey -> Identifier.CODEC.encode(registryKey.getValue(), dynamicOps, object),
						value -> DataResult.error("Elements from registry " + this.registry + " can't be serialized to a value")
					);
			}
		}

		return DataResult.error("Can't access registry " + this.registry);
	}

	@Override
	public <T> DataResult<Pair<RegistryEntry<E>, T>> decode(DynamicOps<T> ops, T input) {
		if (ops instanceof RegistryOps<?> registryOps) {
			Optional<? extends Registry<E>> optional = registryOps.getRegistry(this.registry);
			if (optional.isPresent()) {
				return Identifier.CODEC.decode(ops, input).flatMap(pair -> {
					Identifier identifier = (Identifier)pair.getFirst();
					DataResult<RegistryEntry<E>> dataResult = ((Registry)optional.get()).getOrCreateEntryDataResult(RegistryKey.of(this.registry, identifier));
					return dataResult.map(entry -> Pair.of(entry, pair.getSecond())).setLifecycle(Lifecycle.stable());
				});
			}
		}

		return DataResult.error("Can't access registry " + this.registry);
	}

	public String toString() {
		return "RegistryFixedCodec[" + this.registry + "]";
	}
}
