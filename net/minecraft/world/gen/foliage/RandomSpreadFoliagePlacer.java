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

public class RandomSpreadFoliagePlacer
extends FoliagePlacer {
    public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> RandomSpreadFoliagePlacer.fillFoliagePlacerFields(instance).and(instance.group(((MapCodec)UniformIntDistribution.createValidatedCodec(1, 256, 256).fieldOf("foliage_height")).forGetter(randomSpreadFoliagePlacer -> randomSpreadFoliagePlacer.foliageHeight), ((MapCodec)Codec.intRange(0, 256).fieldOf("leaf_placement_attempts")).forGetter(randomSpreadFoliagePlacer -> randomSpreadFoliagePlacer.leafPlacementAttempts))).apply((Applicative<RandomSpreadFoliagePlacer, ?>)instance, RandomSpreadFoliagePlacer::new));
    private final UniformIntDistribution foliageHeight;
    private final int leafPlacementAttempts;

    public RandomSpreadFoliagePlacer(UniformIntDistribution radius, UniformIntDistribution offset, UniformIntDistribution foliageHeight, int leafPlacementAttempts) {
        super(radius, offset);
        this.foliageHeight = foliageHeight;
        this.leafPlacementAttempts = leafPlacementAttempts;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, int offset, BlockBox box) {
        BlockPos blockPos = treeNode.getCenter();
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        for (int i = 0; i < this.leafPlacementAttempts; ++i) {
            mutable.set(blockPos, random.nextInt(radius) - random.nextInt(radius), random.nextInt(foliageHeight) - random.nextInt(foliageHeight), random.nextInt(radius) - random.nextInt(radius));
            this.placeFoliageBlock(world, random, config, leaves, box, mutable);
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.foliageHeight.getValue(random);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return false;
    }
}

