package net.minecraft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class class_4537 extends PotionItem {
	public class_4537(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!world.isClient) {
			ThrownPotionEntity thrownPotionEntity = new ThrownPotionEntity(world, playerEntity);
			thrownPotionEntity.setItemStack(itemStack);
			thrownPotionEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, -20.0F, 0.5F, 1.0F);
			world.spawnEntity(thrownPotionEntity);
		}

		playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!playerEntity.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
