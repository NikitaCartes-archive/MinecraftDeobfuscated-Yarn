/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BoatDispenserBehavior
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior itemDispenser = new ItemDispenserBehavior();
    private final BoatEntity.Type boatType;

    public BoatDispenserBehavior(BoatEntity.Type type) {
        this.boatType = type;
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer blockPointer, ItemStack itemStack) {
        double g;
        Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
        World world = blockPointer.getWorld();
        double d = blockPointer.getX() + (double)((float)direction.getOffsetX() * 1.125f);
        double e = blockPointer.getY() + (double)((float)direction.getOffsetY() * 1.125f);
        double f = blockPointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125f);
        BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
        if (world.getFluidState(blockPos).matches(FluidTags.WATER)) {
            g = 1.0;
        } else if (world.getBlockState(blockPos).isAir() && world.getFluidState(blockPos.down()).matches(FluidTags.WATER)) {
            g = 0.0;
        } else {
            return this.itemDispenser.dispense(blockPointer, itemStack);
        }
        BoatEntity boatEntity = new BoatEntity(world, d, e + g, f);
        boatEntity.setBoatType(this.boatType);
        boatEntity.yaw = direction.asRotation();
        world.spawnEntity(boatEntity);
        itemStack.decrement(1);
        return itemStack;
    }

    @Override
    protected void playSound(BlockPointer blockPointer) {
        blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
    }
}

