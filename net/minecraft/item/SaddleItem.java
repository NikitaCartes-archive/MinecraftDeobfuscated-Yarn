/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;

public class SaddleItem
extends Item {
    public SaddleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        Saddleable saddleable;
        if (entity instanceof Saddleable && entity.isAlive() && !(saddleable = (Saddleable)((Object)entity)).isSaddled() && saddleable.canBeSaddled()) {
            saddleable.saddle(SoundCategory.NEUTRAL);
            stack.decrement(1);
            return true;
        }
        return false;
    }
}

