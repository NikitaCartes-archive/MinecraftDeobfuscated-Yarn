/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SimpleTrader
implements Trader {
    private final TraderInventory traderInventory;
    private final PlayerEntity player;
    private TraderOfferList recipeList = new TraderOfferList();
    private int experience;

    public SimpleTrader(PlayerEntity playerEntity) {
        this.player = playerEntity;
        this.traderInventory = new TraderInventory(this);
    }

    @Override
    @Nullable
    public PlayerEntity getCurrentCustomer() {
        return this.player;
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity customer) {
    }

    @Override
    public TraderOfferList getOffers() {
        return this.recipeList;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(@Nullable TraderOfferList traderOfferList) {
        this.recipeList = traderOfferList;
    }

    @Override
    public void trade(TradeOffer tradeOffer) {
        tradeOffer.use();
    }

    @Override
    public void onSellingItem(ItemStack itemStack) {
    }

    @Override
    public World getTraderWorld() {
        return this.player.world;
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    @Override
    public void setExperienceFromServer(int experience) {
        this.experience = experience;
    }

    @Override
    public boolean isLevelledTrader() {
        return true;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }
}

