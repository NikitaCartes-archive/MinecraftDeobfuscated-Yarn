package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

/**
 * A dispenser behavior that spawns a projectile with velocity in front of the dispenser.
 */
public abstract class ProjectileDispenserBehavior extends ItemDispenserBehavior {
	@Override
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		World world = pointer.getWorld();
		Position position = DispenserBlock.getOutputLocation(pointer);
		Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
		ProjectileEntity projectileEntity = this.createProjectile(world, position, stack);
		projectileEntity.setVelocity(
			(double)direction.getOffsetX(), (double)((float)direction.getOffsetY() + 0.1F), (double)direction.getOffsetZ(), this.getForce(), this.getVariation()
		);
		world.spawnEntity(projectileEntity);
		stack.decrement(1);
		return stack;
	}

	@Override
	protected void playSound(BlockPointer pointer) {
		pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_LAUNCHES_PROJECTILE, pointer.getPos(), 0);
	}

	/**
	 * Creates the entity that will be spawned in front of the dispenser.
	 * 
	 * @return the created projectile
	 * 
	 * @param world the world the projectile will spawn in
	 * @param stack the stack that the dispenser will consume
	 * @param position the output location of the dispenser
	 */
	protected abstract ProjectileEntity createProjectile(World world, Position position, ItemStack stack);

	/**
	 * {@return the variation of a projectile's velocity when spawned}
	 */
	protected float getVariation() {
		return 6.0F;
	}

	/**
	 * {@return the force of a projectile's velocity when spawned}
	 */
	protected float getForce() {
		return 1.1F;
	}
}
