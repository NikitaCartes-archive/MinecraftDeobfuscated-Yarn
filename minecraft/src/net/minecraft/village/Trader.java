package net.minecraft.village;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface Trader {
	void setCurrentCustomer(@Nullable PlayerEntity customer);

	@Nullable
	PlayerEntity getCurrentCustomer();

	TraderOfferList getOffers();

	@Environment(EnvType.CLIENT)
	void setOffersFromServer(@Nullable TraderOfferList traderOfferList);

	void trade(TradeOffer tradeOffer);

	void onSellingItem(ItemStack itemStack);

	World getTraderWorld();

	int getExperience();

	void setExperienceFromServer(int experience);

	boolean isLevelledTrader();

	SoundEvent method_18010();

	default boolean canRefreshTrades() {
		return false;
	}

	default void sendOffers(PlayerEntity playerEntity, Text text, int i) {
		OptionalInt optionalInt = playerEntity.openContainer(
			new SimpleNamedContainerFactory((ix, playerInventory, playerEntityx) -> new MerchantContainer(ix, playerInventory, this), text)
		);
		if (optionalInt.isPresent()) {
			TraderOfferList traderOfferList = this.getOffers();
			if (!traderOfferList.isEmpty()) {
				playerEntity.sendTradeOffers(optionalInt.getAsInt(), traderOfferList, i, this.getExperience(), this.isLevelledTrader(), this.canRefreshTrades());
			}
		}
	}
}
