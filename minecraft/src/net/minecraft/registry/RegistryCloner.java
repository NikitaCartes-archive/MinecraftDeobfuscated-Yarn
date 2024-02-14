package net.minecraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RuntimeOps;

public class RegistryCloner<T> {
	private final Codec<T> elementCodec;

	RegistryCloner(Codec<T> elementCodec) {
		this.elementCodec = elementCodec;
	}

	public T clone(T value, RegistryWrapper.WrapperLookup subsetRegistry, RegistryWrapper.WrapperLookup fullRegistry) {
		DynamicOps<Object> dynamicOps = subsetRegistry.getOps(RuntimeOps.INSTANCE);
		DynamicOps<Object> dynamicOps2 = fullRegistry.getOps(RuntimeOps.INSTANCE);
		Object object = Util.getResult(this.elementCodec.encodeStart(dynamicOps, value), error -> new IllegalStateException("Failed to encode: " + error));
		return Util.getResult(this.elementCodec.parse(dynamicOps2, object), error -> new IllegalStateException("Failed to decode: " + error));
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
