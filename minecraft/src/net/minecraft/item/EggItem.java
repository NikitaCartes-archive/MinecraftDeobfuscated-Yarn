package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EggItem extends Item {
	public EggItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
		}

		world.playSound(
			null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15012, SoundCategory.field_15248, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			ThrownEggEntity thrownEggEntity = new ThrownEggEntity(world, playerEntity);
			thrownEggEntity.method_16940(itemStack);
			thrownEggEntity.calculateVelocity(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(thrownEggEntity);
		}

		playerEntity.incrementStat(Stats.field_15372.method_14956(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
