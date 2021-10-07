/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public abstract class OffsetPredicate
implements BlockPredicate {
    protected final BlockPos offset;

    protected static <P extends OffsetPredicate> Products.P1<RecordCodecBuilder.Mu<P>, BlockPos> method_39013(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ORIGIN).forGetter(offsetPredicate -> offsetPredicate.offset));
    }

    protected OffsetPredicate(BlockPos offset) {
        this.offset = offset;
    }

    @Override
    public final boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
        return this.test(structureWorldAccess.getBlockState(blockPos.add(this.offset)));
    }

    protected abstract boolean test(BlockState var1);

    @Override
    public /* synthetic */ boolean test(Object world, Object pos) {
        return this.test((StructureWorldAccess)world, (BlockPos)pos);
    }
}

