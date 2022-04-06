package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScatterer {
	public static void spawn(World world, BlockPos pos, Inventory inventory) {
		spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
	}

	public static void spawn(World world, Entity entity, Inventory inventory) {
		spawn(world, entity.getX(), entity.getY(), entity.getZ(), inventory);
	}

	private static void spawn(World world, double x, double y, double z, Inventory inventory) {
		for (int i = 0; i < inventory.size(); i++) {
			spawn(world, x, y, z, inventory.getStack(i));
		}
	}

	public static void spawn(World world, BlockPos pos, DefaultedList<ItemStack> stacks) {
		stacks.forEach(stack -> spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack));
	}

	public static void spawn(World world, double x, double y, double z, ItemStack stack) {
		double d = (double)EntityType.ITEM.getWidth();
		double e = 1.0 - d;
		double f = d / 2.0;
		double g = Math.floor(x) + world.random.nextDouble() * e + f;
		double h = Math.floor(y) + world.random.nextDouble() * e;
		double i = Math.floor(z) + world.random.nextDouble() * e + f;

		while (!stack.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(world, g, h, i, stack.split(world.random.nextInt(21) + 10));
			float j = 0.05F;
			itemEntity.setVelocity(world.random.nextGaussian() * 0.05F, world.random.nextGaussian() * 0.05F + 0.2F, world.random.nextGaussian() * 0.05F);
			world.spawnEntity(itemEntity);
		}
	}
}
