package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PotionItem extends Item {
	public PotionItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_7854() {
		return PotionUtil.setPotion(super.method_7854(), Potions.field_8991);
	}

	@Override
	public ItemStack method_7861(ItemStack itemStack, World world, LivingEntity livingEntity) {
		PlayerEntity playerEntity = livingEntity instanceof PlayerEntity ? (PlayerEntity)livingEntity : null;
		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
		}

		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.CONSUME_ITEM.method_8821((ServerPlayerEntity)playerEntity, itemStack);
		}

		if (!world.isClient) {
			for (StatusEffectInstance statusEffectInstance : PotionUtil.getPotionEffects(itemStack)) {
				if (statusEffectInstance.getEffectType().isInstant()) {
					statusEffectInstance.getEffectType().method_5564(playerEntity, playerEntity, livingEntity, statusEffectInstance.getAmplifier(), 1.0);
				} else {
					livingEntity.addPotionEffect(new StatusEffectInstance(statusEffectInstance));
				}
			}
		}

		if (playerEntity != null) {
			playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
		}

		if (playerEntity == null || !playerEntity.abilities.creativeMode) {
			if (itemStack.isEmpty()) {
				return new ItemStack(Items.field_8469);
			}

			if (playerEntity != null) {
				playerEntity.inventory.method_7394(new ItemStack(Items.field_8469));
			}
		}

		return itemStack;
	}

	@Override
	public int method_7881(ItemStack itemStack) {
		return 32;
	}

	@Override
	public UseAction method_7853(ItemStack itemStack) {
		return UseAction.field_8946;
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return new TypedActionResult<>(ActionResult.field_5812, playerEntity.method_5998(hand));
	}

	@Override
	public String method_7866(ItemStack itemStack) {
		return PotionUtil.getPotion(itemStack).getName(this.getTranslationKey() + ".effect.");
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		PotionUtil.buildTooltip(itemStack, list, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(ItemStack itemStack) {
		return super.method_7886(itemStack) || !PotionUtil.getPotionEffects(itemStack).isEmpty();
	}

	@Override
	public void method_7850(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isInItemGroup(itemGroup)) {
			for (Potion potion : Registry.POTION) {
				if (potion != Potions.field_8984) {
					defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion));
				}
			}
		}
	}
}
