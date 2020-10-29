/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> TYPE_CODEC = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::getType, FoliagePlacerType::getCodec);
    protected final UniformIntDistribution radius;
    protected final UniformIntDistribution offset;

    protected static <P extends FoliagePlacer> Products.P2<RecordCodecBuilder.Mu<P>, UniformIntDistribution, UniformIntDistribution> fillFoliagePlacerFields(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(((MapCodec)UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("radius")).forGetter(foliagePlacer -> foliagePlacer.radius), ((MapCodec)UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("offset")).forGetter(foliagePlacer -> foliagePlacer.offset));
    }

    public FoliagePlacer(UniformIntDistribution radius, UniformIntDistribution offset) {
        this.radius = radius;
        this.offset = offset;
    }

    protected abstract FoliagePlacerType<?> getType();

    public void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, BlockBox box) {
        this.generate(world, random, config, trunkHeight, treeNode, foliageHeight, radius, leaves, this.getRandomOffset(random), box);
    }

    /**
     * This is the main method used to generate foliage.
     */
    protected abstract void generate(ModifiableTestableWorld var1, Random var2, TreeFeatureConfig var3, int var4, TreeNode var5, int var6, int var7, Set<BlockPos> var8, int var9, BlockBox var10);

    public abstract int getRandomHeight(Random var1, int var2, TreeFeatureConfig var3);

    public int getRandomRadius(Random random, int baseHeight) {
        return this.radius.getValue(random);
    }

    private int getRandomOffset(Random random) {
        return this.offset.getValue(random);
    }

    /**
     * Used to exclude certain positions such as corners when creating a square of leaves.
     */
    protected abstract boolean isInvalidForLeaves(Random var1, int var2, int var3, int var4, int var5, boolean var6);

    /**
     * Normalizes x and z coords before checking if they are invalid.
     */
    protected boolean isPositionInvalid(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        int j;
        int i;
        if (giantTrunk) {
            i = Math.min(Math.abs(dx), Math.abs(dx - 1));
            j = Math.min(Math.abs(dz), Math.abs(dz - 1));
        } else {
            i = Math.abs(dx);
            j = Math.abs(dz);
        }
        return this.isInvalidForLeaves(random, i, y, j, radius, giantTrunk);
    }

    /**
     * Generates a square of leaves with the given radius. Sub-classes can use the method {@code isInvalidForLeaves} to exclude certain positions, such as corners.
     */
    protected void generateSquare(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, BlockPos pos, int radius, Set<BlockPos> leaves, int y, boolean giantTrunk, BlockBox box) {
        int i = giantTrunk ? 1 : 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = -radius; j <= radius + i; ++j) {
            for (int k = -radius; k <= radius + i; ++k) {
                if (this.isPositionInvalid(random, j, y, k, radius, giantTrunk)) continue;
                mutable.set(pos, j, y, k);
                if (!TreeFeature.canReplace(world, mutable)) continue;
                world.setBlockState(mutable, config.leavesProvider.getBlockState(random, mutable), 19);
                box.encompass(new BlockBox(mutable, mutable));
                leaves.add(mutable.toImmutable());
            }
        }
    }

    public static final class TreeNode {
        private final BlockPos center;
        private final int foliageRadius;
        private final boolean giantTrunk;

        public TreeNode(BlockPos center, int foliageRadius, boolean giantTrunk) {
            this.center = center;
            this.foliageRadius = foliageRadius;
            this.giantTrunk = giantTrunk;
        }

        public BlockPos getCenter() {
            return this.center;
        }

        public int getFoliageRadius() {
            return this.foliageRadius;
        }

        public boolean isGiantTrunk() {
            return this.giantTrunk;
        }
    }
}

