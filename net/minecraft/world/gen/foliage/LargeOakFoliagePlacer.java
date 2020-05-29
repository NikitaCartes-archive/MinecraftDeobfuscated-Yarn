/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class LargeOakFoliagePlacer
extends BlobFoliagePlacer {
    public static final Codec<LargeOakFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> LargeOakFoliagePlacer.method_28838(instance).apply(instance, LargeOakFoliagePlacer::new));

    public LargeOakFoliagePlacer(int i, int j, int k, int l, int m) {
        super(i, j, k, l, m, FoliagePlacerType.FANCY_FOLIAGE_PLACER);
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, int i, BlockBox blockBox) {
        for (int j = i; j >= i - foliageHeight; --j) {
            int k = radius + (j == i || j == i - foliageHeight ? 0 : 1);
            this.generate(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk(), blockBox);
        }
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        return MathHelper.square((float)baseHeight + 0.5f) + MathHelper.square((float)dy + 0.5f) > (float)(dz * dz);
    }
}

