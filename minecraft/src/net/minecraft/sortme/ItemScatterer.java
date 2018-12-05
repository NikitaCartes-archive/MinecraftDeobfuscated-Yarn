package net.minecraft.sortme;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				spawn(world, d, e, f, itemStack);
			}
		}
	}

	public static void spawn(World world, double d, double e, double f, ItemStack itemStack) {
		float g = 0.75F;
		float h = 0.125F;
		float i = RANDOM.nextFloat() * 0.75F + 0.125F;
		float j = RANDOM.nextFloat() * 0.75F;
		float k = RANDOM.nextFloat() * 0.75F + 0.125F;

		while (!itemStack.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(world, d + (double)i, e + (double)j, f + (double)k, itemStack.split(RANDOM.nextInt(21) + 10));
			float l = 0.05F;
			itemEntity.velocityX = RANDOM.nextGaussian() * 0.05F;
			itemEntity.velocityY = RANDOM.nextGaussian() * 0.05F + 0.2F;
			itemEntity.velocityZ = RANDOM.nextGaussian() * 0.05F;
			world.spawnEntity(itemEntity);
		}
	}
}
