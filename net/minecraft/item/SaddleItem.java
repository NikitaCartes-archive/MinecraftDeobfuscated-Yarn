/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class SaddleItem
extends Item {
    public SaddleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean useOnEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
        if (livingEntity instanceof PigEntity) {
            PigEntity pigEntity = (PigEntity)livingEntity;
            if (pigEntity.isAlive() && !pigEntity.isSaddled() && !pigEntity.isBaby()) {
                pigEntity.setSaddled(true);
                pigEntity.world.playSound(playerEntity, pigEntity.getX(), pigEntity.getY(), pigEntity.getZ(), SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5f, 1.0f);
                itemStack.decrement(1);
            }
            return true;
        }
        return false;
    }
}

