/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> TYPE_CODEC = Registries.FOLIAGE_PLACER_TYPE.getCodec().dispatch(FoliagePlacer::getType, FoliagePlacerType::getCodec);
    protected final IntProvider radius;
    protected final IntProvider offset;

    protected static <P extends FoliagePlacer> Products.P2<RecordCodecBuilder.Mu<P>, IntProvider, IntProvider> fillFoliagePlacerFields(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(((MapCodec)IntProvider.createValidatingCodec(0, 16).fieldOf("radius")).forGetter(placer -> placer.radius), ((MapCodec)IntProvider.createValidatingCodec(0, 16).fieldOf("offset")).forGetter(placer -> placer.offset));
    }

    public FoliagePlacer(IntProvider radius, IntProvider offset) {
        this.radius = radius;
        this.offset = offset;
    }

    protected abstract FoliagePlacerType<?> getType();

    public void generate(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius) {
        this.generate(world, placer, random, config, trunkHeight, treeNode, foliageHeight, radius, this.getRandomOffset(random));
    }

    /**
     * This is the main method used to generate foliage.
     */
    protected abstract void generate(TestableWorld var1, BlockPlacer var2, Random var3, TreeFeatureConfig var4, int var5, TreeNode var6, int var7, int var8, int var9);

    public abstract int getRandomHeight(Random var1, int var2, TreeFeatureConfig var3);

    public int getRandomRadius(Random random, int baseHeight) {
        return this.radius.get(random);
    }

    private int getRandomOffset(Random random) {
        return this.offset.get(random);
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
    protected void generateSquare(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config, BlockPos centerPos, int radius, int y, boolean giantTrunk) {
        int i = giantTrunk ? 1 : 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = -radius; j <= radius + i; ++j) {
            for (int k = -radius; k <= radius + i; ++k) {
                if (this.isPositionInvalid(random, j, y, k, radius, giantTrunk)) continue;
                mutable.set(centerPos, j, y, k);
                FoliagePlacer.placeFoliageBlock(world, placer, random, config, mutable);
            }
        }
    }

    protected final void generateSquareWithHangingLeaves(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config, BlockPos centerPos, int radius, int y, boolean giantTrunk, float hangingLeavesChance, float hangingLeavesExtensionChance) {
        this.generateSquare(world, placer, random, config, centerPos, radius, y, giantTrunk);
        int i = giantTrunk ? 1 : 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            Direction direction2 = direction.rotateYClockwise();
            int j = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? radius + i : radius;
            mutable.set(centerPos, 0, y - 1, 0).move(direction2, j).move(direction, -radius);
            for (int k = -radius; k < radius + i; ++k) {
                boolean bl = placer.hasPlacedBlock(mutable.move(Direction.UP));
                mutable.move(Direction.DOWN);
                if (bl && !(random.nextFloat() > hangingLeavesChance) && FoliagePlacer.placeFoliageBlock(world, placer, random, config, mutable) && !(random.nextFloat() > hangingLeavesExtensionChance)) {
                    FoliagePlacer.placeFoliageBlock(world, placer, random, config, mutable.move(Direction.DOWN));
                    mutable.move(Direction.UP);
                }
                mutable.move(direction);
            }
        }
    }

    protected static boolean placeFoliageBlock(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config, BlockPos pos) {
        if (!TreeFeature.canReplace(world, pos)) {
            return false;
        }
        BlockState blockState = config.foliageProvider.get(random, pos);
        if (blockState.contains(Properties.WATERLOGGED)) {
            blockState = (BlockState)blockState.with(Properties.WATERLOGGED, world.testFluidState(pos, fluidState -> fluidState.isEqualAndStill(Fluids.WATER)));
        }
        placer.placeBlock(pos, blockState);
        return true;
    }

    public static interface BlockPlacer {
        public void placeBlock(BlockPos var1, BlockState var2);

        public boolean hasPlacedBlock(BlockPos var1);
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

