package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

/**
 * A dispenser behavior that spawns a projectile with velocity in front of the dispenser.
 */
public class ProjectileDispenserBehavior extends ItemDispenserBehavior {
	private final ProjectileItem projectile;
	private final ProjectileItem.Settings projectileSettings;

	public ProjectileDispenserBehavior(Item item) {
		if (item instanceof ProjectileItem projectileItem) {
			this.projectile = projectileItem;
			this.projectileSettings = projectileItem.getProjectileSettings();
		} else {
			throw new IllegalArgumentException(item + " not instance of " + ProjectileItem.class.getSimpleName());
		}
	}

	@Override
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		ServerWorld serverWorld = pointer.world();
		Direction direction = pointer.state().get(DispenserBlock.FACING);
		Position position = this.projectileSettings.positionFunction().getDispensePosition(pointer, direction);
		ProjectileEntity.spawnWithVelocity(
			this.projectile.createEntity(serverWorld, position, stack, direction),
			serverWorld,
			stack,
			(double)direction.getOffsetX(),
			(double)direction.getOffsetY(),
			(double)direction.getOffsetZ(),
			this.projectileSettings.power(),
			this.projectileSettings.uncertainty()
		);
		stack.decrement(1);
		return stack;
	}

	@Override
	protected void playSound(BlockPointer pointer) {
		pointer.world().syncWorldEvent(this.projectileSettings.overrideDispenseEvent().orElse(1002), pointer.pos(), 0);
	}
}
