package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public record class_9791<T>(Optional<RegistryEntry<T>> holder, RegistryKey<T> key) {
	public class_9791(RegistryEntry<T> registryEntry) {
		this(Optional.of(registryEntry), (RegistryKey<T>)registryEntry.getKey().orElseThrow());
	}

	public class_9791(RegistryKey<T> registryKey) {
		this(Optional.empty(), registryKey);
	}

	public static <T> Codec<class_9791<T>> method_60736(RegistryKey<Registry<T>> registryKey, Codec<RegistryEntry<T>> codec) {
		return Codec.either(
				codec,
				RegistryKey.createCodec(registryKey).comapFlatMap(registryKeyx -> DataResult.error(() -> "Cannot parse as key without registry"), Function.identity())
			)
			.xmap(class_9791::method_60738, class_9791::method_60734);
	}

	public static <T> PacketCodec<RegistryByteBuf, class_9791<T>> method_60737(
		RegistryKey<Registry<T>> registryKey, PacketCodec<RegistryByteBuf, RegistryEntry<T>> packetCodec
	) {
		return PacketCodec.tuple(PacketCodecs.either(packetCodec, RegistryKey.createPacketCodec(registryKey)), class_9791::method_60734, class_9791::method_60738);
	}

	public Either<RegistryEntry<T>, RegistryKey<T>> method_60734() {
		return (Either<RegistryEntry<T>, RegistryKey<T>>)this.holder.map(Either::left).orElseGet(() -> Either.right(this.key));
	}

	public static <T> class_9791<T> method_60738(Either<RegistryEntry<T>, RegistryKey<T>> either) {
		return either.map(class_9791::new, class_9791::new);
	}

	public Optional<T> method_60740(Registry<T> registry) {
		return this.holder.map(RegistryEntry::value).or(() -> registry.getOrEmpty(this.key));
	}

	public Optional<RegistryEntry<T>> method_60739(RegistryWrapper.WrapperLookup wrapperLookup) {
		return this.holder.or(() -> wrapperLookup.getWrapperOrThrow(this.key.getRegistryRef()).getOptional(this.key));
	}
}
