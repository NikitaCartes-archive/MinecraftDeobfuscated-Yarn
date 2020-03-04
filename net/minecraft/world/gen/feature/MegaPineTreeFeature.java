/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.MegaTreeFeature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;

public class MegaPineTreeFeature
extends MegaTreeFeature<MegaTreeFeatureConfig> {
    public MegaPineTreeFeature(Function<Dynamic<?>, ? extends MegaTreeFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, MegaTreeFeatureConfig megaTreeFeatureConfig) {
        int i = this.getHeight(random, megaTreeFeatureConfig);
        if (!this.checkTreeFitsAndReplaceGround(modifiableTestableWorld, blockPos, i)) {
            return false;
        }
        this.makeTopLeaves(modifiableTestableWorld, random, blockPos.getX(), blockPos.getZ(), blockPos.getY() + i, 0, set2, blockBox, megaTreeFeatureConfig);
        this.generateTrunk(modifiableTestableWorld, random, blockPos, i, set, blockBox, megaTreeFeatureConfig);
        return true;
    }

    private void makeTopLeaves(ModifiableTestableWorld world, Random random, int x, int z, int height, int radius, Set<BlockPos> leaves, BlockBox box, MegaTreeFeatureConfig config) {
        int i = random.nextInt(5) + config.crownHeight;
        int j = 0;
        for (int k = height - i; k <= height; ++k) {
            int l = height - k;
            int m = radius + MathHelper.floor((float)l / (float)i * 3.5f);
            this.makeSquaredLeafLayer(world, random, new BlockPos(x, k, z), m + (l > 0 && m == j && (k & 1) == 0 ? 1 : 0), leaves, box, config);
            j = m;
        }
    }
}

