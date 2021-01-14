/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class JungleFoliagePlacer
extends FoliagePlacer {
    public static final Codec<JungleFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> JungleFoliagePlacer.fillFoliagePlacerFields(instance).and(((MapCodec)Codec.intRange(0, 16).fieldOf("height")).forGetter(jungleFoliagePlacer -> jungleFoliagePlacer.height)).apply((Applicative<JungleFoliagePlacer, ?>)instance, JungleFoliagePlacer::new));
    protected final int height;

    public JungleFoliagePlacer(UniformIntDistribution radius, UniformIntDistribution offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.JUNGLE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, int offset, BlockBox box) {
        int i = treeNode.isGiantTrunk() ? foliageHeight : 1 + random.nextInt(2);
        for (int j = offset; j >= offset - i; --j) {
            int k = radius + treeNode.getFoliageRadius() + 1 - j;
            this.generateSquare(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk(), box);
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        if (dx + dz >= 7) {
            return true;
        }
        return dx * dx + dz * dz > radius * radius;
    }
}

