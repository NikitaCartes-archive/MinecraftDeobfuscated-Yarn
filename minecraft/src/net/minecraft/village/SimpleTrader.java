package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SimpleTrader implements Trader {
	private final TraderInventory traderInventory;
	private final PlayerEntity player;
	private TraderOfferList field_7442 = new TraderOfferList();
	private int experience;

	public SimpleTrader(PlayerEntity playerEntity) {
		this.player = playerEntity;
		this.traderInventory = new TraderInventory(this);
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.player;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
	}

	@Override
	public TraderOfferList method_8264() {
		return this.field_7442;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_8261(@Nullable TraderOfferList traderOfferList) {
		this.field_7442 = traderOfferList;
	}

	@Override
	public void method_8262(TradeOffer tradeOffer) {
		tradeOffer.use();
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
	}

	@Override
	public World method_8260() {
		return this.player.field_6002;
	}

	@Override
	public int getExperience() {
		return this.experience;
	}

	@Override
	public void setExperienceFromServer(int i) {
		this.experience = i;
	}

	@Override
	public boolean isLevelledTrader() {
		return true;
	}

	@Override
	public SoundEvent method_18010() {
		return SoundEvents.field_14815;
	}
}
