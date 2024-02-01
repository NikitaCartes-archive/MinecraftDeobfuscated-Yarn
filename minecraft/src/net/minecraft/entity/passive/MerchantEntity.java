package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.slf4j.Logger;

public abstract class MerchantEntity extends PassiveEntity implements InventoryOwner, Npc, Merchant {
	private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(MerchantEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_30599 = 300;
	private static final int INVENTORY_SIZE = 8;
	@Nullable
	private PlayerEntity customer;
	@Nullable
	protected TradeOfferList offers;
	private final SimpleInventory inventory = new SimpleInventory(8);

	public MerchantEntity(EntityType<? extends MerchantEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
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
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HEAD_ROLLING_TIME_LEFT, 0);
	}

	@Override
	public void setCustomer(@Nullable PlayerEntity customer) {
		this.customer = customer;
	}

	@Nullable
	@Override
	public PlayerEntity getCustomer() {
		return this.customer;
	}

	public boolean hasCustomer() {
		return this.customer != null;
	}

	@Override
	public TradeOfferList getOffers() {
		if (this.offers == null) {
			this.offers = new TradeOfferList();
			this.fillRecipes();
		}

		return this.offers;
	}

	@Override
	public void setOffersFromServer(@Nullable TradeOfferList offers) {
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
			Criteria.VILLAGER_TRADE.trigger((ServerPlayerEntity)this.customer, this, offer.getSellItem());
		}
	}

	protected abstract void afterUsing(TradeOffer offer);

	@Override
	public boolean isLeveledMerchant() {
		return true;
	}

	@Override
	public void onSellingItem(ItemStack stack) {
		if (!this.getWorld().isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
			this.ambientSoundChance = -this.getMinAmbientSoundDelay();
			this.playSound(this.getTradingSound(!stack.isEmpty()));
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
		this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		TradeOfferList tradeOfferList = this.getOffers();
		if (!tradeOfferList.isEmpty()) {
			nbt.put("Offers", Util.getResult(TradeOfferList.CODEC.encodeStart(NbtOps.INSTANCE, tradeOfferList), IllegalStateException::new));
		}

		this.writeInventory(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Offers")) {
			TradeOfferList.CODEC
				.parse(NbtOps.INSTANCE, nbt.get("Offers"))
				.resultOrPartial(Util.addPrefix("Failed to load offers: ", LOGGER::warn))
				.ifPresent(offers -> this.offers = offers);
		}

		this.readInventory(nbt);
	}

	@Nullable
	@Override
	public Entity moveToWorld(ServerWorld destination) {
		this.resetCustomer();
		return super.moveToWorld(destination);
	}

	protected void resetCustomer() {
		this.setCustomer(null);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		this.resetCustomer();
	}

	protected void produceParticles(ParticleEffect parameters) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.getWorld().addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return false;
	}

	@Override
	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		int i = mappedIndex - 300;
		return i >= 0 && i < this.inventory.size() ? StackReference.of(this.inventory, i) : super.getStackReference(mappedIndex);
	}

	protected abstract void fillRecipes();

	protected void fillRecipesFromPool(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count) {
		ArrayList<TradeOffers.Factory> arrayList = Lists.newArrayList(pool);
		int i = 0;

		while (i < count && !arrayList.isEmpty()) {
			TradeOffer tradeOffer = ((TradeOffers.Factory)arrayList.remove(this.random.nextInt(arrayList.size()))).create(this, this.random);
			if (tradeOffer != null) {
				recipeList.add(tradeOffer);
				i++;
			}
		}
	}

	@Override
	public Vec3d getLeashPos(float delta) {
		float f = MathHelper.lerp(delta, this.prevBodyYaw, this.bodyYaw) * (float) (Math.PI / 180.0);
		Vec3d vec3d = new Vec3d(0.0, this.getBoundingBox().getLengthY() - 1.0, 0.2);
		return this.getLerpedPos(delta).add(vec3d.rotateY(-f));
	}

	@Override
	public boolean isClient() {
		return this.getWorld().isClient;
	}
}
