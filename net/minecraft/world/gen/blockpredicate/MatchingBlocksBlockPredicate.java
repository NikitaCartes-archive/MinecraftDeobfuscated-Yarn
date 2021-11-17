/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

class MatchingBlocksBlockPredicate
extends OffsetPredicate {
    private final List<Block> blocks;
    public static final Codec<MatchingBlocksBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> MatchingBlocksBlockPredicate.registerOffsetField(instance).and(((MapCodec)Registry.BLOCK.getCodec().listOf().fieldOf("blocks")).forGetter(predicate -> predicate.blocks)).apply((Applicative<MatchingBlocksBlockPredicate, ?>)instance, MatchingBlocksBlockPredicate::new));

    public MatchingBlocksBlockPredicate(Vec3i vec3i, List<Block> blocks) {
        super(vec3i);
        this.blocks = blocks;
    }

    @Override
    protected boolean test(BlockState state) {
        return this.blocks.contains(state.getBlock());
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_BLOCKS;
    }
}

