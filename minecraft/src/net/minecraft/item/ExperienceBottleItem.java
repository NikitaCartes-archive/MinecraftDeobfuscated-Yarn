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
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (!user.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		world.playSound(
			null, user.x, user.y, user.z, SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			ThrownExperienceBottleEntity thrownExperienceBottleEntity = new ThrownExperienceBottleEntity(world, user);
			thrownExperienceBottleEntity.setItem(itemStack);
			thrownExperienceBottleEntity.setProperties(user, user.pitch, user.yaw, -20.0F, 0.7F, 1.0F);
			world.spawnEntity(thrownExperienceBottleEntity);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
