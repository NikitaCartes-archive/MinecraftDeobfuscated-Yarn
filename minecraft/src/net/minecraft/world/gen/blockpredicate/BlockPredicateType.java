package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface BlockPredicateType<P extends BlockPredicate> {
	BlockPredicateType<MatchingBlocksBlockPredicate> MATCHING_BLOCKS = register("matching_blocks", MatchingBlocksBlockPredicate.CODEC);
	BlockPredicateType<MatchingBlockTagPredicate> MATCHING_BLOCK_TAG = register("matching_block_tag", MatchingBlockTagPredicate.CODEC);
	BlockPredicateType<MatchingFluidsBlockPredicate> MATCHING_FLUIDS = register("matching_fluids", MatchingFluidsBlockPredicate.CODEC);
	BlockPredicateType<HasSturdyFacePredicate> HAS_STURDY_FACE = register("has_sturdy_face", HasSturdyFacePredicate.CODEC);
	BlockPredicateType<SolidBlockPredicate> SOLID = register("solid", SolidBlockPredicate.CODEC);
	BlockPredicateType<ReplaceableBlockPredicate> REPLACEABLE = register("replaceable", ReplaceableBlockPredicate.CODEC);
	BlockPredicateType<WouldSurviveBlockPredicate> WOULD_SURVIVE = register("would_survive", WouldSurviveBlockPredicate.CODEC);
	BlockPredicateType<InsideWorldBoundsBlockPredicate> INSIDE_WORLD_BOUNDS = register("inside_world_bounds", InsideWorldBoundsBlockPredicate.CODEC);
	BlockPredicateType<AnyOfBlockPredicate> ANY_OF = register("any_of", AnyOfBlockPredicate.CODEC);
	BlockPredicateType<AllOfBlockPredicate> ALL_OF = register("all_of", AllOfBlockPredicate.CODEC);
	BlockPredicateType<NotBlockPredicate> NOT = register("not", NotBlockPredicate.CODEC);
	BlockPredicateType<AlwaysTrueBlockPredicate> TRUE = register("true", AlwaysTrueBlockPredicate.CODEC);

	Codec<P> codec();

	private static <P extends BlockPredicate> BlockPredicateType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.BLOCK_PREDICATE_TYPE, id, () -> codec);
	}
}
