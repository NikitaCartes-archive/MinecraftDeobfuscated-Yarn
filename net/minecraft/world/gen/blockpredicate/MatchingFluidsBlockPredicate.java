/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

class MatchingFluidsBlockPredicate
extends OffsetPredicate {
    private final RegistryEntryList<Fluid> fluids;
    public static final Codec<MatchingFluidsBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> MatchingFluidsBlockPredicate.registerOffsetField(instance).and(((MapCodec)RegistryCodecs.entryList(RegistryKeys.FLUID).fieldOf("fluids")).forGetter(predicate -> predicate.fluids)).apply((Applicative<MatchingFluidsBlockPredicate, ?>)instance, MatchingFluidsBlockPredicate::new));

    public MatchingFluidsBlockPredicate(Vec3i offset, RegistryEntryList<Fluid> fluids) {
        super(offset);
        this.fluids = fluids;
    }

    @Override
    protected boolean test(BlockState state) {
        return state.getFluidState().isIn(this.fluids);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_FLUIDS;
    }
}

