package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
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
		world.playSound(
			null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			ThrownEggEntity thrownEggEntity = new ThrownEggEntity(world, playerEntity);
			thrownEggEntity.setItem(itemStack);
			thrownEggEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(thrownEggEntity);
		}

		playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!playerEntity.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		return TypedActionResult.method_22427(itemStack);
	}
}
