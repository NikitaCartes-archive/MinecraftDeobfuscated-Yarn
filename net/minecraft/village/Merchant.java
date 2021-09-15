/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import java.util.OptionalInt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.jetbrains.annotations.Nullable;

public interface Merchant {
    public void setCurrentCustomer(@Nullable PlayerEntity var1);

    @Nullable
    public PlayerEntity getCurrentCustomer();

    public TradeOfferList getOffers();

    public void setOffersFromServer(TradeOfferList var1);

    public void trade(TradeOffer var1);

    public void onSellingItem(ItemStack var1);

    public int getExperience();

    public void setExperienceFromServer(int var1);

    public boolean isLeveledMerchant();

    public SoundEvent getYesSound();

    default public boolean canRefreshTrades() {
        return false;
    }

    default public void sendOffers(PlayerEntity player2, Text test, int levelProgress) {
        TradeOfferList tradeOfferList;
        OptionalInt optionalInt = player2.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> new MerchantScreenHandler(syncId, playerInventory, this), test));
        if (optionalInt.isPresent() && !(tradeOfferList = this.getOffers()).isEmpty()) {
            player2.sendTradeOffers(optionalInt.getAsInt(), tradeOfferList, levelProgress, this.getExperience(), this.isLeveledMerchant(), this.canRefreshTrades());
        }
    }

    public boolean isClient();
}

