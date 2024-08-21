package net.minecraft.entity.passive;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerGossips;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.slf4j.Logger;

public class VillagerEntity extends MerchantEntity implements InteractionObserver, VillagerDataContainer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	public static final int field_30602 = 12;
	public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
	private static final int field_30604 = 2;
	private static final int field_30605 = 10;
	private static final int field_30606 = 1200;
	private static final int field_30607 = 24000;
	private static final int field_30608 = 25;
	private static final int field_30609 = 10;
	private static final int field_30610 = 5;
	private static final long field_30611 = 24000L;
	@VisibleForTesting
	public static final float field_30603 = 0.5F;
	private int levelUpTimer;
	private boolean levelingUp;
	@Nullable
	private PlayerEntity lastCustomer;
	private boolean field_30612;
	private int foodLevel;
	private final VillagerGossips gossip = new VillagerGossips();
	private long gossipStartTime;
	private long lastGossipDecayTime;
	private int experience;
	private long lastRestockTime;
	private int restocksToday;
	private long lastRestockCheckTime;
	private boolean natural;
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.HOME,
		MemoryModuleType.JOB_SITE,
		MemoryModuleType.POTENTIAL_JOB_SITE,
		MemoryModuleType.MEETING_POINT,
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.VISIBLE_VILLAGER_BABIES,
		MemoryModuleType.NEAREST_PLAYERS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
		MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.INTERACTION_TARGET,
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.PATH,
		MemoryModuleType.DOORS_TO_CLOSE,
		MemoryModuleType.NEAREST_BED,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.HURT_BY_ENTITY,
		MemoryModuleType.NEAREST_HOSTILE,
		MemoryModuleType.SECONDARY_JOB_SITE,
		MemoryModuleType.HIDING_PLACE,
		MemoryModuleType.HEARD_BELL_TIME,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.LAST_SLEPT,
		MemoryModuleType.LAST_WOKEN,
		MemoryModuleType.LAST_WORKED_AT_POI,
		MemoryModuleType.GOLEM_DETECTED_RECENTLY
	);
	private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES,
		SensorType.NEAREST_PLAYERS,
		SensorType.NEAREST_ITEMS,
		SensorType.NEAREST_BED,
		SensorType.HURT_BY,
		SensorType.VILLAGER_HOSTILES,
		SensorType.VILLAGER_BABIES,
		SensorType.SECONDARY_POIS,
		SensorType.GOLEM_DETECTED
	);
	public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>>> POINTS_OF_INTEREST = ImmutableMap.of(
		MemoryModuleType.HOME,
		(villager, registryEntry) -> registryEntry.matchesKey(PointOfInterestTypes.HOME),
		MemoryModuleType.JOB_SITE,
		(villager, registryEntry) -> villager.getVillagerData().getProfession().heldWorkstation().test(registryEntry),
		MemoryModuleType.POTENTIAL_JOB_SITE,
		(villager, registryEntry) -> VillagerProfession.IS_ACQUIRABLE_JOB_SITE.test(registryEntry),
		MemoryModuleType.MEETING_POINT,
		(villager, registryEntry) -> registryEntry.matchesKey(PointOfInterestTypes.MEETING)
	);

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
		this(entityType, world, VillagerType.PLAINS);
	}

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type) {
		super(entityType, world);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.getNavigation().setCanSwim(true);
		this.getNavigation().setMaxFollowRange(48.0F);
		this.setCanPickUpLoot(true);
		this.setVillagerData(this.getVillagerData().withType(type).withProfession(VillagerProfession.NONE));
	}

	@Override
	public Brain<VillagerEntity> getBrain() {
		return (Brain<VillagerEntity>)super.getBrain();
	}

	@Override
	protected Brain.Profile<VillagerEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		Brain<VillagerEntity> brain = this.createBrainProfile().deserialize(dynamic);
		this.initBrain(brain);
		return brain;
	}

	public void reinitializeBrain(ServerWorld world) {
		Brain<VillagerEntity> brain = this.getBrain();
		brain.stopAllTasks(world, this);
		this.brain = brain.copy();
		this.initBrain(this.getBrain());
	}

	private void initBrain(Brain<VillagerEntity> brain) {
		VillagerProfession villagerProfession = this.getVillagerData().getProfession();
		if (this.isBaby()) {
			brain.setSchedule(Schedule.VILLAGER_BABY);
			brain.setTaskList(Activity.PLAY, VillagerTaskListProvider.createPlayTasks(0.5F));
		} else {
			brain.setSchedule(Schedule.VILLAGER_DEFAULT);
			brain.setTaskList(
				Activity.WORK,
				VillagerTaskListProvider.createWorkTasks(villagerProfession, 0.5F),
				ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT))
			);
		}

		brain.setTaskList(Activity.CORE, VillagerTaskListProvider.createCoreTasks(villagerProfession, 0.5F));
		brain.setTaskList(
			Activity.MEET,
			VillagerTaskListProvider.createMeetTasks(villagerProfession, 0.5F),
			ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT))
		);
		brain.setTaskList(Activity.REST, VillagerTaskListProvider.createRestTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.IDLE, VillagerTaskListProvider.createIdleTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.PANIC, VillagerTaskListProvider.createPanicTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.PRE_RAID, VillagerTaskListProvider.createPreRaidTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.RAID, VillagerTaskListProvider.createRaidTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.HIDE, VillagerTaskListProvider.createHideTasks(villagerProfession, 0.5F));
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.doExclusively(Activity.IDLE);
		brain.refreshActivities(this.getWorld().getTimeOfDay(), this.getWorld().getTime());
	}

	@Override
	protected void onGrowUp() {
		super.onGrowUp();
		if (this.getWorld() instanceof ServerWorld) {
			this.reinitializeBrain((ServerWorld)this.getWorld());
		}
	}

	public static DefaultAttributeContainer.Builder createVillagerAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.5);
	}

	public boolean isNatural() {
		return this.natural;
	}

	@Override
	protected void mobTick() {
		this.getWorld().getProfiler().push("villagerBrain");
		this.getBrain().tick((ServerWorld)this.getWorld(), this);
		this.getWorld().getProfiler().pop();
		if (this.natural) {
			this.natural = false;
		}

		if (!this.hasCustomer() && this.levelUpTimer > 0) {
			this.levelUpTimer--;
			if (this.levelUpTimer <= 0) {
				if (this.levelingUp) {
					this.levelUp();
					this.levelingUp = false;
				}

				this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
			}
		}

		if (this.lastCustomer != null && this.getWorld() instanceof ServerWorld) {
			((ServerWorld)this.getWorld()).handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
			this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
			this.lastCustomer = null;
		}

		if (!this.isAiDisabled() && this.random.nextInt(100) == 0) {
			Raid raid = ((ServerWorld)this.getWorld()).getRaidAt(this.getBlockPos());
			if (raid != null && raid.isActive() && !raid.isFinished()) {
				this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_SPLASH_PARTICLES);
			}
		}

		if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.hasCustomer()) {
			this.resetCustomer();
		}

		super.mobTick();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getHeadRollingTimeLeft() > 0) {
			this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
		}

		this.decayGossip();
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.VILLAGER_SPAWN_EGG) || !this.isAlive() || this.hasCustomer() || this.isSleeping()) {
			return super.interactMob(player, hand);
		} else if (this.isBaby()) {
			this.sayNo();
			return ActionResult.SUCCESS;
		} else {
			if (!this.getWorld().isClient) {
				boolean bl = this.getOffers().isEmpty();
				if (hand == Hand.MAIN_HAND) {
					if (bl) {
						this.sayNo();
					}

					player.incrementStat(Stats.TALKED_TO_VILLAGER);
				}

				if (bl) {
					return ActionResult.CONSUME;
				}

				this.beginTradeWith(player);
			}

			return ActionResult.SUCCESS;
		}
	}

	private void sayNo() {
		this.setHeadRollingTimeLeft(40);
		if (!this.getWorld().isClient()) {
			this.playSound(SoundEvents.ENTITY_VILLAGER_NO);
		}
	}

	private void beginTradeWith(PlayerEntity customer) {
		this.prepareOffersFor(customer);
		this.setCustomer(customer);
		this.sendOffers(customer, this.getDisplayName(), this.getVillagerData().getLevel());
	}

	@Override
	public void setCustomer(@Nullable PlayerEntity customer) {
		boolean bl = this.getCustomer() != null && customer == null;
		super.setCustomer(customer);
		if (bl) {
			this.resetCustomer();
		}
	}

	@Override
	protected void resetCustomer() {
		super.resetCustomer();
		this.clearSpecialPrices();
	}

	/**
	 * Resets the special price of all the trade offers of this villager.
	 */
	private void clearSpecialPrices() {
		if (!this.getWorld().isClient()) {
			for (TradeOffer tradeOffer : this.getOffers()) {
				tradeOffer.clearSpecialPrice();
			}
		}
	}

	@Override
	public boolean canRefreshTrades() {
		return true;
	}

	@Override
	public boolean isClient() {
		return this.getWorld().isClient;
	}

	public void restock() {
		this.updateDemandBonus();

		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.resetUses();
		}

		this.sendOffersToCustomer();
		this.lastRestockTime = this.getWorld().getTime();
		this.restocksToday++;
	}

	private void sendOffersToCustomer() {
		TradeOfferList tradeOfferList = this.getOffers();
		PlayerEntity playerEntity = this.getCustomer();
		if (playerEntity != null && !tradeOfferList.isEmpty()) {
			playerEntity.sendTradeOffers(
				playerEntity.currentScreenHandler.syncId,
				tradeOfferList,
				this.getVillagerData().getLevel(),
				this.getExperience(),
				this.isLeveledMerchant(),
				this.canRefreshTrades()
			);
		}
	}

	/**
	 * Returns whether this villager needs restock.
	 * 
	 * <p>Checks if at least one of its trade offers has been used.
	 */
	private boolean needsRestock() {
		for (TradeOffer tradeOffer : this.getOffers()) {
			if (tradeOffer.hasBeenUsed()) {
				return true;
			}
		}

		return false;
	}

	private boolean canRestock() {
		return this.restocksToday == 0 || this.restocksToday < 2 && this.getWorld().getTime() > this.lastRestockTime + 2400L;
	}

	public boolean shouldRestock() {
		long l = this.lastRestockTime + 12000L;
		long m = this.getWorld().getTime();
		boolean bl = m > l;
		long n = this.getWorld().getTimeOfDay();
		if (this.lastRestockCheckTime > 0L) {
			long o = this.lastRestockCheckTime / 24000L;
			long p = n / 24000L;
			bl |= p > o;
		}

		this.lastRestockCheckTime = n;
		if (bl) {
			this.lastRestockTime = m;
			this.clearDailyRestockCount();
		}

		return this.canRestock() && this.needsRestock();
	}

	private void restockAndUpdateDemandBonus() {
		int i = 2 - this.restocksToday;
		if (i > 0) {
			for (TradeOffer tradeOffer : this.getOffers()) {
				tradeOffer.resetUses();
			}
		}

		for (int j = 0; j < i; j++) {
			this.updateDemandBonus();
		}

		this.sendOffersToCustomer();
	}

	/**
	 * Updates the demand bonus of all the trade offers of this villager.
	 */
	private void updateDemandBonus() {
		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.updateDemandBonus();
		}
	}

	private void prepareOffersFor(PlayerEntity player) {
		int i = this.getReputation(player);
		if (i != 0) {
			for (TradeOffer tradeOffer : this.getOffers()) {
				tradeOffer.increaseSpecialPrice(-MathHelper.floor((float)i * tradeOffer.getPriceMultiplier()));
			}
		}

		if (player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
			StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
			int j = statusEffectInstance.getAmplifier();

			for (TradeOffer tradeOffer2 : this.getOffers()) {
				double d = 0.3 + 0.0625 * (double)j;
				int k = (int)Math.floor(d * (double)tradeOffer2.getOriginalFirstBuyItem().getCount());
				tradeOffer2.increaseSpecialPrice(-Math.max(k, 1));
			}
		}
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		VillagerData.CODEC
			.encodeStart(NbtOps.INSTANCE, this.getVillagerData())
			.resultOrPartial(LOGGER::error)
			.ifPresent(nbtElement -> nbt.put("VillagerData", nbtElement));
		nbt.putByte("FoodLevel", (byte)this.foodLevel);
		nbt.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE));
		nbt.putInt("Xp", this.experience);
		nbt.putLong("LastRestock", this.lastRestockTime);
		nbt.putLong("LastGossipDecay", this.lastGossipDecayTime);
		nbt.putInt("RestocksToday", this.restocksToday);
		if (this.natural) {
			nbt.putBoolean("AssignProfessionWhenSpawned", true);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("VillagerData", NbtElement.COMPOUND_TYPE)) {
			VillagerData.CODEC
				.parse(NbtOps.INSTANCE, nbt.get("VillagerData"))
				.resultOrPartial(LOGGER::error)
				.ifPresent(villagerData -> this.dataTracker.set(VILLAGER_DATA, villagerData));
		}

		if (nbt.contains("FoodLevel", NbtElement.BYTE_TYPE)) {
			this.foodLevel = nbt.getByte("FoodLevel");
		}

		NbtList nbtList = nbt.getList("Gossips", NbtElement.COMPOUND_TYPE);
		this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, nbtList));
		if (nbt.contains("Xp", NbtElement.INT_TYPE)) {
			this.experience = nbt.getInt("Xp");
		}

		this.lastRestockTime = nbt.getLong("LastRestock");
		this.lastGossipDecayTime = nbt.getLong("LastGossipDecay");
		if (this.getWorld() instanceof ServerWorld) {
			this.reinitializeBrain((ServerWorld)this.getWorld());
		}

		this.restocksToday = nbt.getInt("RestocksToday");
		if (nbt.contains("AssignProfessionWhenSpawned")) {
			this.natural = nbt.getBoolean("AssignProfessionWhenSpawned");
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isSleeping()) {
			return null;
		} else {
			return this.hasCustomer() ? SoundEvents.ENTITY_VILLAGER_TRADE : SoundEvents.ENTITY_VILLAGER_AMBIENT;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_VILLAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VILLAGER_DEATH;
	}

	public void playWorkSound() {
		this.playSound(this.getVillagerData().getProfession().workSound());
	}

	@Override
	public void setVillagerData(VillagerData villagerData) {
		VillagerData villagerData2 = this.getVillagerData();
		if (villagerData2.getProfession() != villagerData.getProfession()) {
			this.offers = null;
		}

		this.dataTracker.set(VILLAGER_DATA, villagerData);
	}

	@Override
	public VillagerData getVillagerData() {
		return this.dataTracker.get(VILLAGER_DATA);
	}

	@Override
	protected void afterUsing(TradeOffer offer) {
		int i = 3 + this.random.nextInt(4);
		this.experience = this.experience + offer.getMerchantExperience();
		this.lastCustomer = this.getCustomer();
		if (this.canLevelUp()) {
			this.levelUpTimer = 40;
			this.levelingUp = true;
			i += 5;
		}

		if (offer.shouldRewardPlayerExperience()) {
			this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ(), i));
		}
	}

	public void method_35201(boolean bl) {
		this.field_30612 = bl;
	}

	public boolean method_35200() {
		return this.field_30612;
	}

	@Override
	public void setAttacker(@Nullable LivingEntity attacker) {
		if (attacker != null && this.getWorld() instanceof ServerWorld) {
			((ServerWorld)this.getWorld()).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
			if (this.isAlive() && attacker instanceof PlayerEntity) {
				this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
			}
		}

		super.setAttacker(attacker);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		LOGGER.info("Villager {} died, message: '{}'", this, damageSource.getDeathMessage(this).getString());
		Entity entity = damageSource.getAttacker();
		if (entity != null) {
			this.notifyDeath(entity);
		}

		this.releaseAllTickets();
		super.onDeath(damageSource);
	}

	private void releaseAllTickets() {
		this.releaseTicketFor(MemoryModuleType.HOME);
		this.releaseTicketFor(MemoryModuleType.JOB_SITE);
		this.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
		this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
	}

	private void notifyDeath(Entity killer) {
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			Optional<LivingTargetCache> optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
			if (!optional.isEmpty()) {
				((LivingTargetCache)optional.get())
					.iterate(InteractionObserver.class::isInstance)
					.forEach(observer -> serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver)observer));
			}
		}
	}

	public void releaseTicketFor(MemoryModuleType<GlobalPos> pos) {
		if (this.getWorld() instanceof ServerWorld) {
			MinecraftServer minecraftServer = ((ServerWorld)this.getWorld()).getServer();
			this.brain
				.getOptionalRegisteredMemory(pos)
				.ifPresent(
					posx -> {
						ServerWorld serverWorld = minecraftServer.getWorld(posx.dimension());
						if (serverWorld != null) {
							PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
							Optional<RegistryEntry<PointOfInterestType>> optional = pointOfInterestStorage.getType(posx.pos());
							BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>> biPredicate = (BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>>)POINTS_OF_INTEREST.get(
								pos
							);
							if (optional.isPresent() && biPredicate.test(this, (RegistryEntry)optional.get())) {
								pointOfInterestStorage.releaseTicket(posx.pos());
								DebugInfoSender.sendPointOfInterest(serverWorld, posx.pos());
							}
						}
					}
				);
		}
	}

	@Override
	public boolean isReadyToBreed() {
		return this.foodLevel + this.getAvailableFood() >= 12 && !this.isSleeping() && this.getBreedingAge() == 0;
	}

	private boolean lacksFood() {
		return this.foodLevel < 12;
	}

	private void consumeAvailableFood() {
		if (this.lacksFood() && this.getAvailableFood() != 0) {
			for (int i = 0; i < this.getInventory().size(); i++) {
				ItemStack itemStack = this.getInventory().getStack(i);
				if (!itemStack.isEmpty()) {
					Integer integer = (Integer)ITEM_FOOD_VALUES.get(itemStack.getItem());
					if (integer != null) {
						int j = itemStack.getCount();

						for (int k = j; k > 0; k--) {
							this.foodLevel = this.foodLevel + integer;
							this.getInventory().removeStack(i, 1);
							if (!this.lacksFood()) {
								return;
							}
						}
					}
				}
			}
		}
	}

	public int getReputation(PlayerEntity player) {
		return this.gossip.getReputationFor(player.getUuid(), gossipType -> true);
	}

	private void depleteFood(int amount) {
		this.foodLevel -= amount;
	}

	public void eatForBreeding() {
		this.consumeAvailableFood();
		this.depleteFood(12);
	}

	public void setOffers(TradeOfferList offers) {
		this.offers = offers;
	}

	private boolean canLevelUp() {
		int i = this.getVillagerData().getLevel();
		return VillagerData.canLevelUp(i) && this.experience >= VillagerData.getUpperLevelExperience(i);
	}

	private void levelUp() {
		this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
		this.fillRecipes();
	}

	@Override
	protected Text getDefaultName() {
		return Text.translatable(this.getType().getTranslationKey() + "." + Registries.VILLAGER_PROFESSION.getId(this.getVillagerData().getProfession()).getPath());
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_VILLAGER_HEART_PARTICLES) {
			this.produceParticles(ParticleTypes.HEART);
		} else if (status == EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES) {
			this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
		} else if (status == EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES) {
			this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
		} else if (status == EntityStatuses.ADD_SPLASH_PARTICLES) {
			this.produceParticles(ParticleTypes.SPLASH);
		} else {
			super.handleStatus(status);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		if (spawnReason == SpawnReason.BREEDING) {
			this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
		}

		if (spawnReason == SpawnReason.COMMAND
			|| spawnReason == SpawnReason.SPAWN_EGG
			|| SpawnReason.isAnySpawner(spawnReason)
			|| spawnReason == SpawnReason.DISPENSER) {
			this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(world.getBiome(this.getBlockPos()))));
		}

		if (spawnReason == SpawnReason.STRUCTURE) {
			this.natural = true;
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Nullable
	public VillagerEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		double d = this.random.nextDouble();
		VillagerType villagerType;
		if (d < 0.5) {
			villagerType = VillagerType.forBiome(serverWorld.getBiome(this.getBlockPos()));
		} else if (d < 0.75) {
			villagerType = this.getVillagerData().getType();
		} else {
			villagerType = ((VillagerEntity)passiveEntity).getVillagerData().getType();
		}

		VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, serverWorld, villagerType);
		villagerEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.BREEDING, null);
		return villagerEntity;
	}

	@Override
	public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
		if (world.getDifficulty() != Difficulty.PEACEFUL) {
			LOGGER.info("Villager {} was struck by lightning {}.", this, lightning);
			WitchEntity witchEntity = EntityType.WITCH.create(world, SpawnReason.CONVERSION);
			if (witchEntity != null) {
				witchEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
				witchEntity.initialize(world, world.getLocalDifficulty(witchEntity.getBlockPos()), SpawnReason.CONVERSION, null);
				witchEntity.setAiDisabled(this.isAiDisabled());
				if (this.hasCustomName()) {
					witchEntity.setCustomName(this.getCustomName());
					witchEntity.setCustomNameVisible(this.isCustomNameVisible());
				}

				witchEntity.setPersistent();
				world.spawnEntityAndPassengers(witchEntity);
				this.releaseAllTickets();
				this.discard();
			} else {
				super.onStruckByLightning(world, lightning);
			}
		} else {
			super.onStruckByLightning(world, lightning);
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		InventoryOwner.pickUpItem(this, this, item);
	}

	@Override
	public boolean canGather(ItemStack stack) {
		Item item = stack.getItem();
		return (stack.isIn(ItemTags.VILLAGER_PICKS_UP) || this.getVillagerData().getProfession().gatherableItems().contains(item))
			&& this.getInventory().canInsert(stack);
	}

	public boolean wantsToStartBreeding() {
		return this.getAvailableFood() >= 24;
	}

	public boolean canBreed() {
		return this.getAvailableFood() < 12;
	}

	private int getAvailableFood() {
		SimpleInventory simpleInventory = this.getInventory();
		return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(item -> simpleInventory.count((Item)item.getKey()) * (Integer)item.getValue()).sum();
	}

	public boolean hasSeedToPlant() {
		return this.getInventory().containsAny(stack -> stack.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS));
	}

	@Override
	protected void fillRecipes() {
		VillagerData villagerData = this.getVillagerData();
		Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap2;
		if (this.getWorld().getEnabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
			Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap = (Int2ObjectMap<TradeOffers.Factory[]>)TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE
				.get(villagerData.getProfession());
			int2ObjectMap2 = int2ObjectMap != null ? int2ObjectMap : (Int2ObjectMap)TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(villagerData.getProfession());
		} else {
			int2ObjectMap2 = (Int2ObjectMap<TradeOffers.Factory[]>)TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(villagerData.getProfession());
		}

		if (int2ObjectMap2 != null && !int2ObjectMap2.isEmpty()) {
			TradeOffers.Factory[] factorys = int2ObjectMap2.get(villagerData.getLevel());
			if (factorys != null) {
				TradeOfferList tradeOfferList = this.getOffers();
				this.fillRecipesFromPool(tradeOfferList, factorys, 2);
			}
		}
	}

	public void talkWithVillager(ServerWorld world, VillagerEntity villager, long time) {
		if ((time < this.gossipStartTime || time >= this.gossipStartTime + 1200L) && (time < villager.gossipStartTime || time >= villager.gossipStartTime + 1200L)) {
			this.gossip.shareGossipFrom(villager.gossip, this.random, 10);
			this.gossipStartTime = time;
			villager.gossipStartTime = time;
			this.summonGolem(world, time, 5);
		}
	}

	private void decayGossip() {
		long l = this.getWorld().getTime();
		if (this.lastGossipDecayTime == 0L) {
			this.lastGossipDecayTime = l;
		} else if (l >= this.lastGossipDecayTime + 24000L) {
			this.gossip.decay();
			this.lastGossipDecayTime = l;
		}
	}

	public void summonGolem(ServerWorld world, long time, int requiredCount) {
		if (this.canSummonGolem(time)) {
			Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
			List<VillagerEntity> list = world.getNonSpectatingEntities(VillagerEntity.class, box);
			List<VillagerEntity> list2 = (List<VillagerEntity>)list.stream().filter(villager -> villager.canSummonGolem(time)).limit(5L).collect(Collectors.toList());
			if (list2.size() >= requiredCount) {
				if (!LargeEntitySpawnHelper.trySpawnAt(
						EntityType.IRON_GOLEM, SpawnReason.MOB_SUMMONED, world, this.getBlockPos(), 10, 8, 6, LargeEntitySpawnHelper.Requirements.IRON_GOLEM
					)
					.isEmpty()) {
					list.forEach(GolemLastSeenSensor::rememberIronGolem);
				}
			}
		}
	}

	public boolean canSummonGolem(long time) {
		return !this.hasRecentlySlept(this.getWorld().getTime()) ? false : !this.brain.hasMemoryModule(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
	}

	@Override
	public void onInteractionWith(EntityInteraction interaction, Entity entity) {
		if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_POSITIVE, 20);
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_POSITIVE, 25);
		} else if (interaction == EntityInteraction.TRADE) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.TRADING, 2);
		} else if (interaction == EntityInteraction.VILLAGER_HURT) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_NEGATIVE, 25);
		} else if (interaction == EntityInteraction.VILLAGER_KILLED) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 25);
		}
	}

	@Override
	public int getExperience() {
		return this.experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	private void clearDailyRestockCount() {
		this.restockAndUpdateDemandBonus();
		this.restocksToday = 0;
	}

	public VillagerGossips getGossip() {
		return this.gossip;
	}

	public void readGossipDataNbt(NbtElement nbt) {
		this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, nbt));
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public void sleep(BlockPos pos) {
		super.sleep(pos);
		this.brain.remember(MemoryModuleType.LAST_SLEPT, this.getWorld().getTime());
		this.brain.forget(MemoryModuleType.WALK_TARGET);
		this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
	}

	@Override
	public void wakeUp() {
		super.wakeUp();
		this.brain.remember(MemoryModuleType.LAST_WOKEN, this.getWorld().getTime());
	}

	private boolean hasRecentlySlept(long worldTime) {
		Optional<Long> optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.LAST_SLEPT);
		return optional.isPresent() ? worldTime - (Long)optional.get() < 24000L : false;
	}
}
