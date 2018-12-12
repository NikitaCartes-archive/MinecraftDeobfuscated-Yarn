package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ExperienceBottleItem extends Item {
	public ExperienceBottleItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlow(ItemStack itemStack) {
		return true;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
		}

		world.playSound(
			null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_14637, SoundCategory.field_15254, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			ThrownExperienceBottleEntity thrownExperienceBottleEntity = new ThrownExperienceBottleEntity(world, playerEntity);
			thrownExperienceBottleEntity.method_16940(itemStack);
			thrownExperienceBottleEntity.calculateVelocity(playerEntity, playerEntity.pitch, playerEntity.yaw, -20.0F, 0.7F, 1.0F);
			world.spawnEntity(thrownExperienceBottleEntity);
		}

		playerEntity.incrementStat(Stats.field_15372.method_14956(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
