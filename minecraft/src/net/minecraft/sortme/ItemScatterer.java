package net.minecraft.sortme;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScatterer {
	private static final Random RANDOM = new Random();

	public static void spawn(World world, BlockPos blockPos, Inventory inventory) {
		spawn(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), inventory);
	}

	public static void spawn(World world, Entity entity, Inventory inventory) {
		spawn(world, entity.x, entity.y, entity.z, inventory);
	}

	private static void spawn(World world, double d, double e, double f, Inventory inventory) {
		for (int i = 0; i < inventory.getInvSize(); i++) {
			spawn(world, d, e, f, inventory.getInvStack(i));
		}
	}

	public static void spawn(World world, BlockPos blockPos, DefaultedList<ItemStack> defaultedList) {
		defaultedList.forEach(itemStack -> spawn(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack));
	}

	public static void spawn(World world, double d, double e, double f, ItemStack itemStack) {
		float g = EntityType.ITEM.getWidth();
		float h = 1.0F - g;
		float i = g / 2.0F;
		float j = RANDOM.nextFloat() * h + i;
		float k = RANDOM.nextFloat() * h;
		float l = RANDOM.nextFloat() * h + i;

		while (!itemStack.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(world, d + (double)j, e + (double)k, f + (double)l, itemStack.split(RANDOM.nextInt(21) + 10));
			float m = 0.05F;
			itemEntity.velocityX = RANDOM.nextGaussian() * 0.05F;
			itemEntity.velocityY = RANDOM.nextGaussian() * 0.05F + 0.2F;
			itemEntity.velocityZ = RANDOM.nextGaussian() * 0.05F;
			world.spawnEntity(itemEntity);
		}
	}
}
