package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.world.World;

public class SpectralArrowItem extends ArrowItem {
	public SpectralArrowItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ProjectileEntity createArrow(World world, ItemStack itemStack, LivingEntity livingEntity) {
		return new SpectralArrowEntity(world, livingEntity);
	}
}
