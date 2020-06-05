package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
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
	protected <E> DataResult<T> encodeOrId(E input, T prefix, RegistryKey<Registry<E>> registryReference, Codec<E> codec) {
		Optional<MutableRegistry<E>> optional = this.tracker.get(registryReference);
		if (optional.isPresent()) {
			Optional<RegistryKey<E>> optional2 = ((MutableRegistry)optional.get()).getKey(input);
			if (optional2.isPresent()) {
				return Identifier.CODEC.encode(((RegistryKey)optional2.get()).getValue(), this.delegate, prefix);
			}
		}

		return codec.encode(input, this.delegate, prefix);
	}
}
