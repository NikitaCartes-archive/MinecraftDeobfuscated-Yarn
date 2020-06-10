package net.minecraft.client.recipebook;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public enum RecipeBookGroup {
	SEARCH(new ItemStack(Items.COMPASS)),
	CRAFTING_BUILDING_BLOCKS(new ItemStack(Blocks.BRICKS)),
	CRAFTING_REDSTONE(new ItemStack(Items.REDSTONE)),
	CRAFTING_EQUIPMENT(new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD)),
	CRAFTING_MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE)),
	FURNACE_SEARCH(new ItemStack(Items.COMPASS)),
	FURNACE_FOOD(new ItemStack(Items.PORKCHOP)),
	FURNACE_BLOCKS(new ItemStack(Blocks.STONE)),
	FURNACE_MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.EMERALD)),
	BLAST_FURNACE_SEARCH(new ItemStack(Items.COMPASS)),
	BLAST_FURNACE_BLOCKS(new ItemStack(Blocks.REDSTONE_ORE)),
	BLAST_FURNACE_MISC(new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.GOLDEN_LEGGINGS)),
	SMOKER_SEARCH(new ItemStack(Items.COMPASS)),
	SMOKER_FOOD(new ItemStack(Items.PORKCHOP)),
	STONECUTTER(new ItemStack(Items.CHISELED_STONE_BRICKS)),
	SMITHING(new ItemStack(Items.NETHERITE_CHESTPLATE)),
	CAMPFIRE(new ItemStack(Items.PORKCHOP)),
	UNKNOWN(new ItemStack(Items.BARRIER));

	private final List<ItemStack> icons;

	private RecipeBookGroup(ItemStack... entries) {
		this.icons = ImmutableList.copyOf(entries);
	}

	public List<ItemStack> getIcons() {
		return this.icons;
	}
}
