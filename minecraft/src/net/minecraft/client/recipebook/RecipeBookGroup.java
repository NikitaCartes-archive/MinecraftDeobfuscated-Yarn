package net.minecraft.client.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategory;

@Environment(EnvType.CLIENT)
public enum RecipeBookGroup {
	field_1809(new ItemStack(Items.field_8251)),
	field_1806(new ItemStack(Blocks.field_10104)),
	field_1803(new ItemStack(Items.field_8725)),
	field_1813(new ItemStack(Items.field_8475), new ItemStack(Items.field_8845)),
	field_1810(new ItemStack(Items.field_8187), new ItemStack(Items.field_8279)),
	field_1804(new ItemStack(Items.field_8251)),
	field_1808(new ItemStack(Items.field_8389)),
	field_1811(new ItemStack(Blocks.field_10340)),
	field_1812(new ItemStack(Items.field_8187), new ItemStack(Items.field_8687)),
	field_17110(new ItemStack(Items.field_8251)),
	field_17111(new ItemStack(Blocks.field_10080)),
	field_17112(new ItemStack(Items.field_8699), new ItemStack(Items.field_8416)),
	field_17113(new ItemStack(Items.field_8251)),
	field_17114(new ItemStack(Items.field_8389)),
	field_17764(new ItemStack(Items.CHISELED_STONE_BRICKS)),
	field_25624(new ItemStack(Items.field_22028)),
	field_17765(new ItemStack(Items.field_8389)),
	field_25625(new ItemStack(Items.BARRIER));

	public static final List<RecipeBookGroup> SMOKER = ImmutableList.of(field_17113, field_17114);
	public static final List<RecipeBookGroup> BLAST_FURNACE = ImmutableList.of(field_17110, field_17111, field_17112);
	public static final List<RecipeBookGroup> FURNACE = ImmutableList.of(field_1804, field_1808, field_1811, field_1812);
	public static final List<RecipeBookGroup> CRAFTING = ImmutableList.of(field_1809, field_1813, field_1806, field_1810, field_1803);
	public static final Map<RecipeBookGroup, List<RecipeBookGroup>> field_25783 = ImmutableMap.of(
		field_1809,
		ImmutableList.of(field_1813, field_1806, field_1810, field_1803),
		field_1804,
		ImmutableList.of(field_1808, field_1811, field_1812),
		field_17110,
		ImmutableList.of(field_17111, field_17112),
		field_17113,
		ImmutableList.of(field_17114)
	);
	private final List<ItemStack> icons;

	private RecipeBookGroup(ItemStack... entries) {
		this.icons = ImmutableList.copyOf(entries);
	}

	public static List<RecipeBookGroup> method_30285(RecipeBookCategory recipeBookCategory) {
		switch (recipeBookCategory) {
			case field_25763:
				return CRAFTING;
			case field_25764:
				return FURNACE;
			case field_25765:
				return BLAST_FURNACE;
			case field_25766:
				return SMOKER;
			default:
				return ImmutableList.of();
		}
	}

	public List<ItemStack> getIcons() {
		return this.icons;
	}
}
