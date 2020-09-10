package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ThrowablePotionItem extends PotionItem {
	public ThrowablePotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (!world.isClient) {
			PotionEntity potionEntity = new PotionEntity(world, user);
			potionEntity.setItem(itemStack);
			potionEntity.setProperties(user, user.pitch, user.yaw, -20.0F, 0.5F, 1.0F);
			world.spawnEntity(potionEntity);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}
}
