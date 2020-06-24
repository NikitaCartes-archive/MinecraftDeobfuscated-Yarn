/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.NbtOps;
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
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class VillagerEntity
extends AbstractTraderEntity
implements InteractionObserver,
VillagerDataContainer {
    private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, new Item[]{Items.BEETROOT_SEEDS});
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
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, new MemoryModuleType[]{MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_LAST_SEEN_TIME});
    private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_LAST_SEEN);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> POINTS_OF_INTEREST = ImmutableMap.of(MemoryModuleType.HOME, (villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.HOME, MemoryModuleType.JOB_SITE, (villagerEntity, pointOfInterestType) -> villagerEntity.getVillagerData().getProfession().getWorkStation() == pointOfInterestType, MemoryModuleType.POTENTIAL_JOB_SITE, (villagerEntity, pointOfInterestType) -> PointOfInterestType.IS_USED_BY_PROFESSION.test((PointOfInterestType)pointOfInterestType), MemoryModuleType.MEETING_POINT, (villagerEntity, pointOfInterestType) -> pointOfInterestType == PointOfInterestType.MEETING);

    public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        this(entityType, world, VillagerType.PLAINS);
    }

    public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type) {
        super((EntityType<? extends AbstractTraderEntity>)entityType, world);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().withType(type).withProfession(VillagerProfession.NONE));
    }

    public Brain<VillagerEntity> getBrain() {
        return super.getBrain();
    }

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
            brain.setTaskList(Activity.PLAY, VillagerTaskListProvider.createPlayTasks(0.5f));
        } else {
            brain.setSchedule(Schedule.VILLAGER_DEFAULT);
            brain.setTaskList(Activity.WORK, VillagerTaskListProvider.createWorkTasks(villagerProfession, 0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
        }
        brain.setTaskList(Activity.CORE, VillagerTaskListProvider.createCoreTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.MEET, VillagerTaskListProvider.createMeetTasks(villagerProfession, 0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT)));
        brain.setTaskList(Activity.REST, VillagerTaskListProvider.createRestTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.IDLE, VillagerTaskListProvider.createIdleTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.PANIC, VillagerTaskListProvider.createPanicTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.PRE_RAID, VillagerTaskListProvider.createPreRaidTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.RAID, VillagerTaskListProvider.createRaidTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.HIDE, VillagerTaskListProvider.createHideTasks(villagerProfession, 0.5f));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.doExclusively(Activity.IDLE);
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
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    public boolean isNatural() {
        return this.natural;
    }

    @Override
    protected void mobTick() {
        Raid raid;
        this.world.getProfiler().push("villagerBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        if (this.natural) {
            this.natural = false;
        }
        if (!this.hasCustomer() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levelingUp) {
                    this.levelUp();
                    this.levelingUp = false;
                }
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastCustomer != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
            this.world.sendEntityStatus(this, (byte)14);
            this.lastCustomer = null;
        }
        if (!this.isAiDisabled() && this.random.nextInt(100) == 0 && (raid = ((ServerWorld)this.world).getRaidAt(this.getBlockPos())) != null && raid.isActive() && !raid.isFinished()) {
            this.world.sendEntityStatus(this, (byte)42);
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
        if (itemStack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return ActionResult.success(this.world.isClient);
            }
            boolean bl = this.getOffers().isEmpty();
            if (hand == Hand.MAIN_HAND) {
                if (bl && !this.world.isClient) {
                    this.sayNo();
                }
                player.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (bl) {
                return ActionResult.success(this.world.isClient);
            }
            if (!this.world.isClient && !this.offers.isEmpty()) {
                this.beginTradeWith(player);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(player, hand);
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
        this.updatePricesOnDemand();
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.resetUses();
        }
        this.lastRestockTime = this.world.getTime();
        ++this.restocksToday;
    }

    private boolean needRestock() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            if (!tradeOffer.method_21834()) continue;
            return true;
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
            long p = n / 24000L;
            long o = this.lastRestockCheckTime / 24000L;
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
        for (int j = 0; j < i; ++j) {
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
    public void writeCustomDataToTag(CompoundTag tag2) {
        super.writeCustomDataToTag(tag2);
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent(tag -> tag2.put("VillagerData", (Tag)tag));
        tag2.putByte("FoodLevel", this.foodLevel);
        tag2.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE).getValue());
        tag2.putInt("Xp", this.experience);
        tag2.putLong("LastRestock", this.lastRestockTime);
        tag2.putLong("LastGossipDecay", this.lastGossipDecayTime);
        tag2.putInt("RestocksToday", this.restocksToday);
        if (this.natural) {
            tag2.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("VillagerData", 10)) {
            DataResult dataResult = VillagerData.CODEC.parse(new Dynamic<Tag>(NbtOps.INSTANCE, tag.get("VillagerData")));
            dataResult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
        }
        if (tag.contains("Offers", 10)) {
            this.offers = new TraderOfferList(tag.getCompound("Offers"));
        }
        if (tag.contains("FoodLevel", 1)) {
            this.foodLevel = tag.getByte("FoodLevel");
        }
        ListTag listTag = tag.getList("Gossips", 10);
        this.gossip.deserialize(new Dynamic<ListTag>(NbtOps.INSTANCE, listTag));
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
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
        this.experience += offer.getTraderExperience();
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
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
            if (this.isAlive() && attacker instanceof PlayerEntity) {
                this.world.sendEntityStatus(this, (byte)13);
            }
        }
        super.setAttacker(attacker);
    }

    @Override
    public void onDeath(DamageSource source) {
        LOGGER.info("Villager {} died, message: '{}'", (Object)this, (Object)source.getDeathMessage(this).getString());
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
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        Optional<List<LivingEntity>> optional = this.brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
        if (!optional.isPresent()) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)this.world;
        optional.get().stream().filter(livingEntity -> livingEntity instanceof InteractionObserver).forEach(livingEntity -> serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver)((Object)livingEntity)));
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> memoryModuleType) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerWorld)this.world).getServer();
        this.brain.getOptionalMemory(memoryModuleType).ifPresent(globalPos -> {
            ServerWorld serverWorld = minecraftServer.getWorld(globalPos.getDimension());
            if (serverWorld == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
            Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(globalPos.getPos());
            BiPredicate<VillagerEntity, PointOfInterestType> biPredicate = POINTS_OF_INTEREST.get(memoryModuleType);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                pointOfInterestStorage.releaseTicket(globalPos.getPos());
                DebugInfoSender.sendPointOfInterest(serverWorld, globalPos.getPos());
            }
        });
    }

    @Override
    public boolean isReadyToBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && this.getBreedingAge() == 0;
    }

    private boolean lacksFood() {
        return this.foodLevel < 12;
    }

    private void consumeAvailableFood() {
        if (!this.lacksFood() || this.getAvailableFood() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().size(); ++i) {
            int j;
            Integer integer;
            ItemStack itemStack = this.getInventory().getStack(i);
            if (itemStack.isEmpty() || (integer = ITEM_FOOD_VALUES.get(itemStack.getItem())) == null) continue;
            for (int k = j = itemStack.getCount(); k > 0; --k) {
                this.foodLevel = (byte)(this.foodLevel + integer);
                this.getInventory().removeStack(i, 1);
                if (this.lacksFood()) continue;
                return;
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

    @Override
    @Environment(value=EnvType.CLIENT)
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

    @Override
    @Nullable
    public EntityData initialize(WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        if (spawnReason == SpawnReason.BREEDING) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }
        if (spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.DISPENSER) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(world.getBiome(this.getBlockPos()))));
        }
        if (spawnReason == SpawnReason.STRUCTURE) {
            this.natural = true;
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    @Override
    public VillagerEntity createChild(PassiveEntity passiveEntity) {
        double d = this.random.nextDouble();
        VillagerType villagerType = d < 0.5 ? VillagerType.forBiome(this.world.getBiome(this.getBlockPos())) : (d < 0.75 ? this.getVillagerData().getType() : ((VillagerEntity)passiveEntity).getVillagerData().getType());
        VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, this.world, villagerType);
        villagerEntity.initialize(this.world, this.world.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.BREEDING, null, null);
        return villagerEntity;
    }

    @Override
    public void onStruckByLightning(LightningEntity lightning) {
        if (this.world.getDifficulty() != Difficulty.PEACEFUL) {
            LOGGER.info("Villager {} was struck by lightning {}.", (Object)this, (Object)lightning);
            WitchEntity witchEntity = EntityType.WITCH.create(this.world);
            witchEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
            witchEntity.initialize(this.world, this.world.getLocalDifficulty(witchEntity.getBlockPos()), SpawnReason.CONVERSION, null, null);
            witchEntity.setAiDisabled(this.isAiDisabled());
            if (this.hasCustomName()) {
                witchEntity.setCustomName(this.getCustomName());
                witchEntity.setCustomNameVisible(this.isCustomNameVisible());
            }
            witchEntity.setPersistent();
            this.world.spawnEntity(witchEntity);
            this.remove();
        } else {
            super.onStruckByLightning(lightning);
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
        return (GATHERABLE_ITEMS.contains(item) || this.getVillagerData().getProfession().getGatherableItems().contains(item)) && this.getInventory().canInsert(stack);
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
        return this.getInventory().containsAny(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
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

    public void talkWithVillager(VillagerEntity villager, long time) {
        if (time >= this.gossipStartTime && time < this.gossipStartTime + 1200L || time >= villager.gossipStartTime && time < villager.gossipStartTime + 1200L) {
            return;
        }
        this.gossip.shareGossipFrom(villager.gossip, this.random, 10);
        this.gossipStartTime = time;
        villager.gossipStartTime = time;
        this.summonGolem(time, 5);
    }

    private void decayGossip() {
        long l = this.world.getTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = l;
            return;
        }
        if (l < this.lastGossipDecayTime + 24000L) {
            return;
        }
        this.gossip.decay();
        this.lastGossipDecayTime = l;
    }

    public void summonGolem(long time, int requiredVillagerCount) {
        if (!this.canSummonGolem(time)) {
            return;
        }
        Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
        List<VillagerEntity> list = this.world.getNonSpectatingEntities(VillagerEntity.class, box);
        List list2 = list.stream().filter(villagerEntity -> villagerEntity.canSummonGolem(time)).limit(5L).collect(Collectors.toList());
        if (list2.size() < requiredVillagerCount) {
            return;
        }
        IronGolemEntity ironGolemEntity = this.spawnIronGolem();
        if (ironGolemEntity == null) {
            return;
        }
        list.forEach(villagerEntity -> villagerEntity.setGolemLastSeenTime(time));
    }

    private void setGolemLastSeenTime(long time) {
        this.brain.remember(MemoryModuleType.GOLEM_LAST_SEEN_TIME, time);
    }

    private boolean hasSeenGolemRecently(long currentTime) {
        Optional<Long> optional = this.brain.getOptionalMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);
        if (!optional.isPresent()) {
            return false;
        }
        Long long_ = optional.get();
        return currentTime - long_ <= 600L;
    }

    public boolean canSummonGolem(long time) {
        if (!this.hasRecentlyWorkedAndSlept(this.world.getTime())) {
            return false;
        }
        return !this.hasSeenGolemRecently(time);
    }

    @Nullable
    private IronGolemEntity spawnIronGolem() {
        BlockPos blockPos = this.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            IronGolemEntity ironGolemEntity;
            double e;
            double d = this.world.random.nextInt(16) - 8;
            BlockPos blockPos2 = this.method_30023(blockPos, d, e = (double)(this.world.random.nextInt(16) - 8));
            if (blockPos2 == null || (ironGolemEntity = EntityType.IRON_GOLEM.create(this.world, null, null, null, blockPos2, SpawnReason.MOB_SUMMONED, false, false)) == null) continue;
            if (ironGolemEntity.canSpawn(this.world, SpawnReason.MOB_SUMMONED) && ironGolemEntity.canSpawn(this.world)) {
                this.world.spawnEntity(ironGolemEntity);
                return ironGolemEntity;
            }
            ironGolemEntity.remove();
        }
        return null;
    }

    @Nullable
    private BlockPos method_30023(BlockPos blockPos, double d, double e) {
        int i = 6;
        BlockPos blockPos2 = blockPos.add(d, 6.0, e);
        BlockState blockState = this.world.getBlockState(blockPos2);
        for (int j = 6; j >= -6; --j) {
            BlockPos blockPos3 = blockPos2;
            BlockState blockState2 = blockState;
            blockPos2 = blockPos3.down();
            blockState = this.world.getBlockState(blockPos2);
            if (!blockState2.isAir() && !blockState2.getMaterial().isLiquid() || !blockState.getMaterial().blocksLight()) continue;
            return blockPos3;
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

    public VillagerGossips getGossip() {
        return this.gossip;
    }

    public void setGossipDataFromTag(Tag tag) {
        this.gossip.deserialize(new Dynamic<Tag>(NbtOps.INSTANCE, tag));
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void sleep(BlockPos pos) {
        super.sleep(pos);
        this.brain.remember(MemoryModuleType.LAST_SLEPT, this.world.getTime());
        this.brain.forget(MemoryModuleType.WALK_TARGET);
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void wakeUp() {
        super.wakeUp();
        this.brain.remember(MemoryModuleType.LAST_WOKEN, this.world.getTime());
    }

    private boolean hasRecentlyWorkedAndSlept(long worldTime) {
        Optional<Long> optional = this.brain.getOptionalMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent()) {
            return worldTime - optional.get() < 24000L;
        }
        return false;
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity mate) {
        return this.createChild(mate);
    }
}

