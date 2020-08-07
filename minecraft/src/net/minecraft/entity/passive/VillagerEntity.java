package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
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

public class VillagerEntity extends AbstractTraderEntity implements InteractionObserver, VillagerDataContainer {
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.field_8229, 4, Items.field_8567, 1, Items.field_8179, 1, Items.field_8186, 1);
	private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of(
		Items.field_8229, Items.field_8567, Items.field_8179, Items.field_8861, Items.field_8317, Items.field_8186, Items.field_8309
	);
	private int levelUpTimer;
	private boolean levelingUp;
	@Nullable
	private PlayerEntity lastCustomer;
	private byte foodLevel;
	private final VillagerGossips gossip = new VillagerGossips();
	private long gossipStartTime;
	private long lastGossipDecayTime;
	private int experience;
	private long lastRestockTime;
	private int restocksToday;
	private long lastRestockCheckTime;
	private boolean natural;
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.field_18438,
		MemoryModuleType.field_18439,
		MemoryModuleType.field_25160,
		MemoryModuleType.field_18440,
		MemoryModuleType.field_18441,
		MemoryModuleType.field_18442,
		MemoryModuleType.field_19006,
		MemoryModuleType.field_18443,
		MemoryModuleType.field_18444,
		MemoryModuleType.field_22354,
		MemoryModuleType.field_22332,
		MemoryModuleType.field_18445,
		MemoryModuleType.field_18446,
		MemoryModuleType.field_18447,
		MemoryModuleType.field_18448,
		MemoryModuleType.field_18449,
		MemoryModuleType.field_26389,
		MemoryModuleType.field_19007,
		MemoryModuleType.field_18451,
		MemoryModuleType.field_18452,
		MemoryModuleType.field_18453,
		MemoryModuleType.field_18873,
		MemoryModuleType.field_19008,
		MemoryModuleType.field_19009,
		MemoryModuleType.field_19293,
		MemoryModuleType.field_19385,
		MemoryModuleType.field_20616,
		MemoryModuleType.field_19386,
		MemoryModuleType.field_25754
	);
	private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(
		SensorType.field_18466,
		SensorType.field_18467,
		SensorType.field_22358,
		SensorType.field_19010,
		SensorType.field_18469,
		SensorType.field_18470,
		SensorType.field_19011,
		SensorType.field_18875,
		SensorType.field_25756
	);
	public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POINTS_OF_INTEREST = ImmutableMap.of(
		MemoryModuleType.field_18438,
		(villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.field_18517,
		MemoryModuleType.field_18439,
		(villagerEntity, pointOfInterestType) -> villagerEntity.getVillagerData().getProfession().getWorkStation() == pointOfInterestType,
		MemoryModuleType.field_25160,
		(villagerEntity, pointOfInterestType) -> PointOfInterestType.IS_USED_BY_PROFESSION.test(pointOfInterestType),
		MemoryModuleType.field_18440,
		(villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.field_18518
	);

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
		this(entityType, world, VillagerType.PLAINS);
	}

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type) {
		super(entityType, world);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.getNavigation().setCanSwim(true);
		this.setCanPickUpLoot(true);
		this.setVillagerData(this.getVillagerData().withType(type).withProfession(VillagerProfession.field_17051));
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
			brain.setTaskList(Activity.field_18885, VillagerTaskListProvider.createPlayTasks(0.5F));
		} else {
			brain.setSchedule(Schedule.VILLAGER_DEFAULT);
			brain.setTaskList(
				Activity.field_18596,
				VillagerTaskListProvider.createWorkTasks(villagerProfession, 0.5F),
				ImmutableSet.of(Pair.of(MemoryModuleType.field_18439, MemoryModuleState.field_18456))
			);
		}

		brain.setTaskList(Activity.field_18594, VillagerTaskListProvider.createCoreTasks(villagerProfession, 0.5F));
		brain.setTaskList(
			Activity.field_18598,
			VillagerTaskListProvider.createMeetTasks(villagerProfession, 0.5F),
			ImmutableSet.of(Pair.of(MemoryModuleType.field_18440, MemoryModuleState.field_18456))
		);
		brain.setTaskList(Activity.field_18597, VillagerTaskListProvider.createRestTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.field_18595, VillagerTaskListProvider.createIdleTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.field_18599, VillagerTaskListProvider.createPanicTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.field_19042, VillagerTaskListProvider.createPreRaidTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.field_19041, VillagerTaskListProvider.createRaidTasks(villagerProfession, 0.5F));
		brain.setTaskList(Activity.field_19043, VillagerTaskListProvider.createHideTasks(villagerProfession, 0.5F));
		brain.setCoreActivities(ImmutableSet.of(Activity.field_18594));
		brain.setDefaultActivity(Activity.field_18595);
		brain.doExclusively(Activity.field_18595);
		brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
	}

	@Override
	protected void onGrowUp() {
		super.onGrowUp();
		if (this.world instanceof ServerWorld) {
			this.reinitializeBrain((ServerWorld)this.world);
		}
	}

	public static DefaultAttributeContainer.Builder createVillagerAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.field_23719, 0.5).add(EntityAttributes.field_23717, 48.0);
	}

	public boolean isNatural() {
		return this.natural;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("villagerBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
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

				this.addStatusEffect(new StatusEffectInstance(StatusEffects.field_5924, 200, 0));
			}
		}

		if (this.lastCustomer != null && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).handleInteraction(EntityInteraction.field_18478, this.lastCustomer, this);
			this.world.sendEntityStatus(this, (byte)14);
			this.lastCustomer = null;
		}

		if (!this.isAiDisabled() && this.random.nextInt(100) == 0) {
			Raid raid = ((ServerWorld)this.world).getRaidAt(this.getBlockPos());
			if (raid != null && raid.isActive() && !raid.isFinished()) {
				this.world.sendEntityStatus(this, (byte)42);
			}
		}

		if (this.getVillagerData().getProfession() == VillagerProfession.field_17051 && this.hasCustomer()) {
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
		if (itemStack.getItem() == Items.field_8086 || !this.isAlive() || this.hasCustomer() || this.isSleeping()) {
			return super.interactMob(player, hand);
		} else if (this.isBaby()) {
			this.sayNo();
			return ActionResult.success(this.world.isClient);
		} else {
			boolean bl = this.getOffers().isEmpty();
			if (hand == Hand.field_5808) {
				if (bl && !this.world.isClient) {
					this.sayNo();
				}

				player.incrementStat(Stats.field_15384);
			}

			if (bl) {
				return ActionResult.success(this.world.isClient);
			} else {
				if (!this.world.isClient && !this.offers.isEmpty()) {
					this.beginTradeWith(player);
				}

				return ActionResult.success(this.world.isClient);
			}
		}
	}

	private void sayNo() {
		this.setHeadRollingTimeLeft(40);
		if (!this.world.isClient()) {
			this.playSound(SoundEvents.field_15008, this.getSoundVolume(), this.getSoundPitch());
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
		this.updatePricesOnDemand();

		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.resetUses();
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
			this.updatePricesOnDemand();
		}
	}

	private void updatePricesOnDemand() {
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

		if (player.hasStatusEffect(StatusEffects.field_18980)) {
			StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.field_18980);
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
		this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.field_17051, 1));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent(tagx -> tag.put("VillagerData", tagx));
		tag.putByte("FoodLevel", this.foodLevel);
		tag.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE).getValue());
		tag.putInt("Xp", this.experience);
		tag.putLong("LastRestock", this.lastRestockTime);
		tag.putLong("LastGossipDecay", this.lastGossipDecayTime);
		tag.putInt("RestocksToday", this.restocksToday);
		if (this.natural) {
			tag.putBoolean("AssignProfessionWhenSpawned", true);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("VillagerData", 10)) {
			DataResult<VillagerData> dataResult = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("VillagerData")));
			dataResult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
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
		if (this.world instanceof ServerWorld) {
			this.reinitializeBrain((ServerWorld)this.world);
		}

		this.restocksToday = tag.getInt("RestocksToday");
		if (tag.contains("AssignProfessionWhenSpawned")) {
			this.natural = tag.getBoolean("AssignProfessionWhenSpawned");
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
			return this.hasCustomer() ? SoundEvents.field_14933 : SoundEvents.field_15175;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.field_15139;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15225;
	}

	public void playWorkSound() {
		SoundEvent soundEvent = this.getVillagerData().getProfession().getWorkSound();
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
			this.levelingUp = true;
			i += 5;
		}

		if (offer.shouldRewardPlayerExperience()) {
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), i));
		}
	}

	@Override
	public void setAttacker(@Nullable LivingEntity attacker) {
		if (attacker != null && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).handleInteraction(EntityInteraction.field_18476, attacker, this);
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

		this.method_30958();
		super.onDeath(source);
	}

	private void method_30958() {
		this.releaseTicketFor(MemoryModuleType.field_18438);
		this.releaseTicketFor(MemoryModuleType.field_18439);
		this.releaseTicketFor(MemoryModuleType.field_25160);
		this.releaseTicketFor(MemoryModuleType.field_18440);
	}

	private void notifyDeath(Entity killer) {
		if (this.world instanceof ServerWorld) {
			Optional<List<LivingEntity>> optional = this.brain.getOptionalMemory(MemoryModuleType.field_18442);
			if (optional.isPresent()) {
				ServerWorld serverWorld = (ServerWorld)this.world;
				((List)optional.get())
					.stream()
					.filter(livingEntity -> livingEntity instanceof InteractionObserver)
					.forEach(livingEntity -> serverWorld.handleInteraction(EntityInteraction.field_18477, killer, (InteractionObserver)livingEntity));
			}
		}
	}

	public void releaseTicketFor(MemoryModuleType<GlobalPos> memoryModuleType) {
		if (this.world instanceof ServerWorld) {
			MinecraftServer minecraftServer = ((ServerWorld)this.world).getServer();
			this.brain.getOptionalMemory(memoryModuleType).ifPresent(globalPos -> {
				ServerWorld serverWorld = minecraftServer.getWorld(globalPos.getDimension());
				if (serverWorld != null) {
					PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
					Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(globalPos.getPos());
					BiPredicate<VillagerEntity, PointOfInterestType> biPredicate = (BiPredicate<VillagerEntity, PointOfInterestType>)POINTS_OF_INTEREST.get(memoryModuleType);
					if (optional.isPresent() && biPredicate.test(this, optional.get())) {
						pointOfInterestStorage.releaseTicket(globalPos.getPos());
						DebugInfoSender.sendPointOfInterest(serverWorld, globalPos.getPos());
					}
				}
			});
		}
	}

	@Override
	public boolean isReadyToBreed() {
		return this.foodLevel + this.getAvailableFood() >= 12 && this.getBreedingAge() == 0;
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
							this.foodLevel = (byte)(this.foodLevel + integer);
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
	protected Text getDefaultName() {
		return new TranslatableText(this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getId(this.getVillagerData().getProfession()).getPath());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 12) {
			this.produceParticles(ParticleTypes.field_11201);
		} else if (status == 13) {
			this.produceParticles(ParticleTypes.field_11231);
		} else if (status == 14) {
			this.produceParticles(ParticleTypes.field_11211);
		} else if (status == 42) {
			this.produceParticles(ParticleTypes.field_11202);
		} else {
			super.handleStatus(status);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess serverWorldAccess, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (spawnReason == SpawnReason.field_16466) {
			this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.field_17051));
		}

		if (spawnReason == SpawnReason.field_16462
			|| spawnReason == SpawnReason.field_16465
			|| spawnReason == SpawnReason.field_16469
			|| spawnReason == SpawnReason.field_16470) {
			this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(serverWorldAccess.method_31081(this.getBlockPos()))));
		}

		if (spawnReason == SpawnReason.field_16474) {
			this.natural = true;
		}

		return super.initialize(serverWorldAccess, difficulty, spawnReason, entityData, entityTag);
	}

	public VillagerEntity method_7225(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		double d = this.random.nextDouble();
		VillagerType villagerType;
		if (d < 0.5) {
			villagerType = VillagerType.forBiome(serverWorld.method_31081(this.getBlockPos()));
		} else if (d < 0.75) {
			villagerType = this.getVillagerData().getType();
		} else {
			villagerType = ((VillagerEntity)passiveEntity).getVillagerData().getType();
		}

		VillagerEntity villagerEntity = new VillagerEntity(EntityType.field_6077, serverWorld, villagerType);
		villagerEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.field_16466, null, null);
		return villagerEntity;
	}

	@Override
	public void onStruckByLightning(ServerWorld serverWorld, LightningEntity lightningEntity) {
		if (serverWorld.getDifficulty() != Difficulty.field_5801) {
			LOGGER.info("Villager {} was struck by lightning {}.", this, lightningEntity);
			WitchEntity witchEntity = EntityType.field_6145.create(serverWorld);
			witchEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
			witchEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(witchEntity.getBlockPos()), SpawnReason.field_16468, null, null);
			witchEntity.setAiDisabled(this.isAiDisabled());
			if (this.hasCustomName()) {
				witchEntity.setCustomName(this.getCustomName());
				witchEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			witchEntity.setPersistent();
			serverWorld.spawnEntityAndPassengers(witchEntity);
			this.method_30958();
			this.remove();
		} else {
			super.onStruckByLightning(serverWorld, lightningEntity);
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		if (this.canGather(itemStack)) {
			SimpleInventory simpleInventory = this.getInventory();
			boolean bl = simpleInventory.canInsert(itemStack);
			if (!bl) {
				return;
			}

			this.method_29499(item);
			this.sendPickup(item, itemStack.getCount());
			ItemStack itemStack2 = simpleInventory.addStack(itemStack);
			if (itemStack2.isEmpty()) {
				item.remove();
			} else {
				itemStack.setCount(itemStack2.getCount());
			}
		}
	}

	@Override
	public boolean canGather(ItemStack stack) {
		Item item = stack.getItem();
		return (GATHERABLE_ITEMS.contains(item) || this.getVillagerData().getProfession().getGatherableItems().contains(item))
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
		return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(entry -> simpleInventory.count((Item)entry.getKey()) * (Integer)entry.getValue()).sum();
	}

	public boolean hasSeedToPlant() {
		return this.getInventory().containsAny(ImmutableSet.of(Items.field_8317, Items.field_8567, Items.field_8179, Items.field_8309));
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

	public void talkWithVillager(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if ((l < this.gossipStartTime || l >= this.gossipStartTime + 1200L) && (l < villagerEntity.gossipStartTime || l >= villagerEntity.gossipStartTime + 1200L)) {
			this.gossip.shareGossipFrom(villagerEntity.gossip, this.random, 10);
			this.gossipStartTime = l;
			villagerEntity.gossipStartTime = l;
			this.summonGolem(serverWorld, l, 5);
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

	public void summonGolem(ServerWorld serverWorld, long l, int i) {
		if (this.canSummonGolem(l)) {
			Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
			List<VillagerEntity> list = serverWorld.getNonSpectatingEntities(VillagerEntity.class, box);
			List<VillagerEntity> list2 = (List<VillagerEntity>)list.stream()
				.filter(villagerEntity -> villagerEntity.canSummonGolem(l))
				.limit(5L)
				.collect(Collectors.toList());
			if (list2.size() >= i) {
				IronGolemEntity ironGolemEntity = this.spawnIronGolem(serverWorld);
				if (ironGolemEntity != null) {
					list.forEach(GolemLastSeenSensor::method_30233);
				}
			}
		}
	}

	public boolean canSummonGolem(long time) {
		return !this.hasRecentlyWorkedAndSlept(this.world.getTime()) ? false : !this.brain.hasMemoryModule(MemoryModuleType.field_25754);
	}

	@Nullable
	private IronGolemEntity spawnIronGolem(ServerWorld serverWorld) {
		BlockPos blockPos = this.getBlockPos();

		for (int i = 0; i < 10; i++) {
			double d = (double)(serverWorld.random.nextInt(16) - 8);
			double e = (double)(serverWorld.random.nextInt(16) - 8);
			BlockPos blockPos2 = this.method_30023(blockPos, d, e);
			if (blockPos2 != null) {
				IronGolemEntity ironGolemEntity = EntityType.field_6147.create(serverWorld, null, null, null, blockPos2, SpawnReason.field_16471, false, false);
				if (ironGolemEntity != null) {
					if (ironGolemEntity.canSpawn(serverWorld, SpawnReason.field_16471) && ironGolemEntity.canSpawn(serverWorld)) {
						serverWorld.spawnEntityAndPassengers(ironGolemEntity);
						return ironGolemEntity;
					}

					ironGolemEntity.remove();
				}
			}
		}

		return null;
	}

	@Nullable
	private BlockPos method_30023(BlockPos blockPos, double d, double e) {
		int i = 6;
		BlockPos blockPos2 = blockPos.add(d, 6.0, e);
		BlockState blockState = this.world.getBlockState(blockPos2);

		for (int j = 6; j >= -6; j--) {
			BlockPos blockPos3 = blockPos2;
			BlockState blockState2 = blockState;
			blockPos2 = blockPos2.method_10074();
			blockState = this.world.getBlockState(blockPos2);
			if ((blockState2.isAir() || blockState2.getMaterial().isLiquid()) && blockState.getMaterial().blocksLight()) {
				return blockPos3;
			}
		}

		return null;
	}

	@Override
	public void onInteractionWith(EntityInteraction interaction, Entity entity) {
		if (interaction == EntityInteraction.field_18474) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18427, 20);
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18426, 25);
		} else if (interaction == EntityInteraction.field_18478) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18428, 2);
		} else if (interaction == EntityInteraction.field_18476) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18425, 25);
		} else if (interaction == EntityInteraction.field_18477) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18424, 25);
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

	public VillagerGossips getGossip() {
		return this.gossip;
	}

	public void setGossipDataFromTag(Tag tag) {
		this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, tag));
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugInfoSender.sendBrainDebugData(this);
	}

	@Override
	public void sleep(BlockPos pos) {
		super.sleep(pos);
		this.brain.remember(MemoryModuleType.field_19385, this.world.getTime());
		this.brain.forget(MemoryModuleType.field_18445);
		this.brain.forget(MemoryModuleType.field_19293);
	}

	@Override
	public void wakeUp() {
		super.wakeUp();
		this.brain.remember(MemoryModuleType.field_20616, this.world.getTime());
	}

	private boolean hasRecentlyWorkedAndSlept(long worldTime) {
		Optional<Long> optional = this.brain.getOptionalMemory(MemoryModuleType.field_19385);
		return optional.isPresent() ? worldTime - (Long)optional.get() < 24000L : false;
	}
}
