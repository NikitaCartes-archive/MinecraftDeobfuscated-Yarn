/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ItemPropertyGetter {
    @Environment(value=EnvType.CLIENT)
    public float call(ItemStack var1, @Nullable World var2, @Nullable LivingEntity var3);
}

