package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Hand;
import net.minecraft.util.Timestamp;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerGossips;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VillagerEntity extends AbstractTraderEntity implements InteractionObserver, VillagerDataContainer {
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
	private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of(
		Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS
	);
	private int levelUpTimer;
	private boolean levellingUp;
	@Nullable
	private PlayerEntity lastCustomer;
	private byte foodLevel;
	private final VillagerGossips gossip = new VillagerGossips();
	private long gossipStartTime;
	private long lastGossipDecayTime;
	private int experience;
	private long lastRestockTime;
	private int restocksToday;
	private long field_20332;
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.HOME,
		MemoryModuleType.JOB_SITE,
		MemoryModuleType.MEETING_POINT,
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.VISIBLE_VILLAGER_BABIES,
		MemoryModuleType.NEAREST_PLAYERS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.INTERACTION_TARGET,
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.PATH,
		MemoryModuleType.INTERACTABLE_DOORS,
		MemoryModuleType.OPENED_DOORS,
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
		MemoryModuleType.GOLEM_LAST_SEEN_TIME
	);
	private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES,
		SensorType.NEAREST_PLAYERS,
		SensorType.INTERACTABLE_DOORS,
		SensorType.NEAREST_BED,
		SensorType.HURT_BY,
		SensorType.VILLAGER_HOSTILES,
		SensorType.VILLAGER_BABIES,
		SensorType.SECONDARY_POIS,
		SensorType.GOLEM_LAST_SEEN
	);
	public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POINTS_OF_INTEREST = ImmutableMap.of(
		MemoryModuleType.HOME,
		(villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.HOME,
		MemoryModuleType.JOB_SITE,
		(villagerEntity, pointOfInterestType) -> villagerEntity.getVillagerData().getProfession().getWorkStation() == pointOfInterestType,
		MemoryModuleType.MEETING_POINT,
		(villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.MEETING
	);

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
		this(entityType, world, VillagerType.PLAINS);
	}

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type) {
		super(entityType, world);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.getNavigation().setCanSwim(true);
		this.setCanPickUpLoot(true);
		this.setVillagerData(this.getVillagerData().withType(type).withProfession(VillagerProfession.NONE));
		this.brain = this.deserializeBrain(new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()));
	}

	@Override
	public Brain<VillagerEntity> getBrain() {
		return (Brain<VillagerEntity>)super.getBrain();
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		Brain<VillagerEntity> brain = new Brain<>(MEMORY_MODULES, SENSORS, dynamic);
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
		float f = (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
		if (this.isBaby()) {
			brain.setSchedule(Schedule.VILLAGER_BABY);
			brain.setTaskList(Activity.PLAY, VillagerTaskListProvider.createPlayTasks(f));
		} else {
			brain.setSchedule(Schedule.VILLAGER_DEFAULT);
			brain.setTaskList(
				Activity.WORK,
				VillagerTaskListProvider.createWorkTasks(villagerProfession, f),
				ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT))
			);
		}

		brain.setTaskList(Activity.CORE, VillagerTaskListProvider.createCoreTasks(villagerProfession, f));
		brain.setTaskList(
			Activity.MEET,
			VillagerTaskListProvider.createMeetTasks(villagerProfession, f),
			ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT))
		);
		brain.setTaskList(Activity.REST, VillagerTaskListProvider.createRestTasks(villagerProfession, f));
		brain.setTaskList(Activity.IDLE, VillagerTaskListProvider.createIdleTasks(villagerProfession, f));
		brain.setTaskList(Activity.PANIC, VillagerTaskListProvider.createPanicTasks(villagerProfession, f));
		brain.setTaskList(Activity.PRE_RAID, VillagerTaskListProvider.createPreRaidTasks(villagerProfession, f));
		brain.setTaskList(Activity.RAID, VillagerTaskListProvider.createRaidTasks(villagerProfession, f));
		brain.setTaskList(Activity.HIDE, VillagerTaskListProvider.createHideTasks(villagerProfession, f));
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities(Activity.IDLE);
		brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
	}

	@Override
	protected void onGrowUp() {
		super.onGrowUp();
		if (this.world instanceof ServerWorld) {
			this.reinitializeBrain((ServerWorld)this.world);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(48.0);
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("brain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		if (!this.hasCustomer() && this.levelUpTimer > 0) {
			this.levelUpTimer--;
			if (this.levelUpTimer <= 0) {
				if (this.levellingUp) {
					this.levelUp();
					this.levellingUp = false;
				}

				this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
			}
		}

		if (this.lastCustomer != null && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
			this.world.sendEntityStatus(this, (byte)14);
			this.lastCustomer = null;
		}

		if (!this.isAiDisabled() && this.random.nextInt(100) == 0) {
			Raid raid = ((ServerWorld)this.world).getRaidAt(new BlockPos(this));
			if (raid != null && raid.isActive() && !raid.isFinished()) {
				this.world.sendEntityStatus(this, (byte)42);
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
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		boolean bl = itemStack.getItem() == Items.NAME_TAG;
		if (bl) {
			itemStack.useOnEntity(player, this, hand);
			return true;
		} else if (itemStack.getItem() == Items.VILLAGER_SPAWN_EGG || !this.isAlive() || this.hasCustomer() || this.isSleeping()) {
			return super.interactMob(player, hand);
		} else if (this.isBaby()) {
			this.sayNo();
			return super.interactMob(player, hand);
		} else {
			boolean bl2 = this.getOffers().isEmpty();
			if (hand == Hand.MAIN_HAND) {
				if (bl2 && !this.world.isClient) {
					this.sayNo();
				}

				player.incrementStat(Stats.TALKED_TO_VILLAGER);
			}

			if (bl2) {
				return super.interactMob(player, hand);
			} else {
				if (!this.world.isClient && !this.offers.isEmpty()) {
					this.beginTradeWith(player);
				}

				return true;
			}
		}
	}

	private void sayNo() {
		this.setHeadRollingTimeLeft(40);
		if (!this.world.isClient()) {
			this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	private void beginTradeWith(PlayerEntity customer) {
		this.prepareRecipesFor(customer);
		this.setCurrentCustomer(customer);
		this.sendOffers(customer, this.getDisplayName(), this.getVillagerData().getLevel());
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity customer) {
		boolean bl = this.getCurrentCustomer() != null && customer == null;
		super.setCurrentCustomer(customer);
		if (bl) {
			this.resetCustomer();
		}
	}

	@Override
	protected void resetCustomer() {
		super.resetCustomer();
		this.clearCurrentBonus();
	}

	private void clearCurrentBonus() {
		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.clearSpecialPrice();
		}
	}

	@Override
	public boolean canRefreshTrades() {
		return true;
	}

	public void restock() {
		this.method_21724();

		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.resetUses();
		}

		if (this.getVillagerData().getProfession() == VillagerProfession.FARMER) {
			this.craftBread();
		}

		this.lastRestockTime = this.world.getTime();
		this.restocksToday++;
	}

	private boolean needRestock() {
		for (TradeOffer tradeOffer : this.getOffers()) {
			if (tradeOffer.method_21834()) {
				return true;
			}
		}

		return false;
	}

	private boolean canRestock() {
		return this.restocksToday == 0 || this.restocksToday < 2 && this.world.getTime() > this.lastRestockTime + 2400L;
	}

	public boolean shouldRestock() {
		long l = this.lastRestockTime + 12000L;
		long m = this.world.getTime();
		boolean bl = m > l;
		long n = this.world.getTimeOfDay();
		if (this.field_20332 > 0L) {
			long o = this.field_20332 / 24000L;
			long p = n / 24000L;
			bl |= p > o;
		}

		this.field_20332 = n;
		if (bl) {
			this.lastRestockTime = m;
			this.clearDailyRestockCount();
		}

		return this.canRestock() && this.needRestock();
	}

	private void method_21723() {
		int i = 2 - this.restocksToday;
		if (i > 0) {
			for (TradeOffer tradeOffer : this.getOffers()) {
				tradeOffer.resetUses();
			}
		}

		for (int j = 0; j < i; j++) {
			this.method_21724();
		}
	}

	private void method_21724() {
		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.updatePriceOnDemand();
		}
	}

	private void prepareRecipesFor(PlayerEntity player) {
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
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.put("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
		tag.putByte("FoodLevel", this.foodLevel);
		tag.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE).getValue());
		tag.putInt("Xp", this.experience);
		tag.putLong("LastRestock", this.lastRestockTime);
		tag.putLong("LastGossipDecay", this.lastGossipDecayTime);
		tag.putInt("RestocksToday", this.restocksToday);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("VillagerData", 10)) {
			this.setVillagerData(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, tag.get("VillagerData"))));
		}

		if (tag.contains("Offers", 10)) {
			this.offers = new TraderOfferList(tag.getCompound("Offers"));
		}

		if (tag.contains("FoodLevel", 1)) {
			this.foodLevel = tag.getByte("FoodLevel");
		}

		ListTag listTag = tag.getList("Gossips", 10);
		this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, listTag));
		if (tag.contains("Xp", 3)) {
			this.experience = tag.getInt("Xp");
		}

		this.lastRestockTime = tag.getLong("LastRestock");
		this.lastGossipDecayTime = tag.getLong("LastGossipDecay");
		this.setCanPickUpLoot(true);
		this.reinitializeBrain((ServerWorld)this.world);
		this.restocksToday = tag.getInt("RestocksToday");
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
		SoundEvent soundEvent = this.getVillagerData().getProfession().method_22384();
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

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
		this.experience = this.experience + offer.getTraderExperience();
		this.lastCustomer = this.getCurrentCustomer();
		if (this.canLevelUp()) {
			this.levelUpTimer = 40;
			this.levellingUp = true;
			i += 5;
		}

		if (offer.shouldRewardPlayerExperience()) {
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), i));
		}
	}

	@Override
	public void setAttacker(@Nullable LivingEntity attacker) {
		if (attacker != null && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
			if (this.isAlive() && attacker instanceof PlayerEntity) {
				this.world.sendEntityStatus(this, (byte)13);
			}
		}

		super.setAttacker(attacker);
	}

	@Override
	public void onDeath(DamageSource source) {
		LOGGER.info("Villager {} died, message: '{}'", this, source.getDeathMessage(this).getString());
		Entity entity = source.getAttacker();
		if (entity != null) {
			this.notifyDeath(entity);
		}

		this.releaseTicketFor(MemoryModuleType.HOME);
		this.releaseTicketFor(MemoryModuleType.JOB_SITE);
		this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
		super.onDeath(source);
	}

	private void notifyDeath(Entity killer) {
		if (this.world instanceof ServerWorld) {
			Optional<List<LivingEntity>> optional = this.brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
			if (optional.isPresent()) {
				ServerWorld serverWorld = (ServerWorld)this.world;
				((List)optional.get())
					.stream()
					.filter(livingEntity -> livingEntity instanceof InteractionObserver)
					.forEach(livingEntity -> serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver)livingEntity));
			}
		}
	}

	public void releaseTicketFor(MemoryModuleType<GlobalPos> memoryModuleType) {
		if (this.world instanceof ServerWorld) {
			MinecraftServer minecraftServer = ((ServerWorld)this.world).getServer();
			this.brain.getOptionalMemory(memoryModuleType).ifPresent(globalPos -> {
				ServerWorld serverWorld = minecraftServer.getWorld(globalPos.getDimension());
				PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
				Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(globalPos.getPos());
				BiPredicate<VillagerEntity, PointOfInterestType> biPredicate = (BiPredicate<VillagerEntity, PointOfInterestType>)POINTS_OF_INTEREST.get(memoryModuleType);
				if (optional.isPresent() && biPredicate.test(this, optional.get())) {
					pointOfInterestStorage.releaseTicket(globalPos.getPos());
					DebugRendererInfoManager.sendPointOfInterest(serverWorld, globalPos.getPos());
				}
			});
		}
	}

	public boolean isReadyToBreed() {
		return this.foodLevel + this.getAvailableFood() >= 12 && this.getBreedingAge() == 0;
	}

	private boolean lacksFood() {
		return this.foodLevel < 12;
	}

	private void consumeAvailableFood() {
		if (this.lacksFood() && this.getAvailableFood() != 0) {
			for (int i = 0; i < this.getInventory().getInvSize(); i++) {
				ItemStack itemStack = this.getInventory().getInvStack(i);
				if (!itemStack.isEmpty()) {
					Integer integer = (Integer)ITEM_FOOD_VALUES.get(itemStack.getItem());
					if (integer != null) {
						int j = itemStack.getCount();

						for (int k = j; k > 0; k--) {
							this.foodLevel = (byte)(this.foodLevel + integer);
							this.getInventory().takeInvStack(i, 1);
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
		return this.gossip.getReputationFor(player.getUuid(), villageGossipType -> true);
	}

	private void depleteFood(int amount) {
		this.foodLevel = (byte)(this.foodLevel - amount);
	}

	public void eatForBreeding() {
		this.consumeAvailableFood();
		this.depleteFood(12);
	}

	public void setOffers(TraderOfferList offers) {
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
	protected Text method_23315() {
		return new TranslatableText(this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getId(this.getVillagerData().getProfession()).getPath());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 12) {
			this.produceParticles(ParticleTypes.HEART);
		} else if (status == 13) {
			this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
		} else if (status == 14) {
			this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
		} else if (status == 42) {
			this.produceParticles(ParticleTypes.SPLASH);
		} else {
			super.handleStatus(status);
		}
	}

	@Nullable
	@Override
	public net.minecraft.entity.EntityData initialize(
		IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable net.minecraft.entity.EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (spawnType == SpawnType.BREEDING) {
			this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
		}

		if (spawnType == SpawnType.COMMAND || spawnType == SpawnType.SPAWN_EGG || spawnType == SpawnType.SPAWNER || spawnType == SpawnType.DISPENSER) {
			this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(world.getBiome(new BlockPos(this)))));
		}

		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	public VillagerEntity createChild(PassiveEntity passiveEntity) {
		double d = this.random.nextDouble();
		VillagerType villagerType;
		if (d < 0.5) {
			villagerType = VillagerType.forBiome(this.world.getBiome(new BlockPos(this)));
		} else if (d < 0.75) {
			villagerType = this.getVillagerData().getType();
		} else {
			villagerType = ((VillagerEntity)passiveEntity).getVillagerData().getType();
		}

		VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, this.world, villagerType);
		villagerEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.BREEDING, null, null);
		return villagerEntity;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightning) {
		WitchEntity witchEntity = EntityType.WITCH.create(this.world);
		witchEntity.setPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		witchEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(witchEntity)), SpawnType.CONVERSION, null, null);
		witchEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			witchEntity.setCustomName(this.getCustomName());
			witchEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(witchEntity);
		this.remove();
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		Item item2 = itemStack.getItem();
		if (this.canGather(item2)) {
			BasicInventory basicInventory = this.getInventory();
			boolean bl = false;

			for (int i = 0; i < basicInventory.getInvSize(); i++) {
				ItemStack itemStack2 = basicInventory.getInvStack(i);
				if (itemStack2.isEmpty() || itemStack2.getItem() == item2 && itemStack2.getCount() < itemStack2.getMaxCount()) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return;
			}

			int ix = basicInventory.countInInv(item2);
			if (ix == 256) {
				return;
			}

			if (ix > 256) {
				basicInventory.poll(item2, ix - 256);
				return;
			}

			this.sendPickup(item, itemStack.getCount());
			ItemStack itemStack2 = basicInventory.add(itemStack);
			if (itemStack2.isEmpty()) {
				item.remove();
			} else {
				itemStack.setCount(itemStack2.getCount());
			}
		}
	}

	public boolean canGather(Item item) {
		return GATHERABLE_ITEMS.contains(item) || this.getVillagerData().getProfession().getGatherableItems().contains(item);
	}

	public boolean wantsToStartBreeding() {
		return this.getAvailableFood() >= 24;
	}

	public boolean canBreed() {
		return this.getAvailableFood() < 12;
	}

	private int getAvailableFood() {
		BasicInventory basicInventory = this.getInventory();
		return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(entry -> basicInventory.countInInv((Item)entry.getKey()) * (Integer)entry.getValue()).sum();
	}

	private void craftBread() {
		BasicInventory basicInventory = this.getInventory();
		int i = basicInventory.countInInv(Items.WHEAT);
		int j = i / 3;
		if (j != 0) {
			int k = j * 3;
			basicInventory.poll(Items.WHEAT, k);
			ItemStack itemStack = basicInventory.add(new ItemStack(Items.BREAD, j));
			if (!itemStack.isEmpty()) {
				this.dropStack(itemStack, 0.5F);
			}
		}
	}

	public boolean hasSeedToPlant() {
		BasicInventory basicInventory = this.getInventory();
		return basicInventory.containsAnyInInv(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
	}

	@Override
	protected void fillRecipes() {
		VillagerData villagerData = this.getVillagerData();
		Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap = (Int2ObjectMap<TradeOffers.Factory[]>)TradeOffers.PROFESSION_TO_LEVELED_TRADE
			.get(villagerData.getProfession());
		if (int2ObjectMap != null && !int2ObjectMap.isEmpty()) {
			TradeOffers.Factory[] factorys = int2ObjectMap.get(villagerData.getLevel());
			if (factorys != null) {
				TraderOfferList traderOfferList = this.getOffers();
				this.fillRecipesFromPool(traderOfferList, factorys, 2);
			}
		}
	}

	public void talkWithVillager(VillagerEntity villagerEntity, long time) {
		if ((time < this.gossipStartTime || time >= this.gossipStartTime + 1200L)
			&& (time < villagerEntity.gossipStartTime || time >= villagerEntity.gossipStartTime + 1200L)) {
			this.gossip.shareGossipFrom(villagerEntity.gossip, this.random, 10);
			this.gossipStartTime = time;
			villagerEntity.gossipStartTime = time;
			this.summonGolem(time, 5);
		}
	}

	private void decayGossip() {
		long l = this.world.getTime();
		if (this.lastGossipDecayTime == 0L) {
			this.lastGossipDecayTime = l;
		} else if (l >= this.lastGossipDecayTime + 24000L) {
			this.gossip.decay();
			this.lastGossipDecayTime = l;
		}
	}

	public void summonGolem(long time, int requiredVillagerCount) {
		if (this.canSummonGolem(time)) {
			Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
			List<VillagerEntity> list = this.world.getNonSpectatingEntities(VillagerEntity.class, box);
			List<VillagerEntity> list2 = (List<VillagerEntity>)list.stream()
				.filter(villagerEntity -> villagerEntity.canSummonGolem(time))
				.limit(5L)
				.collect(Collectors.toList());
			if (list2.size() >= requiredVillagerCount) {
				IronGolemEntity ironGolemEntity = this.spawnIronGolem();
				if (ironGolemEntity != null) {
					list.forEach(villagerEntity -> villagerEntity.setGolemLastSeenTime(time));
				}
			}
		}
	}

	private void setGolemLastSeenTime(long time) {
		this.brain.putMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, time);
	}

	private boolean hasSeenGolemRecently(long currentTime) {
		Optional<Long> optional = this.brain.getOptionalMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);
		if (!optional.isPresent()) {
			return false;
		} else {
			Long long_ = (Long)optional.get();
			return currentTime - long_ <= 600L;
		}
	}

	public boolean canSummonGolem(long time) {
		VillagerData villagerData = this.getVillagerData();
		if (villagerData.getProfession() == VillagerProfession.NONE || villagerData.getProfession() == VillagerProfession.NITWIT) {
			return false;
		} else {
			return !this.hasRecentlyWorkedAndSlept(this.world.getTime()) ? false : !this.hasSeenGolemRecently(time);
		}
	}

	@Nullable
	private IronGolemEntity spawnIronGolem() {
		BlockPos blockPos = new BlockPos(this);

		for (int i = 0; i < 10; i++) {
			double d = (double)(this.world.random.nextInt(16) - 8);
			double e = (double)(this.world.random.nextInt(16) - 8);
			double f = 6.0;

			for (int j = 0; j >= -12; j--) {
				BlockPos blockPos2 = blockPos.add(d, f + (double)j, e);
				if ((this.world.getBlockState(blockPos2).isAir() || this.world.getBlockState(blockPos2).getMaterial().isLiquid())
					&& this.world.getBlockState(blockPos2.down()).getMaterial().blocksLight()) {
					f += (double)j;
					break;
				}
			}

			BlockPos blockPos3 = blockPos.add(d, f, e);
			IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.create(this.world, null, null, null, blockPos3, SpawnType.MOB_SUMMONED, false, false);
			if (ironGolemEntity != null) {
				if (ironGolemEntity.canSpawn(this.world, SpawnType.MOB_SUMMONED) && ironGolemEntity.canSpawn(this.world)) {
					this.world.spawnEntity(ironGolemEntity);
					return ironGolemEntity;
				}

				ironGolemEntity.remove();
			}
		}

		return null;
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

	public void setExperience(int amount) {
		this.experience = amount;
	}

	private void clearDailyRestockCount() {
		this.method_21723();
		this.restocksToday = 0;
	}

	public VillagerGossips method_21651() {
		return this.gossip;
	}

	public void method_21650(Tag tag) {
		this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, tag));
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugRendererInfoManager.sendVillagerAiDebugData(this);
	}

	@Override
	public void sleep(BlockPos pos) {
		super.sleep(pos);
		this.brain.putMemory(MemoryModuleType.LAST_SLEPT, Timestamp.of(this.world.getTime()));
	}

	@Override
	public void wakeUp() {
		super.wakeUp();
		this.brain.putMemory(MemoryModuleType.LAST_WOKEN, Timestamp.of(this.world.getTime()));
	}

	private boolean hasRecentlyWorkedAndSlept(long worldTime) {
		Optional<Timestamp> optional = this.brain.getOptionalMemory(MemoryModuleType.LAST_SLEPT);
		Optional<Timestamp> optional2 = this.brain.getOptionalMemory(MemoryModuleType.LAST_WORKED_AT_POI);
		return optional.isPresent() && optional2.isPresent()
			? worldTime - ((Timestamp)optional.get()).getTime() < 24000L && worldTime - ((Timestamp)optional2.get()).getTime() < 36000L
			: false;
	}
}
