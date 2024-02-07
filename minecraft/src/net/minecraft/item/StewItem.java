package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class StewItem extends Item {
	public StewItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		if (user instanceof PlayerEntity playerEntity && playerEntity.isInCreativeMode()) {
			return itemStack;
		}

		return new ItemStack(Items.BOWL);
	}
}
