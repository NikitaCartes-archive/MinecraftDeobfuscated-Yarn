package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RegistryOps<T> extends ForwardingDynamicOps<T> {
	private final DynamicRegistryManager registryManager;

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, DynamicRegistryManager registryManager) {
		return new RegistryOps<>(delegate, registryManager);
	}

	private RegistryOps(DynamicOps<T> delegate, DynamicRegistryManager dynamicRegistryManager) {
		super(delegate);
		this.registryManager = dynamicRegistryManager;
	}

	public <E> Optional<? extends Registry<E>> getRegistry(RegistryKey<? extends Registry<? extends E>> key) {
		return this.registryManager.getOptional(key);
	}

	public static <E> MapCodec<Registry<E>> createRegistryCodec(RegistryKey<? extends Registry<? extends E>> registryRef) {
		return Codecs.createContextRetrievalCodec(
			ops -> ops instanceof RegistryOps<?> registryOps
					? (DataResult)registryOps.getRegistry(registryRef)
						.map(registry -> DataResult.success(registry, registry.getLifecycle()))
						.orElseGet(() -> DataResult.error("Unknown registry: " + registryRef))
					: DataResult.error("Not a registry ops")
		);
	}
}
