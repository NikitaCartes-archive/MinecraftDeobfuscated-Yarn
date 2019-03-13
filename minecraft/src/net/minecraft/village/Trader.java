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
import net.minecraft.world.World;

public interface Trader {
	void setCurrentCustomer(@Nullable PlayerEntity playerEntity);

	@Nullable
	PlayerEntity getCurrentCustomer();

	TraderRecipeList method_8264();

	@Environment(EnvType.CLIENT)
	void method_8261(@Nullable TraderRecipeList traderRecipeList);

	void method_8262(TraderRecipe traderRecipe);

	void onSellingItem(ItemStack itemStack);

	World method_8260();

	int method_19269();

	void method_19271(int i);

	boolean method_19270();

	default void method_17449(PlayerEntity playerEntity, TextComponent textComponent, int i) {
		OptionalInt optionalInt = playerEntity.openContainer(
			new ClientDummyContainerProvider((ix, playerInventory, playerEntityx) -> new MerchantContainer(ix, playerInventory, this), textComponent)
		);
		if (optionalInt.isPresent()) {
			TraderRecipeList traderRecipeList = this.method_8264();
			if (!traderRecipeList.isEmpty()) {
				playerEntity.method_17354(optionalInt.getAsInt(), traderRecipeList, i, this.method_19269(), this.method_19270());
			}
		}
	}
}
