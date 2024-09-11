package net.minecraft.registry.tag;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public record TagKey<T>(RegistryKey<? extends Registry<T>> registryRef, Identifier id) {
	private static final Interner<TagKey<?>> INTERNER = Interners.newWeakInterner();

	@Deprecated
	public TagKey(RegistryKey<? extends Registry<T>> registryRef, Identifier id) {
		this.registryRef = registryRef;
		this.id = id;
	}

	public static <T> Codec<TagKey<T>> unprefixedCodec(RegistryKey<? extends Registry<T>> registryRef) {
		return Identifier.CODEC.xmap(id -> of(registryRef, id), TagKey::id);
	}

	public static <T> Codec<TagKey<T>> codec(RegistryKey<? extends Registry<T>> registryRef) {
		return Codec.STRING
			.comapFlatMap(
				string -> string.startsWith("#") ? Identifier.validate(string.substring(1)).map(id -> of(registryRef, id)) : DataResult.error(() -> "Not a tag id"),
				string -> "#" + string.id
			);
	}

	public static <T> PacketCodec<ByteBuf, TagKey<T>> packetCodec(RegistryKey<? extends Registry<T>> registryRef) {
		return Identifier.PACKET_CODEC.xmap(id -> of(registryRef, id), TagKey::id);
	}

	public static <T> TagKey<T> of(RegistryKey<? extends Registry<T>> registryRef, Identifier id) {
		return (TagKey<T>)INTERNER.intern(new TagKey<>(registryRef, id));
	}

	public boolean isOf(RegistryKey<? extends Registry<?>> registryRef) {
		return this.registryRef == registryRef;
	}

	public <E> Optional<TagKey<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
		return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
	}

	public String toString() {
		return "TagKey[" + this.registryRef.getValue() + " / " + this.id + "]";
	}
}
