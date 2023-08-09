package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public record TagPredicate<T>(TagKey<T> tag, boolean expected) {
	public static <T> Codec<TagPredicate<T>> createCodec(RegistryKey<? extends Registry<T>> registryRef) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						TagKey.unprefixedCodec(registryRef).fieldOf("id").forGetter(TagPredicate::tag), Codec.BOOL.fieldOf("expected").forGetter(TagPredicate::expected)
					)
					.apply(instance, TagPredicate::new)
		);
	}

	public static <T> TagPredicate<T> expected(TagKey<T> tag) {
		return new TagPredicate<>(tag, true);
	}

	public static <T> TagPredicate<T> unexpected(TagKey<T> tag) {
		return new TagPredicate<>(tag, false);
	}

	public boolean test(RegistryEntry<T> registryEntry) {
		return registryEntry.isIn(this.tag) == this.expected;
	}
}
