package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public abstract class ProjectileDispenserBehavior extends ItemDispenserBehavior {
	@Override
	public ItemStack dispenseSilently(BlockPointer blockPointer, ItemStack itemStack) {
		World world = blockPointer.getWorld();
		Position position = DispenserBlock.getOutputLocation(blockPointer);
		Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
		Projectile projectile = this.createProjectile(world, position, itemStack);
		projectile.setVelocity(
			(double)direction.getOffsetX(), (double)((float)direction.getOffsetY() + 0.1F), (double)direction.getOffsetZ(), this.getForce(), this.getVariation()
		);
		world.spawnEntity((Entity)projectile);
		itemStack.decrement(1);
		return itemStack;
	}

	@Override
	protected void playSound(BlockPointer blockPointer) {
		blockPointer.getWorld().playLevelEvent(1002, blockPointer.getBlockPos(), 0);
	}

	protected abstract Projectile createProjectile(World world, Position position, ItemStack itemStack);

	protected float getVariation() {
		return 6.0F;
	}

	protected float getForce() {
		return 1.1F;
	}
}
