package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public abstract class ValueLookupTagProvider<T> extends TagProvider<T> {
	private final Function<T, RegistryKey<T>> valueToKey;

	public ValueLookupTagProvider(
		DataOutput output,
		RegistryKey<? extends Registry<T>> registryRef,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		Function<T, RegistryKey<T>> valueToKey
	) {
		super(output, registryRef, registryLookupFuture);
		this.valueToKey = valueToKey;
	}

	public ValueLookupTagProvider(
		DataOutput output,
		RegistryKey<? extends Registry<T>> registryRef,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture,
		CompletableFuture<TagProvider.TagLookup<T>> parentTagLookupFuture,
		Function<T, RegistryKey<T>> valueToKey
	) {
		super(output, registryRef, registryLookupFuture, parentTagLookupFuture);
		this.valueToKey = valueToKey;
	}

	protected ValueLookupTagProvider.ObjectBuilder<T> getOrCreateTagBuilder(TagKey<T> tagKey) {
		TagBuilder tagBuilder = this.getTagBuilder(tagKey);
		return new ValueLookupTagProvider.ObjectBuilder<>(tagBuilder, this.valueToKey);
	}

	protected static class ObjectBuilder<T> extends TagProvider.ProvidedTagBuilder<T> {
		private final Function<T, RegistryKey<T>> valueToKey;

		ObjectBuilder(TagBuilder builder, Function<T, RegistryKey<T>> valueToKey) {
			super(builder);
			this.valueToKey = valueToKey;
		}

		public ValueLookupTagProvider.ObjectBuilder<T> addTag(TagKey<T> tagKey) {
			super.addTag(tagKey);
			return this;
		}

		public final ValueLookupTagProvider.ObjectBuilder<T> add(T value) {
			this.add((RegistryKey<T>)this.valueToKey.apply(value));
			return this;
		}

		@SafeVarargs
		public final ValueLookupTagProvider.ObjectBuilder<T> add(T... values) {
			Stream.of(values).map(this.valueToKey).forEach(this::add);
			return this;
		}
	}
}
