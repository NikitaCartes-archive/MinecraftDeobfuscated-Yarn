package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SplashPotionItem extends ThrowablePotionItem {
	public SplashPotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		world.playSound(
			null, user.getX(), user.getY(), user.getZ(), SoundEvents.field_14910, SoundCategory.field_15248, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		return super.use(world, user, hand);
	}
}
