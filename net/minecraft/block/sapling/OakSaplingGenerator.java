/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class OakSaplingGenerator
extends SaplingGenerator {
    @Override
    protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
        if (random.nextInt(10) == 0) {
            return bees ? ConfiguredFeatures.FANCY_OAK_BEES_005 : ConfiguredFeatures.FANCY_OAK;
        }
        return bees ? ConfiguredFeatures.OAK_BEES_005 : ConfiguredFeatures.OAK;
    }
}

