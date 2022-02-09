package net.minecraft.util.dynamic;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RegistryOps<T> extends ForwardingDynamicOps<T> {
	private final Optional<RegistryLoader.LoaderAccess> loaderAccess;
	private final DynamicRegistryManager registryManager;
	private final DynamicOps<JsonElement> entryOps;

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, DynamicRegistryManager registryManager) {
		return new RegistryOps<>(delegate, registryManager, Optional.empty());
	}

	public static <T> RegistryOps<T> ofLoaded(DynamicOps<T> ops, DynamicRegistryManager.Mutable registryManager, ResourceManager resourceManager) {
		return ofLoaded(ops, registryManager, EntryLoader.resourceBacked(resourceManager));
	}

	public static <T> RegistryOps<T> ofLoaded(DynamicOps<T> ops, DynamicRegistryManager.Mutable registryManager, EntryLoader entryLoader) {
		RegistryLoader registryLoader = new RegistryLoader(entryLoader);
		RegistryOps<T> registryOps = new RegistryOps<>(ops, registryManager, Optional.of(registryLoader.createAccess(registryManager)));
		DynamicRegistryManager.load(registryManager, registryOps.getEntryOps(), registryLoader);
		return registryOps;
	}

	private RegistryOps(DynamicOps<T> delegate, DynamicRegistryManager dynamicRegistryManager, Optional<RegistryLoader.LoaderAccess> loaderAccess) {
		super(delegate);
		this.loaderAccess = loaderAccess;
		this.registryManager = dynamicRegistryManager;
		this.entryOps = delegate == JsonOps.INSTANCE ? this : new RegistryOps<>(JsonOps.INSTANCE, dynamicRegistryManager, loaderAccess);
	}

	public <E> Optional<? extends Registry<E>> getRegistry(RegistryKey<? extends Registry<? extends E>> key) {
		return this.registryManager.getOptional(key);
	}

	public Optional<RegistryLoader.LoaderAccess> getLoaderAccess() {
		return this.loaderAccess;
	}

	public DynamicOps<JsonElement> getEntryOps() {
		return this.entryOps;
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
