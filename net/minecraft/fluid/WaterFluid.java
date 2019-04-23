/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.fluid;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class WaterFluid
extends BaseFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_WATER;
    }

    @Override
    public Fluid getStill() {
        return Fluids.WATER;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public Item getBucketItem() {
        return Items.WATER_BUCKET;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
        if (!fluidState.isStill() && !fluidState.get(FALLING).booleanValue()) {
            if (random.nextInt(64) == 0) {
                world.playSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
            }
        } else if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.UNDERWATER, (float)blockPos.getX() + random.nextFloat(), (float)blockPos.getY() + random.nextFloat(), (float)blockPos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    protected boolean isInfinite() {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? iWorld.getBlockEntity(blockPos) : null;
        Block.dropStacks(blockState, iWorld.getWorld(), blockPos, blockEntity);
    }

    @Override
    public int method_15733(ViewableWorld viewableWorld) {
        return 4;
    }

    @Override
    public BlockState toBlockState(FluidState fluidState) {
        return (BlockState)Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, WaterFluid.method_15741(fluidState));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }

    @Override
    public int getLevelDecreasePerBlock(ViewableWorld viewableWorld) {
        return 1;
    }

    @Override
    public int getTickRate(ViewableWorld viewableWorld) {
        return 5;
    }

    @Override
    public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.matches(FluidTags.WATER);
    }

    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }

    public static class Flowing
    extends WaterFluid {
        @Override
        protected void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still
    extends WaterFluid {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}

