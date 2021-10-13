package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface BlockPredicateType<P extends BlockPredicate> {
	BlockPredicateType<MatchingBlocksBlockPredicate> MATCHING_BLOCKS = register("matching_blocks", MatchingBlocksBlockPredicate.CODEC);
	BlockPredicateType<MatchingFluidsBlockPredicate> MATCHING_FLUIDS = register("matching_fluids", MatchingFluidsBlockPredicate.CODEC);
	BlockPredicateType<ReplaceableBlockPredicate> REPLACEABLE = register("replaceable", ReplaceableBlockPredicate.CODEC);
	BlockPredicateType<WouldSurviveBlockPredicate> WOULD_SURVIVE = register("would_survive", WouldSurviveBlockPredicate.CODEC);
	BlockPredicateType<AnyOfBlockPredicate> ANY_OF = register("any_of", AnyOfBlockPredicate.CODEC);
	BlockPredicateType<AllOfBlockPredicate> ALL_OF = register("all_of", AllOfBlockPredicate.CODEC);
	BlockPredicateType<NotBlockPredicate> NOT = register("not", NotBlockPredicate.CODEC);
	BlockPredicateType<TrueBlockPredicate> TRUE = register("true", TrueBlockPredicate.CODEC);

	Codec<P> codec();

	private static <P extends BlockPredicate> BlockPredicateType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.BLOCK_PREDICATE_TYPE, id, () -> codec);
	}
}
