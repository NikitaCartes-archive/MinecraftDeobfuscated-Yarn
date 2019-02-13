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

public interface Trader {
	void setCurrentCustomer(@Nullable PlayerEntity playerEntity);

	@Nullable
	PlayerEntity getCurrentCustomer();

	TraderRecipeList getRecipes();

	@Environment(EnvType.CLIENT)
	void setServerRecipes(@Nullable TraderRecipeList traderRecipeList);

	void useRecipe(TraderRecipe traderRecipe);

	void onSellingItem(ItemStack itemStack);

	World getTraderWorld();

	BlockPos getTraderPos();

	default void sendRecipes(PlayerEntity playerEntity, TextComponent textComponent) {
		OptionalInt optionalInt = playerEntity.openContainer(
			new ClientDummyContainerProvider((i, playerInventory, playerEntityx) -> new MerchantContainer(i, playerInventory, this), textComponent)
		);
		if (optionalInt.isPresent()) {
			TraderRecipeList traderRecipeList = this.getRecipes();
			if (!traderRecipeList.isEmpty()) {
				playerEntity.sendVillagerRecipes(optionalInt.getAsInt(), traderRecipeList);
			}
		}
	}
}
