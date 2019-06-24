package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EnderPearlItem extends Item {
	public EnderPearlItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!playerEntity.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		world.playSound(
			null,
			playerEntity.x,
			playerEntity.y,
			playerEntity.z,
			SoundEvents.ENTITY_ENDER_PEARL_THROW,
			SoundCategory.NEUTRAL,
			0.5F,
			0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		playerEntity.getItemCooldownManager().set(this, 20);
		if (!world.isClient) {
			ThrownEnderpearlEntity thrownEnderpearlEntity = new ThrownEnderpearlEntity(world, playerEntity);
			thrownEnderpearlEntity.setItem(itemStack);
			thrownEnderpearlEntity.method_19207(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(thrownEnderpearlEntity);
		}

		playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
