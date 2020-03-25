/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.class_4981;
import net.minecraft.entity.LivingEntity;
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
        if (entity instanceof class_4981) {
            class_4981 lv = (class_4981)((Object)entity);
            if (entity.isAlive() && !lv.isSaddled() && !entity.isBaby()) {
                lv.setSaddled(true);
                entity.world.playSound(user, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5f, 1.0f);
                stack.decrement(1);
                return true;
            }
        }
        return false;
    }
}

