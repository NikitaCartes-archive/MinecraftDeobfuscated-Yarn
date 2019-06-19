package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FishingRodItem extends Item {
	public FishingRodItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(new Identifier("cast"), (itemStack, world, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				boolean bl = livingEntity.getMainHandStack() == itemStack;
				boolean bl2 = livingEntity.getOffHandStack() == itemStack;
				if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
					bl2 = false;
				}

				return (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).fishHook != null ? 1.0F : 0.0F;
			}
		});
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (playerEntity.fishHook != null) {
			if (!world.isClient) {
				int i = playerEntity.fishHook.method_6957(itemStack);
				itemStack.damage(i, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
			}

			playerEntity.swingHand(hand);
			world.playSound(
				null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15093, SoundCategory.field_15254, 1.0F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
			);
		} else {
			world.playSound(
				null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_14596, SoundCategory.field_15254, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
			);
			if (!world.isClient) {
				int i = EnchantmentHelper.getLure(itemStack);
				int j = EnchantmentHelper.getLuckOfTheSea(itemStack);
				world.spawnEntity(new FishingBobberEntity(playerEntity, world, j, i));
			}

			playerEntity.swingHand(hand);
			playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
		}

		return new TypedActionResult<>(ActionResult.field_5812, itemStack);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
