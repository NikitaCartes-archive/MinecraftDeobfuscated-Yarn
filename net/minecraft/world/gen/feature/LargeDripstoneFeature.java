/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.LargeDripstoneFeatureConfig;
import net.minecraft.world.gen.feature.util.CaveSurface;
import net.minecraft.world.gen.feature.util.DripstoneHelper;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.jetbrains.annotations.Nullable;

public class LargeDripstoneFeature
extends Feature<LargeDripstoneFeatureConfig> {
    public LargeDripstoneFeature(Codec<LargeDripstoneFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<LargeDripstoneFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        LargeDripstoneFeatureConfig largeDripstoneFeatureConfig = context.getConfig();
        Random random = context.getRandom();
        if (!DripstoneHelper.canGenerate(structureWorldAccess, blockPos)) {
            return false;
        }
        Optional<CaveSurface> optional = CaveSurface.create(structureWorldAccess, blockPos, largeDripstoneFeatureConfig.floorToCeilingSearchRange, DripstoneHelper::canGenerate, DripstoneHelper::canReplaceOrLava);
        if (!optional.isPresent() || !(optional.get() instanceof CaveSurface.Bounded)) {
            return false;
        }
        CaveSurface.Bounded bounded = (CaveSurface.Bounded)optional.get();
        if (bounded.getHeight() < 4) {
            return false;
        }
        int i = (int)((float)bounded.getHeight() * largeDripstoneFeatureConfig.maxColumnRadiusToCaveHeightRatio);
        int j = MathHelper.clamp(i, largeDripstoneFeatureConfig.columnRadius.getMin(), largeDripstoneFeatureConfig.columnRadius.getMax());
        int k = MathHelper.nextBetween(random, largeDripstoneFeatureConfig.columnRadius.getMin(), j);
        DripstoneGenerator dripstoneGenerator = LargeDripstoneFeature.createGenerator(blockPos.withY(bounded.getCeiling() - 1), false, random, k, largeDripstoneFeatureConfig.stalactiteBluntness, largeDripstoneFeatureConfig.heightScale);
        DripstoneGenerator dripstoneGenerator2 = LargeDripstoneFeature.createGenerator(blockPos.withY(bounded.getFloor() + 1), true, random, k, largeDripstoneFeatureConfig.stalagmiteBluntness, largeDripstoneFeatureConfig.heightScale);
        WindModifier windModifier = dripstoneGenerator.generateWind(largeDripstoneFeatureConfig) && dripstoneGenerator2.generateWind(largeDripstoneFeatureConfig) ? new WindModifier(blockPos.getY(), random, largeDripstoneFeatureConfig.windSpeed) : WindModifier.create();
        boolean bl = dripstoneGenerator.canGenerate(structureWorldAccess, windModifier);
        boolean bl2 = dripstoneGenerator2.canGenerate(structureWorldAccess, windModifier);
        if (bl) {
            dripstoneGenerator.generate(structureWorldAccess, random, windModifier);
        }
        if (bl2) {
            dripstoneGenerator2.generate(structureWorldAccess, random, windModifier);
        }
        return true;
    }

    private static DripstoneGenerator createGenerator(BlockPos pos, boolean isStalagmite, Random random, int scale, FloatProvider bluntness, FloatProvider heightScale) {
        return new DripstoneGenerator(pos, isStalagmite, scale, bluntness.get(random), heightScale.get(random));
    }

    private void method_35360(StructureWorldAccess structureWorldAccess, BlockPos blockPos, CaveSurface.Bounded bounded, WindModifier windModifier) {
        structureWorldAccess.setBlockState(windModifier.modify(blockPos.withY(bounded.getCeiling() - 1)), Blocks.DIAMOND_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
        structureWorldAccess.setBlockState(windModifier.modify(blockPos.withY(bounded.getFloor() + 1)), Blocks.GOLD_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
        BlockPos.Mutable mutable = blockPos.withY(bounded.getFloor() + 2).mutableCopy();
        while (mutable.getY() < bounded.getCeiling() - 1) {
            BlockPos blockPos2 = windModifier.modify(mutable);
            if (DripstoneHelper.canGenerate(structureWorldAccess, blockPos2) || structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.DRIPSTONE_BLOCK)) {
                structureWorldAccess.setBlockState(blockPos2, Blocks.CREEPER_HEAD.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
            mutable.move(Direction.UP);
        }
    }

    static final class WindModifier {
        private final int y;
        @Nullable
        private final Vec3d wind;

        private WindModifier(int y, Random random, FloatProvider wind) {
            this.y = y;
            float f = wind.get(random);
            float g = MathHelper.nextBetween(random, 0.0f, (float)Math.PI);
            this.wind = new Vec3d(MathHelper.cos(g) * f, 0.0, MathHelper.sin(g) * f);
        }

        private WindModifier() {
            this.y = 0;
            this.wind = null;
        }

        private static WindModifier create() {
            return new WindModifier();
        }

        private BlockPos modify(BlockPos pos) {
            if (this.wind == null) {
                return pos;
            }
            int i = this.y - pos.getY();
            Vec3d vec3d = this.wind.multiply(i);
            return pos.add(vec3d.x, 0.0, vec3d.z);
        }
    }

    static final class DripstoneGenerator {
        private BlockPos pos;
        private final boolean isStalagmite;
        private int scale;
        private final double bluntness;
        private final double heightScale;

        private DripstoneGenerator(BlockPos pos, boolean isStalagmite, int scale, double bluntness, double heightScale) {
            this.pos = pos;
            this.isStalagmite = isStalagmite;
            this.scale = scale;
            this.bluntness = bluntness;
            this.heightScale = heightScale;
        }

        private int getBaseScale() {
            return this.scale(0.0f);
        }

        private int method_35361() {
            if (this.isStalagmite) {
                return this.pos.getY();
            }
            return this.pos.getY() - this.getBaseScale();
        }

        private int method_35362() {
            if (!this.isStalagmite) {
                return this.pos.getY();
            }
            return this.pos.getY() + this.getBaseScale();
        }

        private boolean canGenerate(StructureWorldAccess world, WindModifier wind) {
            while (this.scale > 1) {
                BlockPos.Mutable mutable = this.pos.mutableCopy();
                int i = Math.min(10, this.getBaseScale());
                for (int j = 0; j < i; ++j) {
                    if (world.getBlockState(mutable).isOf(Blocks.LAVA)) {
                        return false;
                    }
                    if (DripstoneHelper.canGenerateBase(world, wind.modify(mutable), this.scale)) {
                        this.pos = mutable;
                        return true;
                    }
                    mutable.move(this.isStalagmite ? Direction.DOWN : Direction.UP);
                }
                this.scale /= 2;
            }
            return false;
        }

        private int scale(float height) {
            return (int)DripstoneHelper.scaleHeightFromRadius(height, this.scale, this.heightScale, this.bluntness);
        }

        private void generate(StructureWorldAccess world, Random random, WindModifier wind) {
            for (int i = -this.scale; i <= this.scale; ++i) {
                block1: for (int j = -this.scale; j <= this.scale; ++j) {
                    int k;
                    float f = MathHelper.sqrt(i * i + j * j);
                    if (f > (float)this.scale || (k = this.scale(f)) <= 0) continue;
                    if ((double)random.nextFloat() < 0.2) {
                        k = (int)((float)k * MathHelper.nextBetween(random, 0.8f, 1.0f));
                    }
                    BlockPos.Mutable mutable = this.pos.add(i, 0, j).mutableCopy();
                    boolean bl = false;
                    for (int l = 0; l < k; ++l) {
                        BlockPos blockPos = wind.modify(mutable);
                        if (DripstoneHelper.canGenerateOrLava(world, blockPos)) {
                            bl = true;
                            Block block = Blocks.DRIPSTONE_BLOCK;
                            world.setBlockState(blockPos, block.getDefaultState(), Block.NOTIFY_LISTENERS);
                        } else if (bl && world.getBlockState(blockPos).isIn(BlockTags.BASE_STONE_OVERWORLD)) continue block1;
                        mutable.move(this.isStalagmite ? Direction.UP : Direction.DOWN);
                    }
                }
            }
        }

        private boolean generateWind(LargeDripstoneFeatureConfig config) {
            return this.scale >= config.minRadiusForWind && this.bluntness >= (double)config.minBluntnessForWind;
        }
    }
}

