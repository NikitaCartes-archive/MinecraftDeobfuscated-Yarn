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

public class PumpkinPileFeature
extends AbstractPileFeature {
    public PumpkinPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected BlockState getPileBlockState(IWorld iWorld) {
        if (iWorld.getRandom().nextFloat() < 0.95f) {
            return Blocks.PUMPKIN.getDefaultState();
        }
        return Blocks.JACK_O_LANTERN.getDefaultState();
    }
}

