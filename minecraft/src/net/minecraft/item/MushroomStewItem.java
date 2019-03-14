package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class MushroomStewItem extends Item {
	public MushroomStewItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack onItemFinishedUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		super.onItemFinishedUsing(itemStack, world, livingEntity);
		return new ItemStack(Items.field_8428);
	}
}
