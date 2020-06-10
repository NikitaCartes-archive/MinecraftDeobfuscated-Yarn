package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;

/**
 * A dynamic ops that encode an id for a registry element rather than a full object.
 * 
 * @see RegistryElementCodec#encode(Object, DynamicOps, Object)
 */
public class RegistryReadingOps<T> extends ForwardingDynamicOps<T> {
	private final RegistryTracker tracker;

	public static <T> RegistryReadingOps<T> of(DynamicOps<T> delegate, RegistryTracker tracker) {
		return new RegistryReadingOps<>(delegate, tracker);
	}

	private RegistryReadingOps(DynamicOps<T> delegate, RegistryTracker tracker) {
		super(delegate);
		this.tracker = tracker;
	}

	/**
	 * Encode an id for a registry element than a full object if possible.
	 * 
	 * <p>This method is called by casting an arbitrary dynamic ops to a registry
	 * reading ops.</p>
	 * 
	 * @see RegistryOps#decodeOrId(Object, RegistryKey, Codec)
	 */
	protected <E> DataResult<T> encodeOrId(E input, T prefix, RegistryKey<Registry<E>> registryReference, MapCodec<E> mapCodec) {
		Optional<MutableRegistry<E>> optional = this.tracker.get(registryReference);
		if (optional.isPresent()) {
			MutableRegistry<E> mutableRegistry = (MutableRegistry<E>)optional.get();
			Optional<RegistryKey<E>> optional2 = mutableRegistry.getKey(input);
			if (optional2.isPresent()) {
				RegistryKey<E> registryKey = (RegistryKey<E>)optional2.get();
				if (mutableRegistry.isLoaded(registryKey)) {
					return NumberCodecs.method_29906(registryReference, mapCodec).codec().encode(Pair.of(registryKey, input), this.delegate, prefix);
				}

				return Identifier.CODEC.encode(registryKey.getValue(), this.delegate, prefix);
			}
		}

		return mapCodec.codec().encode(input, this.delegate, prefix);
	}
}
