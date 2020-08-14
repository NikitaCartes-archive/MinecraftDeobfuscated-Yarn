package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EnderPearlItem extends Item {
	public EnderPearlItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound(
			null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		user.getItemCooldownManager().set(this, 20);
		if (!world.isClient) {
			EnderPearlEntity enderPearlEntity = new EnderPearlEntity(world, user);
			enderPearlEntity.setItem(itemStack);
			enderPearlEntity.setProperties(user, user.pitch, user.yaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(enderPearlEntity);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		return TypedActionResult.method_29237(itemStack, world.isClient());
	}
}
