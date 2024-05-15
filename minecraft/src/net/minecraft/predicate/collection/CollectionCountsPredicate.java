package net.minecraft.predicate.collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.NumberRange;

public interface CollectionCountsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
	List<CollectionCountsPredicate.Entry<T, P>> getEntries();

	static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate<T, P>> createCodec(Codec<P> predicateCodec) {
		return CollectionCountsPredicate.Entry.<T, P>createCodec(predicateCodec)
			.listOf()
			.xmap(CollectionCountsPredicate::create, CollectionCountsPredicate::getEntries);
	}

	@SafeVarargs
	static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> create(CollectionCountsPredicate.Entry<T, P>... entries) {
		return create(List.of(entries));
	}

	static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> create(List<CollectionCountsPredicate.Entry<T, P>> entries) {
		return (CollectionCountsPredicate<T, P>)(switch (entries.size()) {
			case 0 -> new CollectionCountsPredicate.Empty();
			case 1 -> new CollectionCountsPredicate.Single((CollectionCountsPredicate.Entry<T, P>)entries.getFirst());
			default -> new CollectionCountsPredicate.Multiple(entries);
		});
	}

	public static class Empty<T, P extends Predicate<T>> implements CollectionCountsPredicate<T, P> {
		public boolean test(Iterable<T> iterable) {
			return true;
		}

		@Override
		public List<CollectionCountsPredicate.Entry<T, P>> getEntries() {
			return List.of();
		}
	}

	public static record Entry<T, P extends Predicate<T>>(P test, NumberRange.IntRange count) {
		public static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate.Entry<T, P>> createCodec(Codec<P> predicateCodec) {
			return RecordCodecBuilder.create(
				instance -> instance.group(
							predicateCodec.fieldOf("test").forGetter(CollectionCountsPredicate.Entry::test),
							NumberRange.IntRange.CODEC.fieldOf("count").forGetter(CollectionCountsPredicate.Entry::count)
						)
						.apply(instance, CollectionCountsPredicate.Entry::new)
			);
		}

		public boolean test(Iterable<T> collection) {
			int i = 0;

			for (T object : collection) {
				if (this.test.test(object)) {
					i++;
				}
			}

			return this.count.test(i);
		}
	}

	public static record Multiple<T, P extends Predicate<T>>(List<CollectionCountsPredicate.Entry<T, P>> entries) implements CollectionCountsPredicate<T, P> {
		public boolean test(Iterable<T> iterable) {
			for (CollectionCountsPredicate.Entry<T, P> entry : this.entries) {
				if (!entry.test(iterable)) {
					return false;
				}
			}

			return true;
		}

		@Override
		public List<CollectionCountsPredicate.Entry<T, P>> getEntries() {
			return this.entries;
		}
	}

	public static record Single<T, P extends Predicate<T>>(CollectionCountsPredicate.Entry<T, P> entry) implements CollectionCountsPredicate<T, P> {
		public boolean test(Iterable<T> iterable) {
			return this.entry.test(iterable);
		}

		@Override
		public List<CollectionCountsPredicate.Entry<T, P>> getEntries() {
			return List.of(this.entry);
		}
	}
}
