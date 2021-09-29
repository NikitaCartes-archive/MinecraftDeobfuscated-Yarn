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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;

class MatchingBlocksBlockPredicate
implements BlockPredicate {
    private final List<Block> blocks;
    private final BlockPos pos;
    public static final Codec<MatchingBlocksBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.BLOCK.listOf().fieldOf("blocks")).forGetter(matchingBlocksBlockPredicate -> matchingBlocksBlockPredicate.blocks), ((MapCodec)BlockPos.CODEC.fieldOf("offset")).forGetter(matchingBlocksBlockPredicate -> matchingBlocksBlockPredicate.pos)).apply((Applicative<MatchingBlocksBlockPredicate, ?>)instance, MatchingBlocksBlockPredicate::new));

    public MatchingBlocksBlockPredicate(List<Block> blocks, BlockPos pos) {
        this.blocks = blocks;
        this.pos = pos;
    }

    @Override
    public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
        Block block = structureWorldAccess.getBlockState(blockPos.add(this.pos)).getBlock();
        return this.blocks.contains(block);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_BLOCKS;
    }

    @Override
    public /* synthetic */ boolean test(Object world, Object pos) {
        return this.test((StructureWorldAccess)world, (BlockPos)pos);
    }
}

