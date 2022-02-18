package net.minecraft.tag;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public record TagKey<T>(RegistryKey<? extends Registry<T>> registry, Identifier id) {
	private static final Interner<TagKey<?>> INTERNER = Interners.newStrongInterner();

	@Deprecated
	public TagKey(RegistryKey<? extends Registry<T>> registry, Identifier id) {
		this.registry = registry;
		this.id = id;
	}

	public static <T> Codec<TagKey<T>> identifierCodec(RegistryKey<? extends Registry<T>> registry) {
		return Identifier.CODEC.xmap(id -> of(registry, id), TagKey::id);
	}

	public static <T> Codec<TagKey<T>> stringCodec(RegistryKey<? extends Registry<T>> registry) {
		return Codec.STRING
			.comapFlatMap(
				string -> string.startsWith("#") ? Identifier.validate(string.substring(1)).map(id -> of(registry, id)) : DataResult.error("Not a tag id"),
				string -> "#" + string.id
			);
	}

	public static <T> TagKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier id) {
		return (TagKey<T>)INTERNER.intern(new TagKey<>(registry, id));
	}

	public boolean method_41007(RegistryKey<? extends Registry<?>> registryKey) {
		return this.registry == registryKey;
	}

	public <E> Optional<TagKey<E>> method_41008(RegistryKey<? extends Registry<E>> registryKey) {
		return this.method_41007(registryKey) ? Optional.of(this) : Optional.empty();
	}

	public String toString() {
		return "TagKey[" + this.registry.getValue() + " / " + this.id + "]";
	}
}
