package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class MushroomStewItem extends FoodItem {
	public MushroomStewItem(int i, Item.Settings settings) {
		super(i, 0.6F, false, settings);
	}

	@Override
	public ItemStack onItemFinishedUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		super.onItemFinishedUsing(itemStack, world, livingEntity);
		return new ItemStack(Items.field_8428);
	}
}
