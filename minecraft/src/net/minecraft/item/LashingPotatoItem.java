package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LashingPotatoHookEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class LashingPotatoItem extends Item {
	public LashingPotatoItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		LashingPotatoHookEntity lashingPotatoHookEntity = user.lashingPotatoHook;
		if (lashingPotatoHookEntity != null) {
			method_59049(world, user, lashingPotatoHookEntity);
		} else {
			if (!world.isClient) {
				itemStack.damage(1, user, LivingEntity.getSlotForHand(hand));
			}

			this.method_59048(world, user);
		}

		return TypedActionResult.success(itemStack, world.isClient);
	}

	private void method_59048(World world, PlayerEntity player) {
		if (!world.isClient) {
			world.spawnEntity(new LashingPotatoHookEntity(world, player));
		}

		player.incrementStat(Stats.USED.getOrCreateStat(this));
		world.playSound(
			null,
			player.getX(),
			player.getY(),
			player.getZ(),
			SoundEvents.ENTITY_FISHING_BOBBER_THROW,
			SoundCategory.NEUTRAL,
			0.5F,
			0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
		);
		player.emitGameEvent(GameEvent.ITEM_INTERACT_START);
	}

	private static void method_59049(World world, PlayerEntity player, LashingPotatoHookEntity lashingPotatoHookEntity) {
		if (!world.isClient()) {
			lashingPotatoHookEntity.discard();
			player.lashingPotatoHook = null;
		}

		world.playSound(
			null,
			player.getX(),
			player.getY(),
			player.getZ(),
			SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE,
			SoundCategory.NEUTRAL,
			1.0F,
			0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
		);
		player.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
	}
}
