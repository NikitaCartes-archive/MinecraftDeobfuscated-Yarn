/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public abstract class ProjectileDispenserBehavior
extends ItemDispenserBehavior {
    @Override
    public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
        World world = blockPointer.getWorld();
        Position position = DispenserBlock.getOutputLocation(blockPointer);
        Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
        Projectile projectile = this.createProjectile(world, position, itemStack);
        projectile.setVelocity(direction.getOffsetX(), (float)direction.getOffsetY() + 0.1f, direction.getOffsetZ(), this.getForce(), this.getVariation());
        world.spawnEntity((Entity)((Object)projectile));
        itemStack.subtractAmount(1);
        return itemStack;
    }

    @Override
    protected void playSound(BlockPointer blockPointer) {
        blockPointer.getWorld().playLevelEvent(1002, blockPointer.getBlockPos(), 0);
    }

    protected abstract Projectile createProjectile(World var1, Position var2, ItemStack var3);

    protected float getVariation() {
        return 6.0f;
    }

    protected float getForce() {
        return 1.1f;
    }
}

