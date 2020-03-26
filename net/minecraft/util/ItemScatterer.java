/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScatterer {
    private static final Random RANDOM = new Random();

    public static void spawn(World world, BlockPos blockPos, Inventory inventory) {
        ItemScatterer.spawn(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), inventory);
    }

    public static void spawn(World world, Entity entity, Inventory inventory) {
        ItemScatterer.spawn(world, entity.getX(), entity.getY(), entity.getZ(), inventory);
    }

    private static void spawn(World world, double x, double y, double z, Inventory inventory) {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemScatterer.spawn(world, x, y, z, inventory.getStack(i));
        }
    }

    public static void spawn(World world, BlockPos pos, DefaultedList<ItemStack> items) {
        items.forEach(itemStack -> ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack));
    }

    public static void spawn(World world, double x, double y, double z, ItemStack item) {
        double d = EntityType.ITEM.getWidth();
        double e = 1.0 - d;
        double f = d / 2.0;
        double g = Math.floor(x) + RANDOM.nextDouble() * e + f;
        double h = Math.floor(y) + RANDOM.nextDouble() * e;
        double i = Math.floor(z) + RANDOM.nextDouble() * e + f;
        while (!item.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(world, g, h, i, item.split(RANDOM.nextInt(21) + 10));
            float j = 0.05f;
            itemEntity.setVelocity(RANDOM.nextGaussian() * (double)0.05f, RANDOM.nextGaussian() * (double)0.05f + (double)0.2f, RANDOM.nextGaussian() * (double)0.05f);
            world.spawnEntity(itemEntity);
        }
    }
}

