package net.minecraft.client.recipe.book;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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
	field_17114(new ItemStack(Items.field_8389));

	private final List<ItemStack> icons;

	private RecipeBookGroup(ItemStack... itemStacks) {
		this.icons = ImmutableList.copyOf(itemStacks);
	}

	public List<ItemStack> getIcons() {
		return this.icons;
	}
}
