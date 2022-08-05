package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Contains utility methods for spawning item entities scattered around a certain position.
 * All methods consume the item stack, so there is no need for decrementing the
 * stack size yourself.
 * 
 * @see net.minecraft.block.AbstractBlock#onStateReplaced
 */
public class ItemScatterer {
	/**
	 * Spawns item entities from {@code inventory} around {@code pos}.
	 */
	public static void spawn(World world, BlockPos pos, Inventory inventory) {
		spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
	}

	/**
	 * Spawns item entities from {@code inventory} around {@code entity}'s position.
	 */
	public static void spawn(World world, Entity entity, Inventory inventory) {
		spawn(world, entity.getX(), entity.getY(), entity.getZ(), inventory);
	}

	/**
	 * Spawns item entities from {@code inventory} around the given position.
	 */
	private static void spawn(World world, double x, double y, double z, Inventory inventory) {
		for (int i = 0; i < inventory.size(); i++) {
			spawn(world, x, y, z, inventory.getStack(i));
		}
	}

	/**
	 * Spawns item entities from {@code stacks} around {@code pos}.
	 */
	public static void spawn(World world, BlockPos pos, DefaultedList<ItemStack> stacks) {
		stacks.forEach(stack -> spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack));
	}

	/**
	 * Spawns an item entity of {@code stack} around the given position.
	 */
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
			itemEntity.setVelocity(
				world.random.nextTriangular(0.0, 0.11485000171139836),
				world.random.nextTriangular(0.2, 0.11485000171139836),
				world.random.nextTriangular(0.0, 0.11485000171139836)
			);
			world.spawnEntity(itemEntity);
		}
	}
}
