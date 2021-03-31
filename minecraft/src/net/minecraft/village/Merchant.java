package net.minecraft.village;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface Merchant {
	void setCurrentCustomer(@Nullable PlayerEntity customer);

	@Nullable
	PlayerEntity getCurrentCustomer();

	TradeOfferList getOffers();

	void setOffersFromServer(TradeOfferList offers);

	void trade(TradeOffer offer);

	void onSellingItem(ItemStack stack);

	World getMerchantWorld();

	int getExperience();

	void setExperienceFromServer(int experience);

	boolean isLeveledMerchant();

	SoundEvent getYesSound();

	default boolean canRefreshTrades() {
		return false;
	}

	default void sendOffers(PlayerEntity player, Text test, int levelProgress) {
		OptionalInt optionalInt = player.openHandledScreen(
			new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> new MerchantScreenHandler(syncId, playerInventory, this), test)
		);
		if (optionalInt.isPresent()) {
			TradeOfferList tradeOfferList = this.getOffers();
			if (!tradeOfferList.isEmpty()) {
				player.sendTradeOffers(optionalInt.getAsInt(), tradeOfferList, levelProgress, this.getExperience(), this.isLeveledMerchant(), this.canRefreshTrades());
			}
		}
	}
}
