package net.minecraft.predicate.collection;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.predicate.NumberRange;

public record CollectionPredicate<T, P extends Predicate<T>>(
	Optional<CollectionContainsPredicate<T, P>> contains, Optional<CollectionCountsPredicate<T, P>> counts, Optional<NumberRange.IntRange> size
) implements Predicate<Iterable<T>> {
	public static <T, P extends Predicate<T>> Codec<CollectionPredicate<T, P>> createCodec(Codec<P> predicateCodec) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						CollectionContainsPredicate.<T, P>createCodec(predicateCodec).optionalFieldOf("contains").forGetter(CollectionPredicate::contains),
						CollectionCountsPredicate.<T, P>createCodec(predicateCodec).optionalFieldOf("count").forGetter(CollectionPredicate::counts),
						NumberRange.IntRange.CODEC.optionalFieldOf("size").forGetter(CollectionPredicate::size)
					)
					.apply(instance, CollectionPredicate::new)
		);
	}

	public boolean test(Iterable<T> iterable) {
		if (this.contains.isPresent() && !((CollectionContainsPredicate)this.contains.get()).test(iterable)) {
			return false;
		} else {
			return this.counts.isPresent() && !((CollectionCountsPredicate)this.counts.get()).test(iterable)
				? false
				: !this.size.isPresent() || ((NumberRange.IntRange)this.size.get()).test(Iterables.size(iterable));
		}
	}
}
