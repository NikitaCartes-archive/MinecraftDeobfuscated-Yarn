package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class ArrowItem extends Item {
	public ArrowItem(Item.Settings settings) {
		super(settings);
	}

	public ProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
		ArrowEntity arrowEntity = new ArrowEntity(world, shooter);
		arrowEntity.initFromStack(stack);
		return arrowEntity;
	}
}
