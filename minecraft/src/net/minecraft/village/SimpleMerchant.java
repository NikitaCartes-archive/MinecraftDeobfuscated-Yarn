package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SimpleMerchant implements Merchant {
	private final PlayerEntity player;
	private TradeOfferList recipeList = new TradeOfferList();
	private int experience;

	public SimpleMerchant(PlayerEntity playerEntity) {
		this.player = playerEntity;
	}

	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.player;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity customer) {
	}

	@Override
	public TradeOfferList getOffers() {
		return this.recipeList;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setOffersFromServer(TradeOfferList offers) {
		this.recipeList = offers;
	}

	@Override
	public void trade(TradeOffer offer) {
		offer.use();
	}

	@Override
	public void onSellingItem(ItemStack stack) {
	}

	@Override
	public World getMerchantWorld() {
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
	public boolean isLeveledMerchant() {
		return true;
	}

	@Override
	public SoundEvent getYesSound() {
		return SoundEvents.ENTITY_VILLAGER_YES;
	}
}
