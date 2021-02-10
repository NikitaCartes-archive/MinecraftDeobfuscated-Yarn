/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class HugeMushroomFeature
extends Feature<HugeMushroomFeatureConfig> {
    public HugeMushroomFeature(Codec<HugeMushroomFeatureConfig> codec) {
        super(codec);
    }

    protected void generateStem(WorldAccess world, Random random, BlockPos pos, HugeMushroomFeatureConfig config, int height, BlockPos.Mutable mutable) {
        for (int i = 0; i < height; ++i) {
            mutable.set(pos).move(Direction.UP, i);
            if (world.getBlockState(mutable).isOpaqueFullCube(world, mutable)) continue;
            this.setBlockState(world, mutable, config.stemProvider.getBlockState(random, pos));
        }
    }

    protected int getHeight(Random random) {
        int i = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        return i;
    }

    protected boolean canGenerate(WorldAccess world, BlockPos pos, int height, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config) {
        int i = pos.getY();
        if (i < world.getBottomY() + 1 || i + height + 1 >= world.getTopY()) {
            return false;
        }
        BlockState blockState = world.getBlockState(pos.down());
        if (!HugeMushroomFeature.isSoil(blockState) && !blockState.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return false;
        }
        for (int j = 0; j <= height; ++j) {
            int k = this.getCapSize(-1, -1, config.foliageRadius, j);
            for (int l = -k; l <= k; ++l) {
                for (int m = -k; m <= k; ++m) {
                    BlockState blockState2 = world.getBlockState(mutable.set(pos, l, j, m));
                    if (blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean generate(FeatureContext<HugeMushroomFeatureConfig> context) {
        BlockPos.Mutable mutable;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getPos();
        Random random = context.getRandom();
        HugeMushroomFeatureConfig hugeMushroomFeatureConfig = context.getConfig();
        int i = this.getHeight(random);
        if (!this.canGenerate(structureWorldAccess, blockPos, i, mutable = new BlockPos.Mutable(), hugeMushroomFeatureConfig)) {
            return false;
        }
        this.generateCap(structureWorldAccess, random, blockPos, i, mutable, hugeMushroomFeatureConfig);
        this.generateStem(structureWorldAccess, random, blockPos, hugeMushroomFeatureConfig, i, mutable);
        return true;
    }

    protected abstract int getCapSize(int var1, int var2, int var3, int var4);

    protected abstract void generateCap(WorldAccess var1, Random var2, BlockPos var3, int var4, BlockPos.Mutable var5, HugeMushroomFeatureConfig var6);
}

