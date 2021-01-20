/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Merchant {
    public void setCurrentCustomer(@Nullable PlayerEntity var1);

    @Nullable
    public PlayerEntity getCurrentCustomer();

    public TradeOfferList getOffers();

    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(TradeOfferList var1);

    public void trade(TradeOffer var1);

    public void onSellingItem(ItemStack var1);

    public World getMerchantWorld();

    public int getExperience();

    public void setExperienceFromServer(int var1);

    public boolean isLeveledMerchant();

    public SoundEvent getYesSound();

    default public boolean canRefreshTrades() {
        return false;
    }

    default public void sendOffers(PlayerEntity player, Text test, int levelProgress) {
        TradeOfferList tradeOfferList;
        OptionalInt optionalInt = player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> new MerchantScreenHandler(i, playerInventory, this), test));
        if (optionalInt.isPresent() && !(tradeOfferList = this.getOffers()).isEmpty()) {
            player.sendTradeOffers(optionalInt.getAsInt(), tradeOfferList, levelProgress, this.getExperience(), this.isLeveledMerchant(), this.canRefreshTrades());
        }
    }
}

