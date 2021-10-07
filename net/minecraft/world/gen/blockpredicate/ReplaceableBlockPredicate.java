/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

class ReplaceableBlockPredicate
extends OffsetPredicate {
    public static final Codec<ReplaceableBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> ReplaceableBlockPredicate.method_39013(instance).apply(instance, ReplaceableBlockPredicate::new));

    public ReplaceableBlockPredicate(BlockPos blockPos) {
        super(blockPos);
    }

    @Override
    protected boolean test(BlockState state) {
        return state.getMaterial().isReplaceable();
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.REPLACEABLE;
    }
}

