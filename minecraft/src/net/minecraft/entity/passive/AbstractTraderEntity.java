package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

public abstract class AbstractTraderEntity extends PassiveEntity implements Npc, Trader {
	@Nullable
	private PlayerEntity customer;
	@Nullable
	protected TraderOfferList offers;
	private final BasicInventory inventory = new BasicInventory(8);
	private int headRollingTimeLeft;

	public AbstractTraderEntity(EntityType<? extends AbstractTraderEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public int getExperience() {
		return 0;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return this.isChild() ? 0.81F : 1.62F;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
		this.customer = playerEntity;
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.customer;
	}

	public boolean hasCustomer() {
		return this.customer != null;
	}

	@Override
	public TraderOfferList getOffers() {
		if (this.offers == null) {
			this.offers = new TraderOfferList();
			this.fillRecipes();
		}

		return this.offers;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setOffersFromServer(@Nullable TraderOfferList traderOfferList) {
	}

	@Override
	public void setExperienceFromServer(int i) {
	}

	@Override
	public void trade(TradeOffer tradeOffer) {
		tradeOffer.use();
		this.ambientSoundChance = -this.getMinAmbientSoundDelay();
		this.playSound(this.getYesSound(), this.getSoundVolume(), this.getSoundPitch());
		this.afterUsing(tradeOffer);
		if (this.customer instanceof ServerPlayerEntity) {
			Criterions.VILLAGER_TRADE.handle((ServerPlayerEntity)this.customer, this, tradeOffer.getMutableSellItem());
		}
	}

	protected abstract void afterUsing(TradeOffer tradeOffer);

	@Override
	public boolean isLevelledTrader() {
		return true;
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
		if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
			this.ambientSoundChance = -this.getMinAmbientSoundDelay();
			this.playSound(this.getTradingSound(!itemStack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
		}
	}

	protected SoundEvent getYesSound() {
		return SoundEvents.field_14815;
	}

	protected SoundEvent getTradingSound(boolean bl) {
		return bl ? SoundEvents.field_14815 : SoundEvents.field_15008;
	}

	public void playCelebrateSound() {
		this.playSound(SoundEvents.field_19152, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		TraderOfferList traderOfferList = this.getOffers();
		if (!traderOfferList.isEmpty()) {
			compoundTag.put("Offers", traderOfferList.toTag());
		}

		ListTag listTag = new ListTag();

		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.toTag(new CompoundTag()));
			}
		}

		compoundTag.put("Inventory", listTag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Offers", 10)) {
			this.offers = new TraderOfferList(compoundTag.getCompound("Offers"));
		}

		ListTag listTag = compoundTag.getList("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.fromTag(listTag.getCompoundTag(i));
			if (!itemStack.isEmpty()) {
				this.inventory.add(itemStack);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	protected void produceParticles(ParticleParameters particleParameters) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 1.0 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}

	public int getHeadRollingTimeLeft() {
		return this.headRollingTimeLeft;
	}

	public void setHeadRollingTimeLeft(int i) {
		this.headRollingTimeLeft = i;
	}

	public BasicInventory getInventory() {
		return this.inventory;
	}

	@Override
	public boolean equip(int i, ItemStack itemStack) {
		if (super.equip(i, itemStack)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.inventory.getInvSize()) {
				this.inventory.setInvStack(j, itemStack);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public World getTraderWorld() {
		return this.world;
	}

	protected abstract void fillRecipes();

	protected void fillRecipesFromPool(TraderOfferList traderOfferList, TradeOffers.Factory[] factorys, int i) {
		Set<Integer> set = Sets.<Integer>newHashSet();
		if (factorys.length > i) {
			while (set.size() < i) {
				set.add(this.random.nextInt(factorys.length));
			}
		} else {
			for (int j = 0; j < factorys.length; j++) {
				set.add(j);
			}
		}

		for (Integer integer : set) {
			TradeOffers.Factory factory = factorys[integer];
			TradeOffer tradeOffer = factory.create(this, this.random);
			if (tradeOffer != null) {
				traderOfferList.add(tradeOffer);
			}
		}
	}
}
