/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.OakTreeFeature;

public class JungleTreeFeature
extends OakTreeFeature {
    public JungleTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, BlockState blockState, BlockState blockState2, boolean bl2) {
        super(function, bl, i, blockState, blockState2, bl2);
    }

    @Override
    protected int getTreeHeight(Random random) {
        return this.height + random.nextInt(7);
    }
}

