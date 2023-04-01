package net.minecraft.vote;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.Property;

public class MidasCurser {
	public static ItemStack curse(ItemStack stack) {
		Item item = curse(stack.getItem());
		if (stack.getItem() == item) {
			return stack;
		} else {
			ItemStack itemStack = new ItemStack(item, stack.getCount());
			if (stack.hasNbt()) {
				itemStack.setNbt(stack.getNbt());
			}

			return itemStack;
		}
	}

	public static Item curse(Item item) {
		RegistryEntry.Reference<Item> reference = item.getRegistryEntry();
		if (reference.isIn(ItemTags.RAILS)) {
			return Items.POWERED_RAIL;
		} else if (reference.isIn(ItemTags.PICKAXES)) {
			return Items.GOLDEN_PICKAXE;
		} else if (reference.isIn(ItemTags.SWORDS)) {
			return Items.GOLDEN_SWORD;
		} else if (reference.isIn(ItemTags.SHOVELS)) {
			return Items.GOLDEN_SHOVEL;
		} else if (reference.isIn(ItemTags.HOES)) {
			return Items.GOLDEN_HOE;
		} else if (reference.isIn(ItemTags.AXES)) {
			return Items.GOLDEN_AXE;
		} else if (item instanceof ArmorItem armorItem) {
			return switch (armorItem.getType()) {
				case HELMET -> Items.GOLDEN_HELMET;
				case CHESTPLATE -> Items.GOLDEN_CHESTPLATE;
				case LEGGINGS -> Items.GOLDEN_LEGGINGS;
				case BOOTS -> Items.GOLDEN_BOOTS;
			};
		} else if (item == Items.APPLE) {
			return Items.GOLDEN_APPLE;
		} else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
			return Items.ENCHANTED_GOLDEN_APPLE;
		} else if (item == Items.CARROT || item == Items.GOLDEN_CARROT) {
			return Items.GOLDEN_CARROT;
		} else if (item == Items.ARROW || item == Items.SPECTRAL_ARROW) {
			return Items.SPECTRAL_ARROW;
		} else if (item == Items.IRON_INGOT || item == Items.COPPER_INGOT || item == Items.NETHERITE_INGOT || item == Items.GOLD_INGOT) {
			return Items.GOLD_INGOT;
		} else if (item == Items.IRON_NUGGET || item == Items.GOLD_NUGGET) {
			return Items.GOLD_NUGGET;
		} else if (item == Items.RAW_GOLD || item == Items.RAW_COPPER || item == Items.RAW_IRON) {
			return Items.RAW_GOLD;
		} else if (item == Items.MELON_SLICE || item == Items.GLISTERING_MELON_SLICE) {
			return Items.GLISTERING_MELON_SLICE;
		} else if (item instanceof HorseArmorItem) {
			return Items.GOLDEN_HORSE_ARMOR;
		} else {
			return item instanceof BlockItem blockItem ? curse(blockItem.getBlock()).asItem() : Items.GOLD_INGOT;
		}
	}

	public static Block curse(Block block) {
		if (block == Blocks.RAW_COPPER_BLOCK || block == Blocks.RAW_IRON_BLOCK || block == Blocks.RAW_GOLD_BLOCK) {
			return Blocks.RAW_GOLD_BLOCK;
		} else if (block == Blocks.GOLD_ORE
			|| block == Blocks.COPPER_ORE
			|| block == Blocks.IRON_ORE
			|| block == Blocks.COAL_ORE
			|| block == Blocks.EMERALD_ORE
			|| block == Blocks.REDSTONE_ORE
			|| block == Blocks.DIAMOND_ORE
			|| block == Blocks.LAPIS_ORE) {
			return Blocks.GOLD_ORE;
		} else if (block == Blocks.DEEPSLATE_GOLD_ORE
			|| block == Blocks.DEEPSLATE_COPPER_ORE
			|| block == Blocks.DEEPSLATE_IRON_ORE
			|| block == Blocks.DEEPSLATE_COAL_ORE
			|| block == Blocks.DEEPSLATE_EMERALD_ORE
			|| block == Blocks.DEEPSLATE_REDSTONE_ORE
			|| block == Blocks.DEEPSLATE_DIAMOND_ORE
			|| block == Blocks.DEEPSLATE_LAPIS_ORE) {
			return Blocks.DEEPSLATE_GOLD_ORE;
		} else {
			return block != Blocks.NETHER_QUARTZ_ORE && block != Blocks.NETHER_GOLD_ORE ? Blocks.GOLD_BLOCK : Blocks.NETHER_GOLD_ORE;
		}
	}

	public static BlockState curse(BlockState state) {
		Block block = curse(state.getBlock());
		return block == state.getBlock() ? state : copyProperties(state, block.getDefaultState());
	}

	private static BlockState copyProperties(BlockState from, BlockState to) {
		for (Property<?> property : from.getProperties()) {
			to = copyProperty(from, to, property);
		}

		return to;
	}

	private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
		if (to.contains(property)) {
			to = to.with(property, from.get(property));
		}

		return to;
	}
}
