/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldEvents;

public class BoatDispenserBehavior
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior itemDispenser = new ItemDispenserBehavior();
    private final BoatEntity.Type boatType;

    public BoatDispenserBehavior(BoatEntity.Type type) {
        this.boatType = type;
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        double g;
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        ServerWorld world = pointer.getWorld();
        double d = pointer.getX() + (double)((float)direction.getOffsetX() * 1.125f);
        double e = pointer.getY() + (double)((float)direction.getOffsetY() * 1.125f);
        double f = pointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125f);
        BlockPos blockPos = pointer.getBlockPos().offset(direction);
        if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            g = 1.0;
        } else if (world.getBlockState(blockPos).isAir() && world.getFluidState(blockPos.down()).isIn(FluidTags.WATER)) {
            g = 0.0;
        } else {
            return this.itemDispenser.dispense(pointer, stack);
        }
        BoatEntity boatEntity = new BoatEntity(world, d, e + g, f);
        boatEntity.setBoatType(this.boatType);
        boatEntity.setYaw(direction.asRotation());
        world.spawnEntity(boatEntity);
        stack.decrement(1);
        return stack;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.getBlockPos(), 0);
    }
}

