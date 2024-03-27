package net.minecraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class RegistryCloner<T> {
	private final Codec<T> elementCodec;

	RegistryCloner(Codec<T> elementCodec) {
		this.elementCodec = elementCodec;
	}

	public T clone(T value, RegistryWrapper.WrapperLookup subsetRegistry, RegistryWrapper.WrapperLookup fullRegistry) {
		DynamicOps<Object> dynamicOps = subsetRegistry.getOps(JavaOps.INSTANCE);
		DynamicOps<Object> dynamicOps2 = fullRegistry.getOps(JavaOps.INSTANCE);
		Object object = this.elementCodec.encodeStart(dynamicOps, value).getOrThrow(error -> new IllegalStateException("Failed to encode: " + error));
		return this.elementCodec.parse(dynamicOps2, object).getOrThrow(error -> new IllegalStateException("Failed to decode: " + error));
	}

	public static class CloneableRegistries {
		private final Map<RegistryKey<? extends Registry<?>>, RegistryCloner<?>> registries = new HashMap();

		public <T> RegistryCloner.CloneableRegistries add(RegistryKey<? extends Registry<? extends T>> registryRef, Codec<T> elementCodec) {
			this.registries.put(registryRef, new RegistryCloner<>(elementCodec));
			return this;
		}

		@Nullable
		public <T> RegistryCloner<T> get(RegistryKey<? extends Registry<? extends T>> registryRef) {
			return (RegistryCloner<T>)this.registries.get(registryRef);
		}
	}
}
