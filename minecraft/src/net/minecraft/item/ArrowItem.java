package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ArrowItem extends Item implements ProjectileItem {
	public ArrowItem(Item.Settings settings) {
		super(settings);
	}

	public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
		return new ArrowEntity(world, shooter, stack.copyWithCount(1), shotFrom);
	}

	@Override
	public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
		ArrowEntity arrowEntity = new ArrowEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1), null);
		arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
		return arrowEntity;
	}
}
