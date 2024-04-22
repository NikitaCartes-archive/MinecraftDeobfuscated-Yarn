package net.minecraft.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.ForwardingDynamicOps;

public class RegistryOps<T> extends ForwardingDynamicOps<T> {
	private final RegistryOps.RegistryInfoGetter registryInfoGetter;

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, RegistryWrapper.WrapperLookup wrapperLookup) {
		return of(delegate, new RegistryOps.CachedRegistryInfoGetter(wrapperLookup));
	}

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, RegistryOps.RegistryInfoGetter registryInfoGetter) {
		return new RegistryOps<>(delegate, registryInfoGetter);
	}

	public static <T> Dynamic<T> withRegistry(Dynamic<T> dynamic, RegistryWrapper.WrapperLookup registryLookup) {
		return new Dynamic<>(registryLookup.getOps(dynamic.getOps()), dynamic.getValue());
	}

	private RegistryOps(DynamicOps<T> delegate, RegistryOps.RegistryInfoGetter registryInfoGetter) {
		super(delegate);
		this.registryInfoGetter = registryInfoGetter;
	}

	public <U> RegistryOps<U> withDelegate(DynamicOps<U> delegate) {
		return (RegistryOps<U>)(delegate == this.delegate ? this : new RegistryOps<>(delegate, this.registryInfoGetter));
	}

	public <E> Optional<RegistryEntryOwner<E>> getOwner(RegistryKey<? extends Registry<? extends E>> registryRef) {
		return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryOps.RegistryInfo::owner);
	}

	public <E> Optional<RegistryEntryLookup<E>> getEntryLookup(RegistryKey<? extends Registry<? extends E>> registryRef) {
		return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryOps.RegistryInfo::entryLookup);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			RegistryOps<?> registryOps = (RegistryOps<?>)o;
			return this.delegate.equals(registryOps.delegate) && this.registryInfoGetter.equals(registryOps.registryInfoGetter);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.delegate.hashCode() * 31 + this.registryInfoGetter.hashCode();
	}

	public static <E, O> RecordCodecBuilder<O, RegistryEntryLookup<E>> getEntryLookupCodec(RegistryKey<? extends Registry<? extends E>> registryRef) {
		return Codecs.<RegistryEntryLookup<E>>createContextRetrievalCodec(
				ops -> ops instanceof RegistryOps<?> registryOps
						? (DataResult)registryOps.registryInfoGetter
							.getRegistryInfo(registryRef)
							.map(info -> DataResult.success(info.entryLookup(), info.elementsLifecycle()))
							.orElseGet(() -> DataResult.error(() -> "Unknown registry: " + registryRef))
						: DataResult.error(() -> "Not a registry ops")
			)
			.forGetter(object -> null);
	}

	public static <E, O> RecordCodecBuilder<O, RegistryEntry.Reference<E>> getEntryCodec(RegistryKey<E> key) {
		RegistryKey<? extends Registry<E>> registryKey = RegistryKey.ofRegistry(key.getRegistry());
		return Codecs.<RegistryEntry.Reference<E>>createContextRetrievalCodec(
				ops -> ops instanceof RegistryOps<?> registryOps
						? (DataResult)registryOps.registryInfoGetter
							.getRegistryInfo(registryKey)
							.flatMap(info -> info.entryLookup().getOptional(key))
							.map(DataResult::success)
							.orElseGet(() -> DataResult.error(() -> "Can't find value: " + key))
						: DataResult.error(() -> "Not a registry ops")
			)
			.forGetter(object -> null);
	}

	static final class CachedRegistryInfoGetter implements RegistryOps.RegistryInfoGetter {
		private final RegistryWrapper.WrapperLookup registriesLookup;
		private final Map<RegistryKey<? extends Registry<?>>, Optional<? extends RegistryOps.RegistryInfo<?>>> cache = new ConcurrentHashMap();

		public CachedRegistryInfoGetter(RegistryWrapper.WrapperLookup registriesLookup) {
			this.registriesLookup = registriesLookup;
		}

		@Override
		public <E> Optional<RegistryOps.RegistryInfo<E>> getRegistryInfo(RegistryKey<? extends Registry<? extends E>> registryRef) {
			return (Optional<RegistryOps.RegistryInfo<E>>)this.cache.computeIfAbsent(registryRef, this::compute);
		}

		private Optional<RegistryOps.RegistryInfo<Object>> compute(RegistryKey<? extends Registry<?>> registryRef) {
			return this.registriesLookup.getOptionalWrapper(registryRef).map(RegistryOps.RegistryInfo::fromWrapper);
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				if (o instanceof RegistryOps.CachedRegistryInfoGetter cachedRegistryInfoGetter && this.registriesLookup.equals(cachedRegistryInfoGetter.registriesLookup)) {
					return true;
				}

				return false;
			}
		}

		public int hashCode() {
			return this.registriesLookup.hashCode();
		}
	}

	public static record RegistryInfo<T>(RegistryEntryOwner<T> owner, RegistryEntryLookup<T> entryLookup, Lifecycle elementsLifecycle) {
		public static <T> RegistryOps.RegistryInfo<T> fromWrapper(RegistryWrapper.Impl<T> wrapper) {
			return new RegistryOps.RegistryInfo<>(wrapper, wrapper, wrapper.getLifecycle());
		}
	}

	public interface RegistryInfoGetter {
		<T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef);
	}
}
