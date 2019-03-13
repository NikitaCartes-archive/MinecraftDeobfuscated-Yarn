package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SplashPotionItem extends PotionItem {
	public SplashPotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		ItemStack itemStack2 = playerEntity.abilities.creativeMode ? itemStack.copy() : itemStack.split(1);
		world.method_8465(
			null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_14910, SoundCategory.field_15248, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			ThrownPotionEntity thrownPotionEntity = new ThrownPotionEntity(world, playerEntity);
			thrownPotionEntity.method_7494(itemStack2);
			thrownPotionEntity.method_19207(playerEntity, playerEntity.pitch, playerEntity.yaw, -20.0F, 0.5F, 1.0F);
			world.spawnEntity(thrownPotionEntity);
		}

		playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.field_5812, itemStack);
	}
}
