/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4316;
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
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
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
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Hand;
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
import org.jetbrains.annotations.Nullable;

public class VillagerEntity
extends AbstractTraderEntity
implements InteractionObserver,
VillagerDataContainer {
    private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, new Item[]{Items.BEETROOT_SEEDS});
    private int levelUpTimer;
    private boolean levellingUp;
    @Nullable
    private PlayerEntity lastCustomer;
    private byte foodLevel;
    private final VillagerGossips gossip = new VillagerGossips();
    private long gossipStartTime;
    private long field_19357;
    private int experience;
    private long lastRestock;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, new MemoryModuleType[]{MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_LAST_SEEN_TIME});
    private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_LAST_SEEN);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POINTS_OF_INTEREST = ImmutableMap.of(MemoryModuleType.HOME, (villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.HOME, MemoryModuleType.JOB_SITE, (villagerEntity, pointOfInterestType) -> villagerEntity.getVillagerData().getProfession().getWorkStation() == pointOfInterestType, MemoryModuleType.MEETING_POINT, (villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.MEETING);

    public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        this(entityType, world, VillagerType.PLAINS);
    }

    public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType villagerType) {
        super((EntityType<? extends AbstractTraderEntity>)entityType, world);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.NONE));
        this.brain = this.deserializeBrain(new Dynamic<CompoundTag>(NbtOps.INSTANCE, new CompoundTag()));
    }

    public Brain<VillagerEntity> getBrain() {
        return super.getBrain();
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<VillagerEntity> brain = new Brain<VillagerEntity>(MEMORY_MODULES, SENSORS, dynamic);
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
        if (this.isBaby()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.setTaskList(Activity.PLAY, VillagerTaskListProvider.createPlayTasks(f));
        } else {
            brain.setSchedule(Schedule.VILLAGER_DEFAULT);
            brain.setTaskList(Activity.WORK, VillagerTaskListProvider.createWorkTasks(villagerProfession, f), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
        }
        brain.setTaskList(Activity.CORE, VillagerTaskListProvider.createCoreTasks(villagerProfession, f));
        brain.setTaskList(Activity.MEET, VillagerTaskListProvider.createMeetTasks(villagerProfession, f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT)));
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
        Raid raid;
        this.world.getProfiler().push("brain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        if (!this.hasCustomer() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levellingUp) {
                    this.levelUp();
                    this.levellingUp = false;
                }
                this.addPotionEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastCustomer != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
            this.world.sendEntityStatus(this, (byte)14);
            this.lastCustomer = null;
        }
        if (!this.isAiDisabled() && this.random.nextInt(100) == 0 && (raid = ((ServerWorld)this.world).getRaidAt(new BlockPos(this))) != null && raid.isActive() && !raid.isFinished()) {
            this.world.sendEntityStatus(this, (byte)42);
        }
        if (!this.brain.getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent() && this.hasCustomer()) {
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
        this.method_20696();
    }

    @Override
    public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
        boolean bl;
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        boolean bl2 = bl = itemStack.getItem() == Items.NAME_TAG;
        if (bl) {
            itemStack.useOnEntity(playerEntity, this, hand);
            return true;
        }
        if (itemStack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return super.interactMob(playerEntity, hand);
            }
            boolean bl22 = this.getOffers().isEmpty();
            if (hand == Hand.MAIN_HAND) {
                if (bl22 && !this.world.isClient) {
                    this.sayNo();
                }
                playerEntity.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (bl22) {
                return super.interactMob(playerEntity, hand);
            }
            if (!this.world.isClient && !this.offers.isEmpty()) {
                this.beginTradeWith(playerEntity);
            }
            return true;
        }
        return super.interactMob(playerEntity, hand);
    }

    private void sayNo() {
        this.setHeadRollingTimeLeft(40);
        if (!this.world.isClient()) {
            this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private void beginTradeWith(PlayerEntity playerEntity) {
        this.prepareRecipesFor(playerEntity);
        this.setCurrentCustomer(playerEntity);
        this.sendOffers(playerEntity, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
        boolean bl = this.getCurrentCustomer() != null && playerEntity == null;
        super.setCurrentCustomer(playerEntity);
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
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.updatePriceOnDemand();
            tradeOffer.resetUses();
        }
        if (this.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            this.method_20695();
        }
        this.lastRestock = this.world.getTimeOfDay() % 24000L;
    }

    private void prepareRecipesFor(PlayerEntity playerEntity) {
        int i = this.method_20594(playerEntity);
        if (i != 0) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice(-MathHelper.floor((float)i * tradeOffer.getPriceMultiplier()));
            }
        }
        if (playerEntity.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            StatusEffectInstance statusEffectInstance = playerEntity.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
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
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.put("VillagerData", this.getVillagerData().serialize(NbtOps.INSTANCE));
        compoundTag.putByte("FoodLevel", this.foodLevel);
        compoundTag.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE).getValue());
        compoundTag.putInt("Xp", this.experience);
        compoundTag.putLong("LastRestock", this.lastRestock);
        compoundTag.putLong("LastGossipDecay", this.field_19357);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.containsKey("VillagerData", 10)) {
            this.setVillagerData(new VillagerData(new Dynamic<Tag>(NbtOps.INSTANCE, compoundTag.getTag("VillagerData"))));
        }
        if (compoundTag.containsKey("Offers", 10)) {
            this.offers = new TraderOfferList(compoundTag.getCompound("Offers"));
        }
        if (compoundTag.containsKey("FoodLevel", 1)) {
            this.foodLevel = compoundTag.getByte("FoodLevel");
        }
        ListTag listTag = compoundTag.getList("Gossips", 10);
        this.gossip.deserialize(new Dynamic<ListTag>(NbtOps.INSTANCE, listTag));
        if (compoundTag.containsKey("Xp", 3)) {
            this.experience = compoundTag.getInt("Xp");
        }
        this.lastRestock = compoundTag.getLong("LastRestock");
        this.field_19357 = compoundTag.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        this.reinitializeBrain((ServerWorld)this.world);
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.hasCustomer()) {
            return SoundEvents.ENTITY_VILLAGER_TRADE;
        }
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
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
        this.experience += tradeOffer.getTraderExperience();
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
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.VILLAGER_HURT, livingEntity, this);
            if (this.isAlive() && livingEntity instanceof PlayerEntity) {
                this.world.sendEntityStatus(this, (byte)13);
            }
        }
        super.setAttacker(livingEntity);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        Entity entity = damageSource.getAttacker();
        if (entity != null) {
            this.method_20690(entity);
        }
        this.releaseTicketFor(MemoryModuleType.HOME);
        this.releaseTicketFor(MemoryModuleType.JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
        super.onDeath(damageSource);
    }

    private void method_20690(Entity entity) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        Optional<List<LivingEntity>> optional = this.brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
        if (!optional.isPresent()) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)this.world;
        optional.get().stream().filter(livingEntity -> livingEntity instanceof InteractionObserver).forEach(livingEntity -> serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, entity, (InteractionObserver)((Object)livingEntity)));
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> memoryModuleType) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerWorld)this.world).getServer();
        this.brain.getOptionalMemory(memoryModuleType).ifPresent(globalPos -> {
            ServerWorld serverWorld = minecraftServer.getWorld(globalPos.getDimension());
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
            Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(globalPos.getPos());
            BiPredicate<VillagerEntity, PointOfInterestType> biPredicate = POINTS_OF_INTEREST.get(memoryModuleType);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                pointOfInterestStorage.releaseTicket(globalPos.getPos());
                DebugRendererInfoManager.sendPointOfInterest(serverWorld, globalPos.getPos());
            }
        });
    }

    public boolean isReadyToBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && this.getBreedingAge() == 0;
    }

    private boolean method_20698() {
        return this.foodLevel < 12;
    }

    public void consumeAvailableFood() {
        if (!this.method_20698() || this.getAvailableFood() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().getInvSize(); ++i) {
            int j;
            Integer integer;
            ItemStack itemStack = this.getInventory().getInvStack(i);
            if (itemStack.isEmpty() || (integer = ITEM_FOOD_VALUES.get(itemStack.getItem())) == null) continue;
            for (int k = j = itemStack.getCount(); k > 0; --k) {
                this.foodLevel = (byte)(this.foodLevel + integer);
                this.getInventory().takeInvStack(i, 1);
                if (this.method_20698()) continue;
                return;
            }
        }
    }

    public int method_20594(PlayerEntity playerEntity) {
        return this.gossip.getReputationFor(playerEntity.getUuid(), villageGossipType -> true);
    }

    private void depleteFood(int i) {
        this.foodLevel = (byte)(this.foodLevel - i);
    }

    public void method_20697() {
        this.consumeAvailableFood();
        this.depleteFood(12);
    }

    public void setOffers(TraderOfferList traderOfferList) {
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
    public Text getDisplayName() {
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        Text text = this.getCustomName();
        if (text != null) {
            return Team.modifyText(abstractTeam, text).styled(style -> style.setHoverEvent(this.getHoverEvent()).setInsertion(this.getUuidAsString()));
        }
        VillagerProfession villagerProfession = this.getVillagerData().getProfession();
        Text text2 = new TranslatableText(this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getId(villagerProfession).getPath(), new Object[0]).styled(style -> style.setHoverEvent(this.getHoverEvent()).setInsertion(this.getUuidAsString()));
        if (abstractTeam != null) {
            text2.formatted(abstractTeam.getColor());
        }
        return text2;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 12) {
            this.produceParticles(ParticleTypes.HEART);
        } else if (b == 13) {
            this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
        } else if (b == 14) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        } else if (b == 42) {
            this.produceParticles(ParticleTypes.SPLASH);
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        if (spawnType == SpawnType.BREEDING) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }
        if (spawnType == SpawnType.COMMAND || spawnType == SpawnType.SPAWN_EGG || spawnType == SpawnType.SPAWNER) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(iWorld.getBiome(new BlockPos(this)))));
        }
        return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
    }

    public VillagerEntity method_7225(PassiveEntity passiveEntity) {
        double d = this.random.nextDouble();
        VillagerType villagerType = d < 0.5 ? VillagerType.forBiome(this.world.getBiome(new BlockPos(this))) : (d < 0.75 ? this.getVillagerData().getType() : ((VillagerEntity)passiveEntity).getVillagerData().getType());
        VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, this.world, villagerType);
        villagerEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.BREEDING, null, null);
        return villagerEntity;
    }

    @Override
    public void onStruckByLightning(LightningEntity lightningEntity) {
        WitchEntity witchEntity = EntityType.WITCH.create(this.world);
        witchEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
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
    protected void loot(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getStack();
        Item item = itemStack.getItem();
        VillagerProfession villagerProfession = this.getVillagerData().getProfession();
        if (GATHERABLE_ITEMS.contains(item) || villagerProfession.getGatherableItems().contains(item)) {
            BasicInventory basicInventory = this.getInventory();
            int i = basicInventory.getInvAmountOf(item);
            if (i == 256) {
                return;
            }
            if (i > 256) {
                basicInventory.method_20631(item, i - 256);
                return;
            }
            this.sendPickup(itemEntity, itemStack.getCount());
            ItemStack itemStack2 = basicInventory.add(itemStack);
            if (itemStack2.isEmpty()) {
                itemEntity.remove();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }
    }

    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }

    public boolean canBreed() {
        return this.getAvailableFood() < 12;
    }

    private int getAvailableFood() {
        BasicInventory basicInventory = this.getInventory();
        return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(entry -> basicInventory.getInvAmountOf((Item)entry.getKey()) * (Integer)entry.getValue()).sum();
    }

    private void method_20695() {
        BasicInventory basicInventory = this.getInventory();
        int i = basicInventory.getInvAmountOf(Items.WHEAT);
        int j = i / 3;
        if (j == 0) {
            return;
        }
        int k = j * 3;
        basicInventory.method_20631(Items.WHEAT, k);
        ItemStack itemStack = basicInventory.add(new ItemStack(Items.BREAD, j));
        if (!itemStack.isEmpty()) {
            this.dropStack(itemStack, 0.5f);
        }
    }

    public boolean hasSeedToPlant() {
        BasicInventory basicInventory = this.getInventory();
        return basicInventory.containsAnyInInv(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }

    @Override
    protected void fillRecipes() {
        VillagerData villagerData = this.getVillagerData();
        Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(villagerData.getProfession());
        if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
            return;
        }
        TradeOffers.Factory[] factorys = (TradeOffers.Factory[])int2ObjectMap.get(villagerData.getLevel());
        if (factorys == null) {
            return;
        }
        TraderOfferList traderOfferList = this.getOffers();
        this.fillRecipesFromPool(traderOfferList, factorys, 2);
    }

    public void talkWithVillager(VillagerEntity villagerEntity, long l) {
        if (l >= this.gossipStartTime && l < this.gossipStartTime + 1200L || l >= villagerEntity.gossipStartTime && l < villagerEntity.gossipStartTime + 1200L) {
            return;
        }
        this.gossip.shareGossipFrom(villagerEntity.gossip, this.random, 10);
        this.gossipStartTime = l;
        villagerEntity.gossipStartTime = l;
        this.method_20688(l, 5);
    }

    private void method_20696() {
        long l = this.world.getTime();
        if (this.field_19357 == 0L) {
            this.field_19357 = l;
            return;
        }
        if (l < this.field_19357 + 24000L) {
            return;
        }
        this.gossip.method_20651();
        this.field_19357 = l;
    }

    public void method_20688(long l, int i) {
        if (!this.method_20687(l)) {
            return;
        }
        Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
        List<VillagerEntity> list = this.world.getEntities(VillagerEntity.class, box);
        List list2 = list.stream().filter(villagerEntity -> villagerEntity.method_20687(l)).limit(5L).collect(Collectors.toList());
        if (list2.size() < i) {
            return;
        }
        IronGolemEntity ironGolemEntity = this.spawnIronGolem();
        if (ironGolemEntity == null) {
            return;
        }
        list.forEach(villagerEntity -> villagerEntity.method_20692(l));
    }

    private void method_20692(long l) {
        this.brain.putMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, l);
    }

    private boolean method_20694(long l) {
        Optional<Long> optional = this.brain.getOptionalMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);
        if (!optional.isPresent()) {
            return false;
        }
        Long long_ = optional.get();
        return l - long_ <= 600L;
    }

    public boolean method_20687(long l) {
        VillagerData villagerData = this.getVillagerData();
        if (villagerData.getProfession() == VillagerProfession.NONE || villagerData.getProfession() == VillagerProfession.NITWIT) {
            return false;
        }
        if (!this.method_20741(this.world.getTime())) {
            return false;
        }
        return !this.method_20694(l);
    }

    @Nullable
    private IronGolemEntity spawnIronGolem() {
        BlockPos blockPos = new BlockPos(this);
        for (int i = 0; i < 10; ++i) {
            BlockPos blockPos3;
            IronGolemEntity ironGolemEntity;
            double d = this.world.random.nextInt(16) - 8;
            double e = this.world.random.nextInt(16) - 8;
            double f = 6.0;
            for (int j = 0; j >= -12; --j) {
                BlockPos blockPos2 = blockPos.add(d, f + (double)j, e);
                if (!this.world.getBlockState(blockPos2).isAir() && !this.world.getBlockState(blockPos2).getMaterial().isLiquid() || !this.world.getBlockState(blockPos2.down()).getMaterial().blocksLight()) continue;
                f += (double)j;
                break;
            }
            if ((ironGolemEntity = EntityType.IRON_GOLEM.create(this.world, null, null, null, blockPos3 = blockPos.add(d, f, e), SpawnType.MOB_SUMMONED, false, false)) == null) continue;
            if (ironGolemEntity.canSpawn(this.world, SpawnType.MOB_SUMMONED) && ironGolemEntity.canSpawn(this.world)) {
                this.world.spawnEntity(ironGolemEntity);
                return ironGolemEntity;
            }
            ironGolemEntity.remove();
        }
        return null;
    }

    @Override
    public void onInteractionWith(EntityInteraction entityInteraction, Entity entity) {
        if (entityInteraction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_POSITIVE, 25);
        } else if (entityInteraction == EntityInteraction.TRADE) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.TRADING, 2);
        } else if (entityInteraction == EntityInteraction.VILLAGER_HURT) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_NEGATIVE, 25);
        } else if (entityInteraction == EntityInteraction.VILLAGER_KILLED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 25);
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
        this.brain.putMemory(MemoryModuleType.LAST_SLEPT, class_4316.method_20791(this.world.getTime()));
    }

    private boolean method_20741(long l) {
        Optional<class_4316> optional = this.brain.getOptionalMemory(MemoryModuleType.LAST_SLEPT);
        Optional<class_4316> optional2 = this.brain.getOptionalMemory(MemoryModuleType.LAST_WORKED_AT_POI);
        if (optional.isPresent() && optional2.isPresent()) {
            return l - optional.get().method_20790() < 24000L && l - optional2.get().method_20790() < 36000L;
        }
        return false;
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity passiveEntity) {
        return this.method_7225(passiveEntity);
    }
}

