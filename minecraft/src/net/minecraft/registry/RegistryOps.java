package net.minecraft.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.ForwardingDynamicOps;

public class RegistryOps<T> extends ForwardingDynamicOps<T> {
	private final RegistryOps.RegistryInfoGetter registryInfoGetter;

	private static RegistryOps.RegistryInfoGetter caching(RegistryOps.RegistryInfoGetter registryInfoGetter) {
		return new RegistryOps.RegistryInfoGetter() {
			private final Map<RegistryKey<? extends Registry<?>>, Optional<? extends RegistryOps.RegistryInfo<?>>> registryRefToInfo = new HashMap();

			@Override
			public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
				return (Optional<RegistryOps.RegistryInfo<T>>)this.registryRefToInfo.computeIfAbsent(registryRef, registryInfoGetter::getRegistryInfo);
			}
		};
	}

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, RegistryWrapper.WrapperLookup wrapperLookup) {
		return of(delegate, caching(new RegistryOps.RegistryInfoGetter() {
			@Override
			public <E> Optional<RegistryOps.RegistryInfo<E>> getRegistryInfo(RegistryKey<? extends Registry<? extends E>> registryRef) {
				return wrapperLookup.getOptionalWrapper(registryRef).map(wrapper -> new RegistryOps.RegistryInfo<>(wrapper, wrapper, wrapper.getLifecycle()));
			}
		}));
	}

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, RegistryOps.RegistryInfoGetter registryInfoGetter) {
		return new RegistryOps<>(delegate, registryInfoGetter);
	}

	private RegistryOps(DynamicOps<T> delegate, RegistryOps.RegistryInfoGetter registryInfoGetter) {
		super(delegate);
		this.registryInfoGetter = registryInfoGetter;
	}

	public <E> Optional<RegistryEntryOwner<E>> getOwner(RegistryKey<? extends Registry<? extends E>> registryRef) {
		return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryOps.RegistryInfo::owner);
	}

	public <E> Optional<RegistryEntryLookup<E>> getEntryLookup(RegistryKey<? extends Registry<? extends E>> registryRef) {
		return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryOps.RegistryInfo::entryLookup);
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

	public static record RegistryInfo<T>(RegistryEntryOwner<T> owner, RegistryEntryLookup<T> entryLookup, Lifecycle elementsLifecycle) {
	}

	public interface RegistryInfoGetter {
		<T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef);
	}
}
