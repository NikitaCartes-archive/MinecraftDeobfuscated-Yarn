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
    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        PigEntity pigEntity;
        if (entity instanceof PigEntity && (pigEntity = (PigEntity)entity).isAlive() && !pigEntity.isSaddled() && !pigEntity.isBaby()) {
            pigEntity.setSaddled(true);
            pigEntity.world.playSound(user, pigEntity.getX(), pigEntity.getY(), pigEntity.getZ(), SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5f, 1.0f);
            stack.decrement(1);
            return true;
        }
        return false;
    }
}

