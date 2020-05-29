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
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> field_24931 = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::method_28843, FoliagePlacerType::method_28849);
    protected final int radius;
    protected final int randomRadius;
    protected final int offset;
    protected final int randomOffset;

    protected static <P extends FoliagePlacer> Products.P4<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer, Integer> method_28846(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(((MapCodec)Codec.INT.fieldOf("radius")).forGetter(foliagePlacer -> foliagePlacer.radius), ((MapCodec)Codec.INT.fieldOf("radius_random")).forGetter(foliagePlacer -> foliagePlacer.randomRadius), ((MapCodec)Codec.INT.fieldOf("offset")).forGetter(foliagePlacer -> foliagePlacer.offset), ((MapCodec)Codec.INT.fieldOf("offset_random")).forGetter(foliagePlacer -> foliagePlacer.randomOffset));
    }

    public FoliagePlacer(int radius, int randomRadius, int offset, int randomOffset) {
        this.radius = radius;
        this.randomRadius = randomRadius;
        this.offset = offset;
        this.randomOffset = randomOffset;
    }

    protected abstract FoliagePlacerType<?> method_28843();

    public void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, BlockBox blockBox) {
        this.generate(world, random, config, trunkHeight, treeNode, foliageHeight, radius, leaves, this.method_27386(random), blockBox);
    }

    /**
     * This is the main method used to generate foliage.
     */
    protected abstract void generate(ModifiableTestableWorld var1, Random var2, TreeFeatureConfig var3, int var4, TreeNode var5, int var6, int var7, Set<BlockPos> var8, int var9, BlockBox var10);

    public abstract int getHeight(Random var1, int var2, TreeFeatureConfig var3);

    public int getRadius(Random random, int baseHeight) {
        return this.radius + random.nextInt(this.randomRadius + 1);
    }

    private int method_27386(Random random) {
        return this.offset + random.nextInt(this.randomOffset + 1);
    }

    protected abstract boolean isInvalidForLeaves(Random var1, int var2, int var3, int var4, int var5, boolean var6);

    protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
        int n;
        int m;
        if (bl) {
            m = Math.min(Math.abs(i), Math.abs(i - 1));
            n = Math.min(Math.abs(k), Math.abs(k - 1));
        } else {
            m = Math.abs(i);
            n = Math.abs(k);
        }
        return this.isInvalidForLeaves(random, m, j, n, l, bl);
    }

    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, BlockPos blockPos, int baseHeight, Set<BlockPos> set, int i, boolean giantTrunk, BlockBox blockBox) {
        int j = giantTrunk ? 1 : 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = -baseHeight; k <= baseHeight + j; ++k) {
            for (int l = -baseHeight; l <= baseHeight + j; ++l) {
                if (this.method_27387(random, k, i, l, baseHeight, giantTrunk)) continue;
                mutable.set(blockPos, k, i, l);
                if (!TreeFeature.canReplace(world, mutable)) continue;
                world.setBlockState(mutable, config.leavesProvider.getBlockState(random, mutable), 19);
                blockBox.encompass(new BlockBox(mutable, mutable));
                set.add(mutable.toImmutable());
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

