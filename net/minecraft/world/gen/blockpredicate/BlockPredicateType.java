/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.blockpredicate.AllOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.AlwaysTrueBlockPredicate;
import net.minecraft.world.gen.blockpredicate.AnyOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.HasSturdyFacePredicate;
import net.minecraft.world.gen.blockpredicate.InsideWorldBoundsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingBlockTagPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingBlocksBlockPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingFluidsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.NotBlockPredicate;
import net.minecraft.world.gen.blockpredicate.ReplaceableBlockPredicate;
import net.minecraft.world.gen.blockpredicate.SolidBlockPredicate;
import net.minecraft.world.gen.blockpredicate.WouldSurviveBlockPredicate;

public interface BlockPredicateType<P extends BlockPredicate> {
    public static final BlockPredicateType<MatchingBlocksBlockPredicate> MATCHING_BLOCKS = BlockPredicateType.register("matching_blocks", MatchingBlocksBlockPredicate.CODEC);
    public static final BlockPredicateType<MatchingBlockTagPredicate> MATCHING_BLOCK_TAG = BlockPredicateType.register("matching_block_tag", MatchingBlockTagPredicate.CODEC);
    public static final BlockPredicateType<MatchingFluidsBlockPredicate> MATCHING_FLUIDS = BlockPredicateType.register("matching_fluids", MatchingFluidsBlockPredicate.CODEC);
    public static final BlockPredicateType<HasSturdyFacePredicate> HAS_STURDY_FACE = BlockPredicateType.register("has_sturdy_face", HasSturdyFacePredicate.CODEC);
    public static final BlockPredicateType<SolidBlockPredicate> SOLID = BlockPredicateType.register("solid", SolidBlockPredicate.CODEC);
    public static final BlockPredicateType<ReplaceableBlockPredicate> REPLACEABLE = BlockPredicateType.register("replaceable", ReplaceableBlockPredicate.CODEC);
    public static final BlockPredicateType<WouldSurviveBlockPredicate> WOULD_SURVIVE = BlockPredicateType.register("would_survive", WouldSurviveBlockPredicate.CODEC);
    public static final BlockPredicateType<InsideWorldBoundsBlockPredicate> INSIDE_WORLD_BOUNDS = BlockPredicateType.register("inside_world_bounds", InsideWorldBoundsBlockPredicate.CODEC);
    public static final BlockPredicateType<AnyOfBlockPredicate> ANY_OF = BlockPredicateType.register("any_of", AnyOfBlockPredicate.CODEC);
    public static final BlockPredicateType<AllOfBlockPredicate> ALL_OF = BlockPredicateType.register("all_of", AllOfBlockPredicate.CODEC);
    public static final BlockPredicateType<NotBlockPredicate> NOT = BlockPredicateType.register("not", NotBlockPredicate.CODEC);
    public static final BlockPredicateType<AlwaysTrueBlockPredicate> TRUE = BlockPredicateType.register("true", AlwaysTrueBlockPredicate.CODEC);

    public Codec<P> codec();

    private static <P extends BlockPredicate> BlockPredicateType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.BLOCK_PREDICATE_TYPE, id, () -> codec);
    }
}

