package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Villager {
	void setCurrentCustomer(@Nullable PlayerEntity playerEntity);

	@Nullable
	PlayerEntity getCurrentCustomer();

	VillagerRecipeList getRecipes();

	@Environment(EnvType.CLIENT)
	void setServerRecipes(@Nullable VillagerRecipeList villagerRecipeList);

	void useRecipe(VillagerRecipe villagerRecipe);

	void onSellingItem(ItemStack itemStack);

	TextComponent getDisplayName();

	World getVillagerWorld();

	BlockPos getVillagerPos();
}
