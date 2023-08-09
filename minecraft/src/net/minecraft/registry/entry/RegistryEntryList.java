package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * A registry entry list is an immutable list of registry entries. This, is either a direct
 * reference to each item, or a reference to a tag. A <strong>tag</strong> is a way
 * to dynamically define a list of registered values. Anything registered in a registry
 * can be tagged, and each registry holds a list of tags it recognizes.
 * 
 * <p>This can be iterated directly (i.e. {@code for (RegistryEntry<T> entry : entries)}.
 * Note that this does not implement {@link java.util.Collection}.
 * 
 * @see net.minecraft.registry.Registry
 * @see RegistryEntry
 */
public interface RegistryEntryList<T> extends Iterable<RegistryEntry<T>> {
	/**
	 * {@return a stream of registry entries in this list}
	 */
	Stream<RegistryEntry<T>> stream();

	/**
	 * {@return the number of entries in this list}
	 */
	int size();

	/**
	 * {@return the object that identifies this registry entry list}
	 * 
	 * <p>This is the tag key for a reference list, and the backing list for a direct list.
	 */
	Either<TagKey<T>, List<RegistryEntry<T>>> getStorage();

	/**
	 * {@return a random entry of the list, or an empty optional if this list is empty}
	 */
	Optional<RegistryEntry<T>> getRandom(Random random);

	/**
	 * {@return the registry entry at {@code index}}
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	RegistryEntry<T> get(int index);

	/**
	 * {@return whether {@code entry} is in this list}
	 */
	boolean contains(RegistryEntry<T> entry);

	boolean ownerEquals(RegistryEntryOwner<T> owner);

	Optional<TagKey<T>> getTagKey();

	@Deprecated
	@VisibleForTesting
	static <T> RegistryEntryList.Named<T> of(RegistryEntryOwner<T> owner, TagKey<T> tagKey) {
		return new RegistryEntryList.Named<>(owner, tagKey);
	}

	/**
	 * {@return a new direct list of {@code entries}}
	 */
	@SafeVarargs
	static <T> RegistryEntryList.Direct<T> of(RegistryEntry<T>... entries) {
		return new RegistryEntryList.Direct<>(List.of(entries));
	}

	/**
	 * {@return a new direct list of {@code entries}}
	 */
	static <T> RegistryEntryList.Direct<T> of(List<? extends RegistryEntry<T>> entries) {
		return new RegistryEntryList.Direct<>(List.copyOf(entries));
	}

	/**
	 * {@return a new direct list of {@code values} converted to a registry entry with {@code mapper}}
	 */
	@SafeVarargs
	static <E, T> RegistryEntryList.Direct<T> of(Function<E, RegistryEntry<T>> mapper, E... values) {
		return of(Stream.of(values).map(mapper).toList());
	}

	/**
	 * {@return a new direct list of {@code values} converted to a registry entry with {@code mapper}}
	 */
	static <E, T> RegistryEntryList.Direct<T> of(Function<E, RegistryEntry<T>> mapper, Collection<E> values) {
		return of(values.stream().map(mapper).toList());
	}

	/**
	 * A direct registry entry list that holds the values directly, instead of using tags.
	 */
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
		public Optional<TagKey<T>> getTagKey() {
			return Optional.empty();
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

	/**
	 * An internal implementation of {@link RegistryEntryList}.
	 */
	public abstract static class ListBacked<T> implements RegistryEntryList<T> {
		protected abstract List<RegistryEntry<T>> getEntries();

		@Override
		public int size() {
			return this.getEntries().size();
		}

		public Spliterator<RegistryEntry<T>> spliterator() {
			return this.getEntries().spliterator();
		}

		public Iterator<RegistryEntry<T>> iterator() {
			return this.getEntries().iterator();
		}

		@Override
		public Stream<RegistryEntry<T>> stream() {
			return this.getEntries().stream();
		}

		@Override
		public Optional<RegistryEntry<T>> getRandom(Random random) {
			return Util.getRandomOrEmpty(this.getEntries(), random);
		}

		@Override
		public RegistryEntry<T> get(int index) {
			return (RegistryEntry<T>)this.getEntries().get(index);
		}

		@Override
		public boolean ownerEquals(RegistryEntryOwner<T> owner) {
			return true;
		}
	}

	/**
	 * A registry entry list that references a tag from the registry.
	 */
	public static class Named<T> extends RegistryEntryList.ListBacked<T> {
		private final RegistryEntryOwner<T> owner;
		private final TagKey<T> tag;
		private List<RegistryEntry<T>> entries = List.of();

		public Named(RegistryEntryOwner<T> owner, TagKey<T> tag) {
			this.owner = owner;
			this.tag = tag;
		}

		public void copyOf(List<RegistryEntry<T>> entries) {
			this.entries = List.copyOf(entries);
		}

		/**
		 * {@return the tag key that this list references}
		 */
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
		public Optional<TagKey<T>> getTagKey() {
			return Optional.of(this.tag);
		}

		@Override
		public boolean contains(RegistryEntry<T> entry) {
			return entry.isIn(this.tag);
		}

		public String toString() {
			return "NamedSet(" + this.tag + ")[" + this.entries + "]";
		}

		@Override
		public boolean ownerEquals(RegistryEntryOwner<T> owner) {
			return this.owner.ownerEquals(owner);
		}
	}
}
