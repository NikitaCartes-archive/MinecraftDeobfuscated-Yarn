package net.minecraft.registry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;

public record RegistryPair<T>(Optional<RegistryEntry<T>> entry, RegistryKey<T> key) {
	public RegistryPair(RegistryEntry<T> entry) {
		this(Optional.of(entry), (RegistryKey<T>)entry.getKey().orElseThrow());
	}

	public RegistryPair(RegistryKey<T> key) {
		this(Optional.empty(), key);
	}

	public static <T> Codec<RegistryPair<T>> createCodec(RegistryKey<Registry<T>> registryRef, Codec<RegistryEntry<T>> entryCodec) {
		return Codec.either(
				entryCodec,
				RegistryKey.createCodec(registryRef).comapFlatMap(registryKey -> DataResult.error(() -> "Cannot parse as key without registry"), Function.identity())
			)
			.xmap(RegistryPair::create, RegistryPair::asEither);
	}

	public static <T> PacketCodec<RegistryByteBuf, RegistryPair<T>> createPacketCodec(
		RegistryKey<Registry<T>> registryRef, PacketCodec<RegistryByteBuf, RegistryEntry<T>> entryPacketCodec
	) {
		return PacketCodec.tuple(PacketCodecs.either(entryPacketCodec, RegistryKey.createPacketCodec(registryRef)), RegistryPair::asEither, RegistryPair::create);
	}

	public Either<RegistryEntry<T>, RegistryKey<T>> asEither() {
		return (Either<RegistryEntry<T>, RegistryKey<T>>)this.entry.map(Either::left).orElseGet(() -> Either.right(this.key));
	}

	public static <T> RegistryPair<T> create(Either<RegistryEntry<T>, RegistryKey<T>> entryOrKey) {
		return entryOrKey.map(RegistryPair::new, RegistryPair::new);
	}

	public Optional<T> getValue(Registry<T> registry) {
		return this.entry.map(RegistryEntry::value).or(() -> registry.getOrEmpty(this.key));
	}

	public Optional<RegistryEntry<T>> getEntry(RegistryWrapper.WrapperLookup registryLookup) {
		return this.entry.or(() -> registryLookup.getWrapperOrThrow(this.key.getRegistryRef()).getOptional(this.key));
	}
}
