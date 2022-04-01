package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_7317;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public abstract class MerchantEntity extends PassiveEntity implements InventoryOwner, Npc, Merchant {
	private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(MerchantEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<EntityType<?>>> field_38530 = DataTracker.registerData(MerchantEntity.class, TrackedDataHandlerRegistry.field_38682);
	public static final int field_30599 = 300;
	private static final int INVENTORY_SIZE = 8;
	@Nullable
	private PlayerEntity customer;
	@Nullable
	protected TradeOfferList offers;
	@Nullable
	private TradeOffer field_38531;
	@Nullable
	private Entity field_38532;
	private final SimpleInventory inventory = new SimpleInventory(8);

	public MerchantEntity(EntityType<? extends MerchantEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	public int getHeadRollingTimeLeft() {
		return this.dataTracker.get(HEAD_ROLLING_TIME_LEFT);
	}

	public void setHeadRollingTimeLeft(int ticks) {
		this.dataTracker.set(HEAD_ROLLING_TIME_LEFT, ticks);
	}

	public Optional<EntityType<?>> method_42832() {
		return this.dataTracker.get(field_38530);
	}

	public void method_42829(Optional<EntityType<?>> optional) {
		this.dataTracker.set(field_38530, optional);
	}

	@Nullable
	public Entity method_42833() {
		Optional<EntityType<?>> optional = this.method_42832();
		if (optional.isEmpty()) {
			return null;
		} else {
			Entity entity = this.field_38532;
			if (entity == null || entity.getType() != optional.get()) {
				this.field_38532 = entity = ((EntityType)optional.get()).create(this.world);
			}

			if (entity != null) {
				entity.age = this.age;
			}

			return entity;
		}
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
		this.dataTracker.startTracking(field_38530, Optional.empty());
	}

	public boolean method_42831(PlayerEntity playerEntity) {
		if (this.field_38531 != null) {
			class_7317 lv = playerEntity.method_42802();
			if (lv != null && this.field_38531.method_42853(lv)) {
				playerEntity.method_42801();
				class_7317 lv2 = this.field_38531.getSellItem();
				if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
					lv2.method_42841(serverPlayerEntity);
				}

				this.trade(this.field_38531);
				playerEntity.incrementStat(Stats.TRADED_WITH_VILLAGER);
				return true;
			}
		}

		return false;
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
			Criteria.VILLAGER_TRADE.trigger((ServerPlayerEntity)this.customer, this, offer.method_42856());
		}
	}

	protected abstract void afterUsing(TradeOffer offer);

	@Override
	public boolean isLeveledMerchant() {
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
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		TradeOfferList tradeOfferList = this.getOffers();
		if (!tradeOfferList.isEmpty()) {
			nbt.put("Offers", tradeOfferList.toNbt());
		}

		nbt.put("Inventory", this.inventory.toNbtList());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Offers", NbtElement.COMPOUND_TYPE)) {
			this.offers = new TradeOfferList(nbt.getCompound("Offers"));
		}

		this.inventory.readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
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
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		this.resetCustomer();
	}

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
	public StackReference getStackReference(int mappedIndex) {
		int i = mappedIndex - 300;
		return i >= 0 && i < this.inventory.size() ? StackReference.of(this.inventory, i) : super.getStackReference(mappedIndex);
	}

	protected abstract void fillRecipes();

	protected void fillRecipesFromPool(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count) {
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

	@Override
	public Vec3d getLeashPos(float delta) {
		float f = MathHelper.lerp(delta, this.prevBodyYaw, this.bodyYaw) * (float) (Math.PI / 180.0);
		Vec3d vec3d = new Vec3d(0.0, this.getBoundingBox().getYLength() - 1.0, 0.2);
		return this.getLerpedPos(delta).add(vec3d.rotateY(-f));
	}

	@Override
	public boolean isClient() {
		return this.world.isClient;
	}

	public void method_42830(@Nullable TradeOffer tradeOffer) {
		this.field_38531 = tradeOffer;
		class_7317 lv = tradeOffer != null ? tradeOffer.getSellItem() : null;
		if (lv instanceof class_7317.class_7318 lv2) {
			this.equipStack(EquipmentSlot.MAINHAND, lv2.method_42840());
			this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
		} else {
			this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085F);
		}

		if (lv instanceof class_7317.class_7319 lv3) {
			this.method_42829(Optional.of(lv3.entity()));
		} else {
			this.method_42829(Optional.empty());
		}
	}
}
