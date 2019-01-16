package net.minecraft.village;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.MerchantContainer;
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

	World getVillagerWorld();

	BlockPos getVillagerPos();

	default void method_17449(PlayerEntity playerEntity, TextComponent textComponent) {
		OptionalInt optionalInt = playerEntity.openContainer(
			new ClientDummyContainerProvider((i, playerInventory, playerEntityx) -> new MerchantContainer(i, playerInventory, this), textComponent)
		);
		if (optionalInt.isPresent()) {
			VillagerRecipeList villagerRecipeList = this.getRecipes();
			if (!villagerRecipeList.isEmpty()) {
				playerEntity.sendVillagerRecipes(optionalInt.getAsInt(), villagerRecipeList);
			}
		}
	}
}
