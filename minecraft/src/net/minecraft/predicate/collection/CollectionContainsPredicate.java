package net.minecraft.predicate.collection;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionContainsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
	List<P> getPredicates();

	static <T, P extends Predicate<T>> Codec<CollectionContainsPredicate<T, P>> createCodec(Codec<P> predicateCodec) {
		return predicateCodec.listOf().xmap(CollectionContainsPredicate::create, CollectionContainsPredicate::getPredicates);
	}

	@SafeVarargs
	static <T, P extends Predicate<T>> CollectionContainsPredicate<T, P> create(P... predicates) {
		return create(List.of(predicates));
	}

	static <T, P extends Predicate<T>> CollectionContainsPredicate<T, P> create(List<P> predicates) {
		return (CollectionContainsPredicate<T, P>)(switch (predicates.size()) {
			case 0 -> new CollectionContainsPredicate.Empty();
			case 1 -> new CollectionContainsPredicate.Single((P)predicates.getFirst());
			default -> new CollectionContainsPredicate.Multiple(predicates);
		});
	}

	public static class Empty<T, P extends Predicate<T>> implements CollectionContainsPredicate<T, P> {
		public boolean test(Iterable<T> iterable) {
			return true;
		}

		@Override
		public List<P> getPredicates() {
			return List.of();
		}
	}

	public static record Multiple<T, P extends Predicate<T>>(List<P> tests) implements CollectionContainsPredicate<T, P> {
		public boolean test(Iterable<T> iterable) {
			List<Predicate<T>> list = new ArrayList(this.tests);

			for (T object : iterable) {
				list.removeIf(predicate -> predicate.test(object));
				if (list.isEmpty()) {
					return true;
				}
			}

			return false;
		}

		@Override
		public List<P> getPredicates() {
			return this.tests;
		}
	}

	public static record Single<T, P extends Predicate<T>>(P test) implements CollectionContainsPredicate<T, P> {
		public boolean test(Iterable<T> iterable) {
			for (T object : iterable) {
				if (this.test.test(object)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public List<P> getPredicates() {
			return List.of(this.test);
		}
	}
}
