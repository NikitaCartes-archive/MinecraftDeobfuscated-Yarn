package net.minecraft.item;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CarrotOnAStickItem extends Item {
	public CarrotOnAStickItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (world.isClient) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (playerEntity.hasVehicle() && playerEntity.getRiddenEntity() instanceof PigEntity) {
				PigEntity pigEntity = (PigEntity)playerEntity.getRiddenEntity();
				if (itemStack.getDurability() - itemStack.getDamage() >= 7 && pigEntity.method_6577()) {
					itemStack.applyDamage(7, playerEntity);
					if (itemStack.isEmpty()) {
						ItemStack itemStack2 = new ItemStack(Items.field_8378);
						itemStack2.setTag(itemStack.getTag());
						return new TypedActionResult<>(ActionResult.field_5812, itemStack2);
					}

					return new TypedActionResult<>(ActionResult.field_5812, itemStack);
				}
			}

			playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		}
	}
}
