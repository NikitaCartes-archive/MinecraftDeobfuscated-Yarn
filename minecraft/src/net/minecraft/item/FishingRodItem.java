package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;

public class FishingRodItem extends Item implements Vanishable {
	public FishingRodItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (user.fishHook != null) {
			if (!world.isClient) {
				int i = user.fishHook.use(itemStack);
				itemStack.damage(i, user, p -> p.sendToolBreakStatus(hand));
			}

			world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE,
				SoundCategory.NEUTRAL,
				1.0F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
			);
			user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
		} else {
			world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ENTITY_FISHING_BOBBER_THROW,
				SoundCategory.NEUTRAL,
				0.5F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
			);
			if (!world.isClient) {
				int i = EnchantmentHelper.getLure(itemStack);
				int j = EnchantmentHelper.getLuckOfTheSea(itemStack);
				FishingBobberEntity fishingBobberEntity = new FishingBobberEntity(user, world, j, i);
				if (world.isNight()) {
					Vector3f vector3f = new Vector3f(-1.0F, 0.0F, 0.0F).rotateZ((float)((Math.PI * 2) * (double)world.getTimeOfDay() / 24000.0));
					if (user.getRotationVec(1.0F).toVector3f().dot(vector3f) > 0.98F) {
						fishingBobberEntity.field_44123 = true;
					}
				}

				world.spawnEntity(fishingBobberEntity);
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			user.emitGameEvent(GameEvent.ITEM_INTERACT_START);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
