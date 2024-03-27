package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;

abstract class CombinedBlockPredicate implements BlockPredicate {
	protected final List<BlockPredicate> predicates;

	protected CombinedBlockPredicate(List<BlockPredicate> predicates) {
		this.predicates = predicates;
	}

	public static <T extends CombinedBlockPredicate> MapCodec<T> buildCodec(Function<List<BlockPredicate>, T> combiner) {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(BlockPredicate.BASE_CODEC.listOf().fieldOf("predicates").forGetter(predicate -> predicate.predicates)).apply(instance, combiner)
		);
	}
}
