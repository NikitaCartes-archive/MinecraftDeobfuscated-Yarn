package net.minecraft.registry.tag;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public record TagKey<T>(RegistryKey<? extends Registry<T>> registry, Identifier id) {
	private static final Interner<TagKey<?>> INTERNER = Interners.newWeakInterner();

	@Deprecated
	public TagKey(RegistryKey<? extends Registry<T>> registry, Identifier id) {
		this.registry = registry;
		this.id = id;
	}

	public static <T> Codec<TagKey<T>> unprefixedCodec(RegistryKey<? extends Registry<T>> registry) {
		return Identifier.CODEC.xmap(id -> of(registry, id), TagKey::id);
	}

	public static <T> Codec<TagKey<T>> codec(RegistryKey<? extends Registry<T>> registry) {
		return Codec.STRING
			.comapFlatMap(
				string -> string.startsWith("#") ? Identifier.validate(string.substring(1)).map(id -> of(registry, id)) : DataResult.error(() -> "Not a tag id"),
				string -> "#" + string.id
			);
	}

	public static <T> TagKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier id) {
		return (TagKey<T>)INTERNER.intern(new TagKey<>(registry, id));
	}

	public boolean isOf(RegistryKey<? extends Registry<?>> registryRef) {
		return this.registry == registryRef;
	}

	public <E> Optional<TagKey<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
		return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
	}

	public String toString() {
		return "TagKey[" + this.registry.getValue() + " / " + this.id + "]";
	}
}
