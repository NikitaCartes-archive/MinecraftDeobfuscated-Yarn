/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;

class ReplaceableBlockPredicate
implements BlockPredicate {
    public static final ReplaceableBlockPredicate INSTANCE = new ReplaceableBlockPredicate();
    public static final Codec<ReplaceableBlockPredicate> CODEC = Codec.unit(() -> INSTANCE);

    private ReplaceableBlockPredicate() {
    }

    @Override
    public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
        return structureWorldAccess.getBlockState(blockPos).getMaterial().isReplaceable();
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.REPLACEABLE;
    }

    @Override
    public /* synthetic */ boolean test(Object world, Object pos) {
        return this.test((StructureWorldAccess)world, (BlockPos)pos);
    }
}

