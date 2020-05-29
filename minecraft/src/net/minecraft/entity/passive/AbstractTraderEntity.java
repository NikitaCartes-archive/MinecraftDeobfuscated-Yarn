package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractTraderEntity extends PassiveEntity implements Npc, Trader {
	private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(AbstractTraderEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Nullable
	private PlayerEntity customer;
	@Nullable
	protected TraderOfferList offers;
	private final SimpleInventory inventory = new SimpleInventory(8);

	public AbstractTraderEntity(EntityType<? extends AbstractTraderEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
	}

	@Override
	public EntityData initialize(
		WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData();
			((PassiveEntity.PassiveData)entityData).setBabyAllowed(false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	public int getHeadRollingTimeLeft() {
		return this.dataTracker.get(HEAD_ROLLING_TIME_LEFT);
	}

	public void setHeadRollingTimeLeft(int ticks) {
		this.dataTracker.set(HEAD_ROLLING_TIME_LEFT, ticks);
	}

	@Override
	public int getExperience() {
		return 0;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.81F : 1.62F;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HEAD_ROLLING_TIME_LEFT, 0);
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity customer) {
		this.customer = customer;
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
	public void setOffersFromServer(@Nullable TraderOfferList offers) {
	}

	@Override
	public void setExperienceFromServer(int experience) {
	}

	@Override
	public void trade(TradeOffer offer) {
		offer.use();
		this.ambientSoundChance = -this.getMinAmbientSoundDelay();
		this.afterUsing(offer);
		if (this.customer instanceof ServerPlayerEntity) {
			Criteria.VILLAGER_TRADE.handle((ServerPlayerEntity)this.customer, this, offer.getMutableSellItem());
		}
	}

	protected abstract void afterUsing(TradeOffer offer);

	@Override
	public boolean isLevelledTrader() {
		return true;
	}

	@Override
	public void onSellingItem(ItemStack stack) {
		if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
			this.ambientSoundChance = -this.getMinAmbientSoundDelay();
			this.playSound(this.getTradingSound(!stack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public SoundEvent getYesSound() {
		return SoundEvents.ENTITY_VILLAGER_YES;
	}

	protected SoundEvent getTradingSound(boolean sold) {
		return sold ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
	}

	public void playCelebrateSound() {
		this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		TraderOfferList traderOfferList = this.getOffers();
		if (!traderOfferList.isEmpty()) {
			tag.put("Offers", traderOfferList.toTag());
		}

		tag.put("Inventory", this.inventory.getTags());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("Offers", 10)) {
			this.offers = new TraderOfferList(tag.getCompound("Offers"));
		}

		this.inventory.readTags(tag.getList("Inventory", 10));
	}

	@Nullable
	@Override
	public Entity changeDimension(RegistryKey<World> newDimension) {
		this.resetCustomer();
		return super.changeDimension(newDimension);
	}

	protected void resetCustomer() {
		this.setCurrentCustomer(null);
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		this.resetCustomer();
	}

	@Environment(EnvType.CLIENT)
	protected void produceParticles(ParticleEffect parameters) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return false;
	}

	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (super.equip(slot, item)) {
			return true;
		} else {
			int i = slot - 300;
			if (i >= 0 && i < this.inventory.size()) {
				this.inventory.setStack(i, item);
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

	protected void fillRecipesFromPool(TraderOfferList recipeList, TradeOffers.Factory[] pool, int count) {
		Set<Integer> set = Sets.<Integer>newHashSet();
		if (pool.length > count) {
			while (set.size() < count) {
				set.add(this.random.nextInt(pool.length));
			}
		} else {
			for (int i = 0; i < pool.length; i++) {
				set.add(i);
			}
		}

		for (Integer integer : set) {
			TradeOffers.Factory factory = pool[integer];
			TradeOffer tradeOffer = factory.create(this, this.random);
			if (tradeOffer != null) {
				recipeList.add(tradeOffer);
			}
		}
	}
}
