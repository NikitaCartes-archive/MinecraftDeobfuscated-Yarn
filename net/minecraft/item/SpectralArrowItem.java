/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpectralArrowItem
extends ArrowItem {
    public SpectralArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ProjectileEntity createProjectile(World world, ItemStack itemStack, LivingEntity livingEntity) {
        return new SpectralArrowEntity(world, livingEntity);
    }
}

