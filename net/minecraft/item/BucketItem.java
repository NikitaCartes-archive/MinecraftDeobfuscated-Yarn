/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BucketItem
extends Item {
    private final Fluid fluid;

    public BucketItem(Fluid fluid, Item.Settings settings) {
        super(settings);
        this.fluid = fluid;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        HitResult hitResult = BucketItem.rayTrace(world, playerEntity, this.fluid == Fluids.EMPTY ? RayTraceContext.FluidHandling.SOURCE_ONLY : RayTraceContext.FluidHandling.NONE);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos2;
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
                return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
            }
            if (this.fluid == Fluids.EMPTY) {
                Fluid fluid;
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.getBlock() instanceof FluidDrainable && (fluid = ((FluidDrainable)((Object)blockState.getBlock())).tryDrainFluid(world, blockPos, blockState)) != Fluids.EMPTY) {
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    playerEntity.playSound(fluid.matches(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0f, 1.0f);
                    ItemStack itemStack2 = this.getFilledStack(itemStack, playerEntity, fluid.getBucketItem());
                    if (!world.isClient) {
                        Criterions.FILLED_BUCKET.handle((ServerPlayerEntity)playerEntity, new ItemStack(fluid.getBucketItem()));
                    }
                    return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack2);
                }
                return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
            }
            BlockState blockState = world.getBlockState(blockPos);
            BlockPos blockPos3 = blockPos2 = blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockHitResult.getBlockPos().offset(blockHitResult.getSide());
            if (this.placeFluid(playerEntity, world, blockPos2, blockHitResult)) {
                this.onEmptied(world, itemStack, blockPos2);
                if (playerEntity instanceof ServerPlayerEntity) {
                    Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
                }
                playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, this.getEmptiedStack(itemStack, playerEntity));
            }
            return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
    }

    protected ItemStack getEmptiedStack(ItemStack itemStack, PlayerEntity playerEntity) {
        if (!playerEntity.abilities.creativeMode) {
            return new ItemStack(Items.BUCKET);
        }
        return itemStack;
    }

    public void onEmptied(World world, ItemStack itemStack, BlockPos blockPos) {
    }

    private ItemStack getFilledStack(ItemStack itemStack, PlayerEntity playerEntity, Item item) {
        if (playerEntity.abilities.creativeMode) {
            return itemStack;
        }
        itemStack.decrement(1);
        if (itemStack.isEmpty()) {
            return new ItemStack(item);
        }
        if (!playerEntity.inventory.insertStack(new ItemStack(item))) {
            playerEntity.dropItem(new ItemStack(item), false);
        }
        return itemStack;
    }

    public boolean placeFluid(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        if (!(this.fluid instanceof BaseFluid)) {
            return false;
        }
        BlockState blockState = world.getBlockState(blockPos);
        Material material = blockState.getMaterial();
        boolean bl = !material.isSolid();
        boolean bl2 = material.isReplaceable();
        if (world.isAir(blockPos) || bl || bl2 || blockState.getBlock() instanceof FluidFillable && ((FluidFillable)((Object)blockState.getBlock())).canFillWithFluid(world, blockPos, blockState, this.fluid)) {
            if (world.dimension.doesWaterVaporize() && this.fluid.matches(FluidTags.WATER)) {
                int i = blockPos.getX();
                int j = blockPos.getY();
                int k = blockPos.getZ();
                world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
                for (int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
                }
            } else if (blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER) {
                if (((FluidFillable)((Object)blockState.getBlock())).tryFillWithFluid(world, blockPos, blockState, ((BaseFluid)this.fluid).getStill(false))) {
                    this.playEmptyingSound(playerEntity, world, blockPos);
                }
            } else {
                if (!world.isClient && (bl || bl2) && !material.isLiquid()) {
                    world.breakBlock(blockPos, true);
                }
                this.playEmptyingSound(playerEntity, world, blockPos);
                world.setBlockState(blockPos, this.fluid.getDefaultState().getBlockState(), 11);
            }
            return true;
        }
        if (blockHitResult == null) {
            return false;
        }
        return this.placeFluid(playerEntity, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
    }

    protected void playEmptyingSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
        SoundEvent soundEvent = this.fluid.matches(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        iWorld.playSound(playerEntity, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}

