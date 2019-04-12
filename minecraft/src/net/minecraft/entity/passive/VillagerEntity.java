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
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
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
	public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.field_8229, 4, Items.field_8567, 1, Items.field_8179, 1, Items.field_8186, 1);
	private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of(
		Items.field_8229, Items.field_8567, Items.field_8179, Items.field_8861, Items.field_8317, Items.field_8186, Items.field_8309
	);
	private int levelUpTimer;
	private boolean levellingUp;
	@Nullable
	private PlayerEntity lastCustomer;
	@Nullable
	private UUID buddyGolemId;
	private long oneMinAfterLastGolemCheckTimestamp = Long.MIN_VALUE;
	private byte foodLevel;
	private final VillagerGossips gossip = new VillagerGossips();
	private long gossipStartTime;
	private int experience;
	private long lastRestock;
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.field_18438,
		MemoryModuleType.field_18439,
		MemoryModuleType.field_18440,
		MemoryModuleType.field_18441,
		MemoryModuleType.field_18442,
		MemoryModuleType.field_19006,
		MemoryModuleType.field_18443,
		MemoryModuleType.field_18444,
		MemoryModuleType.field_18445,
		MemoryModuleType.field_18446,
		MemoryModuleType.field_18447,
		MemoryModuleType.field_18448,
		MemoryModuleType.field_18449,
		MemoryModuleType.field_18450,
		MemoryModuleType.field_19007,
		MemoryModuleType.field_18451,
		MemoryModuleType.field_18452,
		MemoryModuleType.field_18453,
		MemoryModuleType.field_18873,
		MemoryModuleType.field_18874,
		MemoryModuleType.field_19008,
		MemoryModuleType.field_19009
	);
	private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(
		SensorType.field_18466,
		SensorType.field_18467,
		SensorType.field_18468,
		SensorType.field_19010,
		SensorType.field_18469,
		SensorType.field_18470,
		SensorType.field_19011,
		SensorType.field_18875
	);
	public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POINTS_OF_INTEREST = ImmutableMap.of(
		MemoryModuleType.field_18438,
		(villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.field_18517,
		MemoryModuleType.field_18439,
		(villagerEntity, pointOfInterestType) -> villagerEntity.getVillagerData().getProfession().getWorkStation() == pointOfInterestType,
		MemoryModuleType.field_18440,
		(villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.field_18518
	);

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
		this(entityType, world, VillagerType.PLAINS);
	}

	public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType villagerType) {
		super(entityType, world);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.setCanPickUpLoot(true);
		this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.field_17051));
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

	public void reinitializeBrain(ServerWorld serverWorld) {
		Brain<VillagerEntity> brain = this.getBrain();
		brain.stopAllTasks(serverWorld, this);
		this.brain = brain.copy();
		this.initBrain(this.getBrain());
	}

	private void initBrain(Brain<VillagerEntity> brain) {
		VillagerProfession villagerProfession = this.getVillagerData().getProfession();
		float f = (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
		if (this.isChild()) {
			brain.setSchedule(Schedule.VILLAGER_BABY);
			brain.setTaskList(Activity.field_18885, VillagerTaskListProvider.getPlayTasks(f));
		} else {
			brain.setSchedule(Schedule.VILLAGER_DEFAULT);
			brain.setTaskList(
				Activity.field_18596,
				VillagerTaskListProvider.getWorkTasks(villagerProfession, f),
				ImmutableSet.of(Pair.of(MemoryModuleType.field_18439, MemoryModuleState.field_18456))
			);
		}

		brain.setTaskList(Activity.field_18594, VillagerTaskListProvider.getCoreTasks(villagerProfession, f));
		brain.setTaskList(
			Activity.field_18598,
			VillagerTaskListProvider.getMeetTasks(villagerProfession, f),
			ImmutableSet.of(Pair.of(MemoryModuleType.field_18440, MemoryModuleState.field_18456))
		);
		brain.setTaskList(
			Activity.field_18597,
			VillagerTaskListProvider.getRestTasks(villagerProfession, f),
			ImmutableSet.of(Pair.of(MemoryModuleType.field_18438, MemoryModuleState.field_18456))
		);
		brain.setTaskList(Activity.field_18595, VillagerTaskListProvider.getIdleTasks(villagerProfession, f));
		brain.setTaskList(Activity.field_18599, VillagerTaskListProvider.getPanicTasks(villagerProfession, f));
		brain.setTaskList(Activity.field_19042, VillagerTaskListProvider.getPreRaidTasks(villagerProfession, f));
		brain.setTaskList(Activity.field_19041, VillagerTaskListProvider.getRaidTasks(villagerProfession, f));
		brain.setTaskList(Activity.field_19043, VillagerTaskListProvider.getHideTasks(villagerProfession, f));
		brain.setCoreActivities(ImmutableSet.of(Activity.field_18594));
		brain.setDefaultActivity(Activity.field_18595);
		brain.resetPossibleActivities(Activity.field_18595);
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

				this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 200, 0));
			}
		}

		if (this.lastCustomer != null && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).handleInteraction(EntityInteraction.field_18478, this.lastCustomer, this);
			this.world.sendEntityStatus(this, (byte)14);
			this.lastCustomer = null;
		}

		if (!this.isAiDisabled() && this.random.nextInt(100) == 0) {
			Raid raid = ((ServerWorld)this.world).getRaidAt(new BlockPos(this));
			if (raid != null && raid.isActive() && !raid.isFinished()) {
				this.world.sendEntityStatus(this, (byte)42);
			}
		}

		super.mobTick();
	}

	public void resetCustomer() {
		this.setCurrentCustomer(null);
		this.clearCurrentBonus();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getHeadRollingTimeLeft() > 0) {
			this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
		}
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = itemStack.getItem() == Items.field_8448;
		if (bl) {
			itemStack.interactWithEntity(playerEntity, this, hand);
			return true;
		} else if (itemStack.getItem() == Items.field_8086 || !this.isAlive() || this.hasCustomer() || this.isSleeping()) {
			return super.interactMob(playerEntity, hand);
		} else if (this.isChild()) {
			this.sayNo();
			return super.interactMob(playerEntity, hand);
		} else {
			if (hand == Hand.MAIN) {
				playerEntity.incrementStat(Stats.field_15384);
			}

			if (this.getOffers().isEmpty()) {
				this.sayNo();
				return super.interactMob(playerEntity, hand);
			} else {
				if (!this.world.isClient && !this.offers.isEmpty()) {
					this.beginTradeWith(playerEntity);
				}

				return true;
			}
		}
	}

	private void sayNo() {
		this.setHeadRollingTimeLeft(40);
		if (!this.world.isClient()) {
			this.playSound(SoundEvents.field_15008, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	private void beginTradeWith(PlayerEntity playerEntity) {
		this.prepareRecipesFor(playerEntity);
		this.setCurrentCustomer(playerEntity);
		this.sendOffers(playerEntity, this.getDisplayName(), this.getVillagerData().getLevel());
	}

	public void restock() {
		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.updatePriceOnDemand();
			tradeOffer.resetUses();
		}

		this.lastRestock = this.world.getTimeOfDay() % 24000L;
	}

	private void prepareRecipesFor(PlayerEntity playerEntity) {
		int i = this.gossip.getReputationFor(playerEntity.getUuid(), villageGossipType -> villageGossipType != VillageGossipType.field_18429);
		if (i != 0) {
			for (TradeOffer tradeOffer : this.getOffers()) {
				tradeOffer.increaseSpecialPrice(-MathHelper.floor((float)i * tradeOffer.getPriceMultiplier()));
			}
		}

		if (playerEntity.hasStatusEffect(StatusEffects.field_18980)) {
			StatusEffectInstance statusEffectInstance = playerEntity.getStatusEffect(StatusEffects.field_18980);
			int j = statusEffectInstance.getAmplifier();

			for (TradeOffer tradeOffer2 : this.getOffers()) {
				double d = 0.3 + 0.0625 * (double)j;
				int k = (int)Math.floor(d * (double)tradeOffer2.getOriginalFirstBuyItem().getAmount());
				tradeOffer2.increaseSpecialPrice(-Math.max(k, 1));
			}
		}
	}

	private void clearCurrentBonus() {
		for (TradeOffer tradeOffer : this.getOffers()) {
			tradeOffer.clearSpecialPrice();
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.field_17051, 1));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.put("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
		compoundTag.putByte("FoodLevel", this.foodLevel);
		compoundTag.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE).getValue());
		compoundTag.putInt("Xp", this.experience);
		compoundTag.putLong("LastRestock", this.lastRestock);
		if (this.buddyGolemId != null) {
			compoundTag.putUuid("BuddyGolem", this.buddyGolemId);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("VillagerData", 10)) {
			this.setVillagerData(new VillagerData(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getTag("VillagerData"))));
		}

		if (compoundTag.containsKey("Offers", 10)) {
			this.offers = new TraderOfferList(compoundTag.getCompound("Offers"));
		}

		if (compoundTag.containsKey("FoodLevel", 1)) {
			this.foodLevel = compoundTag.getByte("FoodLevel");
		}

		ListTag listTag = compoundTag.getList("Gossips", 10);
		this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, listTag));
		if (compoundTag.containsKey("Xp", 3)) {
			this.experience = compoundTag.getInt("Xp");
		} else {
			int i = this.getVillagerData().getLevel();
			if (VillagerData.canLevelUp(i)) {
				this.experience = VillagerData.getLowerLevelExperience(i);
			}
		}

		this.lastRestock = compoundTag.getLong("LastRestock");
		if (compoundTag.hasUuid("BuddyGolem")) {
			this.buddyGolemId = compoundTag.getUuid("BuddyGolem");
		}

		this.setCanPickUpLoot(true);
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
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
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15139;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15225;
	}

	public void playWorkSound() {
		SoundEvent soundEvent = this.getVillagerData().getProfession().getWorkStation().getSound();
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
	protected void afterUsing(TradeOffer tradeOffer) {
		int i = 3 + this.random.nextInt(4);
		this.experience = this.experience + tradeOffer.getTraderExperience();
		this.lastCustomer = this.getCurrentCustomer();
		if (this.canLevelUp()) {
			this.levelUpTimer = 40;
			this.levellingUp = true;
			i += 5;
		}

		if (tradeOffer.shouldRewardPlayerExperience()) {
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, i));
		}
	}

	@Override
	public void setAttacker(@Nullable LivingEntity livingEntity) {
		if (livingEntity != null && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).handleInteraction(EntityInteraction.field_18476, livingEntity, this);
			if (this.isAlive() && livingEntity instanceof PlayerEntity) {
				this.world.sendEntityStatus(this, (byte)13);
			}
		}

		super.setAttacker(livingEntity);
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		this.releaseTicketFor(MemoryModuleType.field_18438);
		this.releaseTicketFor(MemoryModuleType.field_18439);
		this.releaseTicketFor(MemoryModuleType.field_18440);
		super.onDeath(damageSource);
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

	public void consumeAvailableFood() {
		if (this.foodLevel < 12 && this.getAvailableFood() != 0) {
			for (int i = 0; i < this.getInventory().getInvSize(); i++) {
				ItemStack itemStack = this.getInventory().getInvStack(i);
				if (!itemStack.isEmpty()) {
					Integer integer = (Integer)ITEM_FOOD_VALUES.get(itemStack.getItem());
					if (integer != null) {
						int j = itemStack.getAmount();

						for (int k = j; k > 0; k--) {
							this.foodLevel = (byte)(this.foodLevel + integer);
							this.getInventory().takeInvStack(i, 1);
							if (this.foodLevel >= 12) {
								return;
							}
						}
					}
				}
			}
		}
	}

	public void depleteFood(int i) {
		this.foodLevel = (byte)(this.foodLevel - i);
	}

	public void setRecipes(TraderOfferList traderOfferList) {
		this.offers = traderOfferList;
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
	public TextComponent getDisplayName() {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		TextComponent textComponent = this.getCustomName();
		if (textComponent != null) {
			return Team.modifyText(abstractTeam, textComponent)
				.modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
		} else {
			VillagerProfession villagerProfession = this.getVillagerData().getProfession();
			TextComponent textComponent2 = new TranslatableTextComponent(
					this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getId(villagerProfession).getPath()
				)
				.modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
			if (abstractTeam != null) {
				textComponent2.applyFormat(abstractTeam.getColor());
			}

			return textComponent2;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 12) {
			this.produceParticles(ParticleTypes.field_11201);
		} else if (b == 13) {
			this.produceParticles(ParticleTypes.field_11231);
		} else if (b == 14) {
			this.produceParticles(ParticleTypes.field_11211);
		} else if (b == 42) {
			this.produceParticles(ParticleTypes.field_11202);
		} else {
			super.handleStatus(b);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (spawnType == SpawnType.field_16466) {
			this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.field_17051));
		}

		if (spawnType == SpawnType.field_16462 || spawnType == SpawnType.field_16465 || spawnType == SpawnType.field_16469) {
			this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(iWorld.getBiome(new BlockPos(this)))));
		}

		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	public VillagerEntity method_7225(PassiveEntity passiveEntity) {
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
		villagerEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.field_16466, null, null);
		return villagerEntity;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		WitchEntity witchEntity = EntityType.WITCH.create(this.world);
		witchEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
		witchEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(witchEntity)), SpawnType.field_16468, null, null);
		witchEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			witchEntity.setCustomName(this.getCustomName());
			witchEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(witchEntity);
		this.remove();
	}

	@Override
	protected void loot(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		Item item = itemStack.getItem();
		VillagerProfession villagerProfession = this.getVillagerData().getProfession();
		if (GATHERABLE_ITEMS.contains(item) || villagerProfession.getGatherableItems().contains(item)) {
			if (villagerProfession == VillagerProfession.field_17056 && item == Items.field_8861) {
				int i = itemStack.getAmount() / 3;
				if (i > 0) {
					ItemStack itemStack2 = this.getInventory().add(new ItemStack(Items.field_8229, i));
					itemStack.subtractAmount(i * 3);
					if (!itemStack2.isEmpty()) {
						this.dropStack(itemStack2, 0.5F);
					}
				}
			}

			ItemStack itemStack3 = this.getInventory().add(itemStack);
			if (itemStack3.isEmpty()) {
				itemEntity.remove();
			} else {
				itemStack.setAmount(itemStack3.getAmount());
			}
		}
	}

	public boolean wantsToStartBreeding() {
		return this.getAvailableFood() >= 24;
	}

	public boolean canBreed() {
		boolean bl = this.getVillagerData().getProfession() == VillagerProfession.field_17056;
		int i = this.getAvailableFood();
		return bl ? i < 60 : i < 12;
	}

	private int getAvailableFood() {
		BasicInventory basicInventory = this.getInventory();
		return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(entry -> basicInventory.getInvAmountOf((Item)entry.getKey()) * (Integer)entry.getValue()).sum();
	}

	public boolean hasSeedToPlant() {
		BasicInventory basicInventory = this.getInventory();
		return basicInventory.containsAnyInInv(ImmutableSet.of(Items.field_8317, Items.field_8567, Items.field_8179, Items.field_8309));
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

	public void talkWithVillager(VillagerEntity villagerEntity, long l) {
		if ((l < this.gossipStartTime || l >= this.gossipStartTime + 1200L) && (l < villagerEntity.gossipStartTime || l >= villagerEntity.gossipStartTime + 1200L)) {
			boolean bl = this.isLackingBuddyGolem(l);
			if (this.wantsGolem(this) || bl) {
				this.gossip.startGossip(this.getUuid(), VillageGossipType.field_18429, VillageGossipType.field_18429.maxReputation);
			}

			this.gossip.shareGossipFrom(villagerEntity.gossip, this.random, 10);
			this.gossipStartTime = l;
			villagerEntity.gossipStartTime = l;
			if (bl) {
				this.trySpawnGolem();
			}
		}
	}

	private void trySpawnGolem() {
		VillagerData villagerData = this.getVillagerData();
		if (villagerData.getProfession() != VillagerProfession.field_17051 && villagerData.getProfession() != VillagerProfession.field_17062) {
			Optional<VillagerEntity.GolemSpawnCondition> optional = this.getBrain().getOptionalMemory(MemoryModuleType.field_18874);
			if (optional.isPresent()) {
				if (((VillagerEntity.GolemSpawnCondition)optional.get()).canSpawn(this.world.getTime())) {
					boolean bl = this.gossip.getGossipCount(VillageGossipType.field_18429, d -> d > 30.0) >= 5L;
					if (bl) {
						BoundingBox boundingBox = this.getBoundingBox().stretch(80.0, 80.0, 80.0);
						List<VillagerEntity> list = (List<VillagerEntity>)this.world
							.getEntities(VillagerEntity.class, boundingBox, this::wantsGolem)
							.stream()
							.limit(5L)
							.collect(Collectors.toList());
						boolean bl2 = list.size() >= 5;
						if (bl2) {
							IronGolemEntity ironGolemEntity = this.spawnIronGolem();
							if (ironGolemEntity != null) {
								UUID uUID = ironGolemEntity.getUuid();

								for (VillagerEntity villagerEntity : list) {
									for (VillagerEntity villagerEntity2 : list) {
										villagerEntity.gossip.startGossip(villagerEntity2.getUuid(), VillageGossipType.field_18429, -VillageGossipType.field_18429.maxReputation);
									}

									villagerEntity.buddyGolemId = uUID;
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean wantsGolem(Entity entity) {
		return this.gossip.getReputationFor(entity.getUuid(), villageGossipType -> villageGossipType == VillageGossipType.field_18429) > 30;
	}

	private boolean isLackingBuddyGolem(long l) {
		if (this.buddyGolemId == null) {
			return true;
		} else {
			if (this.oneMinAfterLastGolemCheckTimestamp < l + 1200L) {
				this.oneMinAfterLastGolemCheckTimestamp = l + 1200L;
				Entity entity = ((ServerWorld)this.world).getEntity(this.buddyGolemId);
				if (entity == null || !entity.isAlive() || this.squaredDistanceTo(entity) > 80.0) {
					this.buddyGolemId = null;
					return true;
				}
			}

			return false;
		}
	}

	@Nullable
	private IronGolemEntity spawnIronGolem() {
		BlockPos blockPos = new BlockPos(this);

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
			IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.create(this.world, null, null, null, blockPos2, SpawnType.field_16471, false, false);
			if (ironGolemEntity != null) {
				if (ironGolemEntity.canSpawn(this.world, SpawnType.field_16471) && ironGolemEntity.canSpawn(this.world)) {
					this.world.spawnEntity(ironGolemEntity);
					return ironGolemEntity;
				}

				ironGolemEntity.remove();
			}
		}

		return null;
	}

	@Override
	public void onInteractionWith(EntityInteraction entityInteraction, Entity entity) {
		if (entityInteraction == EntityInteraction.field_18474) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18427, 25);
		} else if (entityInteraction == EntityInteraction.field_18478) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18428, 2);
		} else if (entityInteraction == EntityInteraction.field_18476) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18425, 25);
		} else if (entityInteraction == EntityInteraction.field_18477) {
			this.gossip.startGossip(entity.getUuid(), VillageGossipType.field_18424, 25);
		}
	}

	@Override
	public int getExperience() {
		return this.experience;
	}

	public void setExperience(int i) {
		this.experience = i;
	}

	public long getLastRestock() {
		return this.lastRestock;
	}

	@Override
	protected void sendAiDebugData() {
		super.sendAiDebugData();
		DebugRendererInfoManager.sendVillagerAiDebugData(this);
	}

	@Override
	public void sleep(BlockPos blockPos) {
		super.sleep(blockPos);
		VillagerEntity.GolemSpawnCondition golemSpawnCondition = (VillagerEntity.GolemSpawnCondition)this.getBrain()
			.getOptionalMemory(MemoryModuleType.field_18874)
			.orElseGet(VillagerEntity.GolemSpawnCondition::new);
		golemSpawnCondition.setLastSlept(this.world.getTime());
		this.brain.putMemory(MemoryModuleType.field_18874, golemSpawnCondition);
	}

	public static final class GolemSpawnCondition {
		private long lastWorked;
		private long lastSlept;

		public void setLastWorked(long l) {
			this.lastWorked = l;
		}

		public void setLastSlept(long l) {
			this.lastSlept = l;
		}

		private boolean canSpawn(long l) {
			return l - this.lastSlept < 24000L && l - this.lastWorked < 36000L;
		}
	}
}
