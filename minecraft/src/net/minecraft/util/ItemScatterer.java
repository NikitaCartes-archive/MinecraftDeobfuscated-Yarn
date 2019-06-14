package net.minecraft.util;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScatterer {
	private static final Random RANDOM = new Random();

	public static void method_5451(World world, BlockPos blockPos, Inventory inventory) {
		method_5450(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), inventory);
	}

	public static void method_5452(World world, Entity entity, Inventory inventory) {
		method_5450(world, entity.x, entity.y, entity.z, inventory);
	}

	private static void method_5450(World world, double d, double e, double f, Inventory inventory) {
		for (int i = 0; i < inventory.getInvSize(); i++) {
			method_5449(world, d, e, f, inventory.getInvStack(i));
		}
	}

	public static void method_17349(World world, BlockPos blockPos, DefaultedList<ItemStack> defaultedList) {
		defaultedList.forEach(itemStack -> method_5449(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack));
	}

	public static void method_5449(World world, double d, double e, double f, ItemStack itemStack) {
		double g = (double)EntityType.field_6052.getWidth();
		double h = 1.0 - g;
		double i = g / 2.0;
		double j = Math.floor(d) + RANDOM.nextDouble() * h + i;
		double k = Math.floor(e) + RANDOM.nextDouble() * h;
		double l = Math.floor(f) + RANDOM.nextDouble() * h + i;

		while (!itemStack.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(world, j, k, l, itemStack.split(RANDOM.nextInt(21) + 10));
			float m = 0.05F;
			itemEntity.setVelocity(RANDOM.nextGaussian() * 0.05F, RANDOM.nextGaussian() * 0.05F + 0.2F, RANDOM.nextGaussian() * 0.05F);
			world.spawnEntity(itemEntity);
		}
	}
}
