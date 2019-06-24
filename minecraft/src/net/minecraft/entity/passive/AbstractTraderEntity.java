package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public abstract class AbstractTraderEntity extends PassiveEntity implements Npc, Trader {
	private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(AbstractTraderEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Nullable
	private PlayerEntity customer;
	@Nullable
	protected TraderOfferList offers;
	private final BasicInventory inventory = new BasicInventory(8);

	public AbstractTraderEntity(EntityType<? extends AbstractTraderEntity> entityType, World world) {
		super(entityType, world);
	}

	public int getHeadRollingTimeLeft() {
		return this.dataTracker.get(HEAD_ROLLING_TIME_LEFT);
	}

	public void setHeadRollingTimeLeft(int i) {
		this.dataTracker.set(HEAD_ROLLING_TIME_LEFT, i);
	}

	@Override
	public int getExperience() {
		return 0;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return this.isBaby() ? 0.81F : 1.62F;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HEAD_ROLLING_TIME_LEFT, 0);
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

	@Override
	public SoundEvent method_18010() {
		return SoundEvents.ENTITY_VILLAGER_YES;
	}

	protected SoundEvent getTradingSound(boolean bl) {
		return bl ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
	}

	public void playCelebrateSound() {
		this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
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

	@Nullable
	@Override
	public Entity changeDimension(DimensionType dimensionType) {
		this.resetCustomer();
		return super.changeDimension(dimensionType);
	}

	protected void resetCustomer() {
		this.setCurrentCustomer(null);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		this.resetCustomer();
	}

	@Environment(EnvType.CLIENT)
	protected void produceParticles(ParticleEffect particleEffect) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleEffect,
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
