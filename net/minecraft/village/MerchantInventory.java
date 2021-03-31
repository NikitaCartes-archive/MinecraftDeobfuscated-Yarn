/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.jetbrains.annotations.Nullable;

public class MerchantInventory
implements Inventory {
    private final Merchant merchant;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    @Nullable
    private TradeOffer tradeOffer;
    private int offerIndex;
    private int merchantRewardedExperience;

    public MerchantInventory(Merchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = this.inventory.get(slot);
        if (slot == 2 && !itemStack.isEmpty()) {
            return Inventories.splitStack(this.inventory, slot, itemStack.getCount());
        }
        ItemStack itemStack2 = Inventories.splitStack(this.inventory, slot, amount);
        if (!itemStack2.isEmpty() && this.needsOfferUpdate(slot)) {
            this.updateOffers();
        }
        return itemStack2;
    }

    private boolean needsOfferUpdate(int slot) {
        return slot == 0 || slot == 1;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (this.needsOfferUpdate(slot)) {
            this.updateOffers();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.merchant.getCurrentCustomer() == player;
    }

    @Override
    public void markDirty() {
        this.updateOffers();
    }

    public void updateOffers() {
        ItemStack itemStack2;
        ItemStack itemStack;
        this.tradeOffer = null;
        if (this.inventory.get(0).isEmpty()) {
            itemStack = this.inventory.get(1);
            itemStack2 = ItemStack.EMPTY;
        } else {
            itemStack = this.inventory.get(0);
            itemStack2 = this.inventory.get(1);
        }
        if (itemStack.isEmpty()) {
            this.setStack(2, ItemStack.EMPTY);
            this.merchantRewardedExperience = 0;
            return;
        }
        TradeOfferList tradeOfferList = this.merchant.getOffers();
        if (!tradeOfferList.isEmpty()) {
            TradeOffer tradeOffer = tradeOfferList.getValidOffer(itemStack, itemStack2, this.offerIndex);
            if (tradeOffer == null || tradeOffer.isDisabled()) {
                this.tradeOffer = tradeOffer;
                tradeOffer = tradeOfferList.getValidOffer(itemStack2, itemStack, this.offerIndex);
            }
            if (tradeOffer != null && !tradeOffer.isDisabled()) {
                this.tradeOffer = tradeOffer;
                this.setStack(2, tradeOffer.copySellItem());
                this.merchantRewardedExperience = tradeOffer.getMerchantExperience();
            } else {
                this.setStack(2, ItemStack.EMPTY);
                this.merchantRewardedExperience = 0;
            }
        }
        this.merchant.onSellingItem(this.getStack(2));
    }

    @Nullable
    public TradeOffer getTradeOffer() {
        return this.tradeOffer;
    }

    public void setOfferIndex(int index) {
        this.offerIndex = index;
        this.updateOffers();
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    public int getMerchantRewardedExperience() {
        return this.merchantRewardedExperience;
    }
}

