package net.minecraft.village;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.MerchantContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface Trader {
	void setCurrentCustomer(@Nullable PlayerEntity playerEntity);

	@Nullable
	PlayerEntity getCurrentCustomer();

	TraderOfferList method_8264();

	@Environment(EnvType.CLIENT)
	void method_8261(@Nullable TraderOfferList traderOfferList);

	void method_8262(TradeOffer tradeOffer);

	void onSellingItem(ItemStack itemStack);

	World method_8260();

	int getExperience();

	void setExperienceFromServer(int i);

	boolean isLevelledTrader();

	SoundEvent method_18010();

	default boolean canRefreshTrades() {
		return false;
	}

	default void sendOffers(PlayerEntity playerEntity, Text text, int i) {
		OptionalInt optionalInt = playerEntity.openContainer(
			new ClientDummyContainerProvider((ix, playerInventory, playerEntityx) -> new MerchantContainer(ix, playerInventory, this), text)
		);
		if (optionalInt.isPresent()) {
			TraderOfferList traderOfferList = this.method_8264();
			if (!traderOfferList.isEmpty()) {
				playerEntity.method_17354(optionalInt.getAsInt(), traderOfferList, i, this.getExperience(), this.isLevelledTrader(), this.canRefreshTrades());
			}
		}
	}
}
