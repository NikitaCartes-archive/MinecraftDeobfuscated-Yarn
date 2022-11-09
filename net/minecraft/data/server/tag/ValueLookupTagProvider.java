/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.AbstractTagProvider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public abstract class ValueLookupTagProvider<T>
extends AbstractTagProvider<T> {
    private final Function<T, RegistryKey<T>> valueToKey;

    public ValueLookupTagProvider(DataOutput output, RegistryKey<? extends Registry<T>> registryRef, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, Function<T, RegistryKey<T>> valueToKey) {
        super(output, registryRef, registryLookupFuture);
        this.valueToKey = valueToKey;
    }

    @Override
    protected ObjectBuilder<T> getOrCreateTagBuilder(TagKey<T> tagKey) {
        TagBuilder tagBuilder = this.getTagBuilder(tagKey);
        return new ObjectBuilder<T>(tagBuilder, this.valueToKey);
    }

    @Override
    protected /* synthetic */ AbstractTagProvider.ProvidedTagBuilder getOrCreateTagBuilder(TagKey tag) {
        return this.getOrCreateTagBuilder(tag);
    }

    protected static class ObjectBuilder<T>
    extends AbstractTagProvider.ProvidedTagBuilder<T> {
        private final Function<T, RegistryKey<T>> valueToKey;

        ObjectBuilder(TagBuilder builder, Function<T, RegistryKey<T>> valueToKey) {
            super(builder);
            this.valueToKey = valueToKey;
        }

        @Override
        public ObjectBuilder<T> addTag(TagKey<T> tagKey) {
            super.addTag(tagKey);
            return this;
        }

        public final ObjectBuilder<T> add(T value) {
            ((AbstractTagProvider.ProvidedTagBuilder)this).add(this.valueToKey.apply(value));
            return this;
        }

        @SafeVarargs
        public final ObjectBuilder<T> add(T ... values) {
            Stream.of(values).map(this.valueToKey).forEach(this::add);
            return this;
        }

        @Override
        public /* synthetic */ AbstractTagProvider.ProvidedTagBuilder addTag(TagKey identifiedTag) {
            return this.addTag(identifiedTag);
        }
    }
}

