package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class MushroomStewItem extends Item {
	public MushroomStewItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
		ItemStack itemStack = super.finishUsing(stack, world, entity);
		return entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode ? itemStack : new ItemStack(Items.BOWL);
	}
}
