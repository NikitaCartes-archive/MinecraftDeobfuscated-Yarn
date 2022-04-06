/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RegistryEntryList<T>
extends Iterable<RegistryEntry<T>> {
    public Stream<RegistryEntry<T>> stream();

    public int size();

    public Either<TagKey<T>, List<RegistryEntry<T>>> getStorage();

    public Optional<RegistryEntry<T>> getRandom(AbstractRandom var1);

    public RegistryEntry<T> get(int var1);

    public boolean contains(RegistryEntry<T> var1);

    public boolean isOf(Registry<T> var1);

    @SafeVarargs
    public static <T> Direct<T> of(RegistryEntry<T> ... entries) {
        return new Direct<T>(List.of(entries));
    }

    public static <T> Direct<T> of(List<? extends RegistryEntry<T>> entries) {
        return new Direct(List.copyOf(entries));
    }

    @SafeVarargs
    public static <E, T> Direct<T> of(Function<E, RegistryEntry<T>> mapper, E ... values) {
        return RegistryEntryList.of(Stream.of(values).map(mapper).toList());
    }

    public static <E, T> Direct<T> of(Function<E, RegistryEntry<T>> mapper, List<E> values) {
        return RegistryEntryList.of(values.stream().map(mapper).toList());
    }

    public static class Direct<T>
    extends ListBacked<T> {
        private final List<RegistryEntry<T>> entries;
        @Nullable
        private Set<RegistryEntry<T>> entrySet;

        Direct(List<RegistryEntry<T>> entries) {
            this.entries = entries;
        }

        @Override
        protected List<RegistryEntry<T>> getEntries() {
            return this.entries;
        }

        @Override
        public Either<TagKey<T>, List<RegistryEntry<T>>> getStorage() {
            return Either.right(this.entries);
        }

        @Override
        public boolean contains(RegistryEntry<T> entry) {
            if (this.entrySet == null) {
                this.entrySet = Set.copyOf(this.entries);
            }
            return this.entrySet.contains(entry);
        }

        public String toString() {
            return "DirectSet[" + this.entries + "]";
        }
    }

    public static class Named<T>
    extends ListBacked<T> {
        private final Registry<T> registry;
        private final TagKey<T> tag;
        private List<RegistryEntry<T>> entries = List.of();

        Named(Registry<T> registry, TagKey<T> tag) {
            this.registry = registry;
            this.tag = tag;
        }

        void copyOf(List<RegistryEntry<T>> entries) {
            this.entries = List.copyOf(entries);
        }

        public TagKey<T> getTag() {
            return this.tag;
        }

        @Override
        protected List<RegistryEntry<T>> getEntries() {
            return this.entries;
        }

        @Override
        public Either<TagKey<T>, List<RegistryEntry<T>>> getStorage() {
            return Either.left(this.tag);
        }

        @Override
        public boolean contains(RegistryEntry<T> entry) {
            return entry.isIn(this.tag);
        }

        public String toString() {
            return "NamedSet(" + this.tag + ")[" + this.entries + "]";
        }

        @Override
        public boolean isOf(Registry<T> registry) {
            return this.registry == registry;
        }
    }

    public static abstract class ListBacked<T>
    implements RegistryEntryList<T> {
        protected abstract List<RegistryEntry<T>> getEntries();

        @Override
        public int size() {
            return this.getEntries().size();
        }

        @Override
        public Spliterator<RegistryEntry<T>> spliterator() {
            return this.getEntries().spliterator();
        }

        @Override
        @NotNull
        public Iterator<RegistryEntry<T>> iterator() {
            return this.getEntries().iterator();
        }

        @Override
        public Stream<RegistryEntry<T>> stream() {
            return this.getEntries().stream();
        }

        @Override
        public Optional<RegistryEntry<T>> getRandom(AbstractRandom random) {
            return Util.getRandomOrEmpty(this.getEntries(), random);
        }

        @Override
        public RegistryEntry<T> get(int index) {
            return this.getEntries().get(index);
        }

        @Override
        public boolean isOf(Registry<T> registry) {
            return true;
        }
    }
}

