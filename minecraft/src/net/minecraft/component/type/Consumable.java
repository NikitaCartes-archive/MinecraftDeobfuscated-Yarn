package net.minecraft.component.type;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Consumable {
	void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable);
}
