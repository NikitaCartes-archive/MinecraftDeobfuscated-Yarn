package net.minecraft.item;

import net.minecraft.class_4537;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SplashPotionItem extends class_4537 {
	public SplashPotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		world.playSound(
			null,
			playerEntity.x,
			playerEntity.y,
			playerEntity.z,
			SoundEvents.ENTITY_SPLASH_POTION_THROW,
			SoundCategory.PLAYERS,
			0.5F,
			0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		return super.use(world, playerEntity, hand);
	}
}
