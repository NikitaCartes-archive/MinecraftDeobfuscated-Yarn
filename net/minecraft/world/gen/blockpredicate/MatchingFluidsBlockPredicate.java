/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;

class MatchingFluidsBlockPredicate
implements BlockPredicate {
    private final List<Fluid> fluids;
    private final BlockPos pos;
    public static final Codec<MatchingFluidsBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.FLUID.listOf().fieldOf("fluids")).forGetter(matchingFluidsBlockPredicate -> matchingFluidsBlockPredicate.fluids), ((MapCodec)BlockPos.CODEC.fieldOf("offset")).forGetter(matchingFluidsBlockPredicate -> matchingFluidsBlockPredicate.pos)).apply((Applicative<MatchingFluidsBlockPredicate, ?>)instance, MatchingFluidsBlockPredicate::new));

    public MatchingFluidsBlockPredicate(List<Fluid> fluids, BlockPos pos) {
        this.fluids = fluids;
        this.pos = pos;
    }

    @Override
    public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
        return this.fluids.contains(structureWorldAccess.getFluidState(blockPos.add(this.pos)).getFluid());
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_FLUIDS;
    }

    @Override
    public /* synthetic */ boolean test(Object world, Object pos) {
        return this.test((StructureWorldAccess)world, (BlockPos)pos);
    }
}

