/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.CombinedBlockPredicate;

class AllOfBlockPredicate
extends CombinedBlockPredicate {
    public static final Codec<AllOfBlockPredicate> CODEC = AllOfBlockPredicate.buildCodec(AllOfBlockPredicate::new);

    public AllOfBlockPredicate(List<BlockPredicate> list) {
        super(list);
    }

    @Override
    public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
        for (BlockPredicate blockPredicate : this.predicates) {
            if (blockPredicate.test(structureWorldAccess, blockPos)) continue;
            return false;
        }
        return true;
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.ALL_OF;
    }

    @Override
    public /* synthetic */ boolean test(Object world, Object pos) {
        return this.test((StructureWorldAccess)world, (BlockPos)pos);
    }
}

