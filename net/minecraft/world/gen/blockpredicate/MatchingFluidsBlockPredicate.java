/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

class MatchingFluidsBlockPredicate
extends OffsetPredicate {
    private final List<Fluid> fluids;
    public static final Codec<MatchingFluidsBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> MatchingFluidsBlockPredicate.registerOffsetField(instance).and(((MapCodec)Registry.FLUID.method_39673().listOf().fieldOf("fluids")).forGetter(predicate -> predicate.fluids)).apply((Applicative<MatchingFluidsBlockPredicate, ?>)instance, MatchingFluidsBlockPredicate::new));

    public MatchingFluidsBlockPredicate(Vec3i vec3i, List<Fluid> fluids) {
        super(vec3i);
        this.fluids = fluids;
    }

    @Override
    protected boolean test(BlockState state) {
        return this.fluids.contains(state.getFluidState().getFluid());
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_FLUIDS;
    }
}

