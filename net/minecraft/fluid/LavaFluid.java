/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.fluid;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
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

public abstract class LavaFluid
extends BaseFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_LAVA;
    }

    @Override
    public Fluid getStill() {
        return Fluids.LAVA;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public Item getBucketItem() {
        return Items.LAVA_BUCKET;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
        BlockPos blockPos2 = blockPos.up();
        if (world.getBlockState(blockPos2).isAir() && !world.getBlockState(blockPos2).isFullOpaque(world, blockPos2)) {
            if (random.nextInt(100) == 0) {
                double d = (float)blockPos.getX() + random.nextFloat();
                double e = blockPos.getY() + 1;
                double f = (float)blockPos.getZ() + random.nextFloat();
                world.addParticle(ParticleTypes.LAVA, d, e, f, 0.0, 0.0, 0.0);
                world.playSound(d, e, f, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
            if (random.nextInt(200) == 0) {
                world.playSound(blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public void onRandomTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
        if (!world.getGameRules().getBoolean("doFireTick")) {
            return;
        }
        int i = random.nextInt(3);
        if (i > 0) {
            BlockPos blockPos2 = blockPos;
            for (int j = 0; j < i; ++j) {
                if (!world.isHeightValidAndBlockLoaded(blockPos2 = blockPos2.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1))) {
                    return;
                }
                BlockState blockState = world.getBlockState(blockPos2);
                if (blockState.isAir()) {
                    if (!this.method_15819(world, blockPos2)) continue;
                    world.setBlockState(blockPos2, Blocks.FIRE.getDefaultState());
                    return;
                }
                if (!blockState.getMaterial().blocksMovement()) continue;
                return;
            }
        } else {
            for (int k = 0; k < 3; ++k) {
                BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!world.isHeightValidAndBlockLoaded(blockPos3)) {
                    return;
                }
                if (!world.isAir(blockPos3.up()) || !this.method_15817(world, blockPos3)) continue;
                world.setBlockState(blockPos3.up(), Blocks.FIRE.getDefaultState());
            }
        }
    }

    private boolean method_15819(ViewableWorld viewableWorld, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!this.method_15817(viewableWorld, blockPos.offset(direction))) continue;
            return true;
        }
        return false;
    }

    private boolean method_15817(ViewableWorld viewableWorld, BlockPos blockPos) {
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && !viewableWorld.isBlockLoaded(blockPos)) {
            return false;
        }
        return viewableWorld.getBlockState(blockPos).getMaterial().isBurnable();
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_LAVA;
    }

    @Override
    protected void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
        this.method_15818(iWorld, blockPos);
    }

    @Override
    public int method_15733(ViewableWorld viewableWorld) {
        return viewableWorld.getDimension().doesWaterVaporize() ? 4 : 2;
    }

    @Override
    public BlockState toBlockState(FluidState fluidState) {
        return (BlockState)Blocks.LAVA.getDefaultState().with(FluidBlock.LEVEL, LavaFluid.method_15741(fluidState));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
    }

    @Override
    public int getLevelDecreasePerBlock(ViewableWorld viewableWorld) {
        return viewableWorld.getDimension().doesWaterVaporize() ? 1 : 2;
    }

    @Override
    public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return fluidState.getHeight(blockView, blockPos) >= 0.44444445f && fluid.matches(FluidTags.WATER);
    }

    @Override
    public int getTickRate(ViewableWorld viewableWorld) {
        return viewableWorld.getDimension().isNether() ? 10 : 30;
    }

    @Override
    public int getNextTickDelay(World world, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
        int i = this.getTickRate(world);
        if (!(fluidState.isEmpty() || fluidState2.isEmpty() || fluidState.get(FALLING).booleanValue() || fluidState2.get(FALLING).booleanValue() || !(fluidState2.getHeight(world, blockPos) > fluidState.getHeight(world, blockPos)) || world.getRandom().nextInt(4) == 0)) {
            i *= 4;
        }
        return i;
    }

    private void method_15818(IWorld iWorld, BlockPos blockPos) {
        iWorld.playLevelEvent(1501, blockPos, 0);
    }

    @Override
    protected boolean isInfinite() {
        return false;
    }

    @Override
    protected void flow(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidState2 = iWorld.getFluidState(blockPos);
            if (this.matches(FluidTags.LAVA) && fluidState2.matches(FluidTags.WATER)) {
                if (blockState.getBlock() instanceof FluidBlock) {
                    iWorld.setBlockState(blockPos, Blocks.STONE.getDefaultState(), 3);
                }
                this.method_15818(iWorld, blockPos);
                return;
            }
        }
        super.flow(iWorld, blockPos, blockState, direction, fluidState);
    }

    @Override
    protected boolean hasRandomTicks() {
        return true;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }

    public static class Flowing
    extends LavaFluid {
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
    extends LavaFluid {
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

