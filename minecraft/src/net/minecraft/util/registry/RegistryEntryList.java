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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RegistryEntryList<T> extends Iterable<RegistryEntry<T>> {
	Stream<RegistryEntry<T>> stream();

	int size();

	Either<TagKey<T>, List<RegistryEntry<T>>> getStorage();

	Optional<RegistryEntry<T>> getRandom(AbstractRandom random);

	RegistryEntry<T> get(int index);

	boolean contains(RegistryEntry<T> entry);

	boolean isOf(Registry<T> registry);

	@SafeVarargs
	static <T> RegistryEntryList.Direct<T> of(RegistryEntry<T>... entries) {
		return new RegistryEntryList.Direct<>(List.of(entries));
	}

	static <T> RegistryEntryList.Direct<T> of(List<? extends RegistryEntry<T>> entries) {
		return new RegistryEntryList.Direct<>(List.copyOf(entries));
	}

	@SafeVarargs
	static <E, T> RegistryEntryList.Direct<T> of(Function<E, RegistryEntry<T>> mapper, E... values) {
		return of(Stream.of(values).map(mapper).toList());
	}

	static <E, T> RegistryEntryList.Direct<T> of(Function<E, RegistryEntry<T>> mapper, List<E> values) {
		return of(values.stream().map(mapper).toList());
	}

	public static class Direct<T> extends RegistryEntryList.ListBacked<T> {
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

	public abstract static class ListBacked<T> implements RegistryEntryList<T> {
		protected abstract List<RegistryEntry<T>> getEntries();

		@Override
		public int size() {
			return this.getEntries().size();
		}

		public Spliterator<RegistryEntry<T>> spliterator() {
			return this.getEntries().spliterator();
		}

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
			return (RegistryEntry<T>)this.getEntries().get(index);
		}

		@Override
		public boolean isOf(Registry<T> registry) {
			return true;
		}
	}

	public static class Named<T> extends RegistryEntryList.ListBacked<T> {
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
}
