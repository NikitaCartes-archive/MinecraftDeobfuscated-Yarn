/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PotionItem
extends Item {
    public PotionItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getDefaultStack() {
        return PotionUtil.setPotion(super.getDefaultStack(), Potions.WATER);
    }

    @Override
    public ItemStack onItemFinishedUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
        PlayerEntity playerEntity;
        PlayerEntity playerEntity2 = playerEntity = livingEntity instanceof PlayerEntity ? (PlayerEntity)livingEntity : null;
        if (playerEntity == null || !playerEntity.abilities.creativeMode) {
            itemStack.subtractAmount(1);
        }
        if (playerEntity instanceof ServerPlayerEntity) {
            Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)playerEntity, itemStack);
        }
        if (!world.isClient) {
            List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
            for (StatusEffectInstance statusEffectInstance : list) {
                if (statusEffectInstance.getEffectType().isInstant()) {
                    statusEffectInstance.getEffectType().applyInstantEffect(playerEntity, playerEntity, livingEntity, statusEffectInstance.getAmplifier(), 1.0);
                    continue;
                }
                livingEntity.addPotionEffect(new StatusEffectInstance(statusEffectInstance));
            }
        }
        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        if (playerEntity == null || !playerEntity.abilities.creativeMode) {
            if (itemStack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (playerEntity != null) {
                playerEntity.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return itemStack;
    }

    @Override
    public int getMaxUseTime(ItemStack itemStack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.setCurrentHand(hand);
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
    }

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        return PotionUtil.getPotion(itemStack).getName(this.getTranslationKey() + ".effect.");
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void buildTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
        PotionUtil.buildTooltip(itemStack, list, 1.0f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return super.hasEnchantmentGlint(itemStack) || !PotionUtil.getPotionEffects(itemStack).isEmpty();
    }

    @Override
    public void appendItemsForGroup(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
        if (this.isInItemGroup(itemGroup)) {
            for (Potion potion : Registry.POTION) {
                if (potion == Potions.EMPTY) continue;
                defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion));
            }
        }
    }
}

