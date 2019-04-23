/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractPileFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class IcePileFeature
extends AbstractPileFeature {
    public IcePileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected BlockState getPileBlockState(IWorld iWorld) {
        return iWorld.getRandom().nextInt(7) == 0 ? Blocks.BLUE_ICE.getDefaultState() : Blocks.PACKED_ICE.getDefaultState();
    }
}

