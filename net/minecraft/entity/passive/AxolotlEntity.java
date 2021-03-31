/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticLookControl;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AxolotlBrain;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class AxolotlEntity
extends AnimalEntity
implements Bucketable {
    public static final int field_30388 = 200;
    public static final Predicate<LivingEntity> AXOLOTL_NOT_PLAYING_DEAD = entity -> entity.getType() == EntityType.AXOLOTL && !((AxolotlEntity)entity).isPlayingDead();
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super AxolotlEntity>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, new MemoryModuleType[]{MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN});
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> PLAYING_DEAD = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final double field_30389 = 20.0;
    public static final int field_30390 = 1200;
    private static final int field_30392 = 6000;
    public static final String field_30391 = "Variant";
    private static final int field_30393 = 100;

    public AxolotlEntity(EntityType<? extends AxolotlEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.moveControl = new AxolotlMoveControl(this);
        this.lookControl = new AxolotlLookControl(this, 20);
        this.stepHeight = 1.0f;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0f;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(PLAYING_DEAD, false);
        this.dataTracker.startTracking(FROM_BUCKET, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(field_30391, this.getVariant().getId());
        nbt.putBoolean("FromBucket", this.isFromBucket());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(Variant.VARIANTS[nbt.getInt(field_30391)]);
        this.setFromBucket(nbt.getBoolean("FromBucket"));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        boolean bl = false;
        if (spawnReason == SpawnReason.BUCKET) {
            return entityData;
        }
        if (entityData instanceof AxolotlData) {
            if (((AxolotlData)entityData).getSpawnedCount() >= 2) {
                bl = true;
            }
        } else {
            entityData = new AxolotlData(Variant.getRandomAll(this.world.random), Variant.getRandomAll(this.world.random));
        }
        this.setVariant(((AxolotlData)entityData).getRandomVariant(this.world.random));
        if (bl) {
            this.setBreedingAge(-24000);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void baseTick() {
        int i = this.getAir();
        super.baseTick();
        if (!this.isAiDisabled()) {
            this.tickAir(i);
        }
    }

    protected void tickAir(int air) {
        if (this.isAlive() && !this.isWet()) {
            this.setAir(air - 1);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.damage(DamageSource.DRYOUT, 2.0f);
            }
        } else {
            this.setAir(this.getMaxAir());
        }
    }

    public boolean method_35176() {
        return this.getAir() < this.getMaxAir();
    }

    @Override
    public int getMaxAir() {
        return 6000;
    }

    public Variant getVariant() {
        return Variant.VARIANTS[this.dataTracker.get(VARIANT)];
    }

    private void setVariant(Variant variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    private static boolean shouldBabyBeDifferent(Random random) {
        return random.nextInt(1200) == 0;
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return world.intersectsEntities(this);
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    public void setPlayingDead(boolean playingDead) {
        this.dataTracker.set(PLAYING_DEAD, playingDead);
    }

    public boolean isPlayingDead() {
        return this.dataTracker.get(PLAYING_DEAD);
    }

    @Override
    public boolean isFromBucket() {
        return this.dataTracker.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.dataTracker.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public double getAttackDistanceScalingFactor(@Nullable Entity entity) {
        return this.isPlayingDead() ? 0.0 : super.getAttackDistanceScalingFactor(entity);
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        AxolotlEntity axolotlEntity = EntityType.AXOLOTL.create(world);
        if (axolotlEntity != null) {
            Variant variant = AxolotlEntity.shouldBabyBeDifferent(this.random) ? Variant.getRandomNatural(this.random) : (this.random.nextBoolean() ? this.getVariant() : ((AxolotlEntity)entity).getVariant());
            axolotlEntity.setVariant(variant);
        }
        return axolotlEntity;
    }

    @Override
    public double method_33191(LivingEntity livingEntity) {
        return 1.5 + (double)livingEntity.getWidth() * 2.0;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return ItemTags.AXOLOTL_TEMPT_ITEMS.contains(stack.getItem());
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return true;
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("axolotlBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("axolotlActivityUpdate");
        AxolotlBrain.method_33244(this);
        this.world.getProfiler().pop();
        if (!this.isAiDisabled()) {
            Optional<Integer> optional = this.getBrain().getOptionalMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            this.setPlayingDead(optional.isPresent() && optional.get() > 0);
        }
    }

    public static DefaultAttributeContainer.Builder createAxolotlAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new AxolotlSwimNavigation(this, world);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = target.damage(DamageSource.mob(this), (int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.dealDamage(this, target);
            this.playSound(SoundEvents.ENTITY_AXOLOTL_ATTACK, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        float f = this.getHealth();
        if (!this.world.isClient && !this.isAiDisabled() && this.world.random.nextInt(3) == 0 && ((float)this.world.random.nextInt(3) < amount || f / this.getMaxHealth() < 0.5f) && amount < f && source != DamageSource.DRYOUT && !this.isPlayingDead()) {
            this.brain.remember(MemoryModuleType.PLAY_DEAD_TICKS, 200);
        }
        return super.damage(source, amount);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.275f;
    }

    @Override
    public int getLookPitchSpeed() {
        return 1;
    }

    @Override
    public int getBodyYawSpeed() {
        return 1;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
        NbtCompound nbtCompound = stack.getOrCreateTag();
        nbtCompound.putInt(field_30391, this.getVariant().getId());
        nbtCompound.putInt("Age", this.getBreedingAge());
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
        this.setVariant(Variant.VARIANTS[nbt.getInt(field_30391)]);
        if (nbt.contains("Age")) {
            this.setBreedingAge(nbt.getInt("Age"));
        }
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(Items.AXOLOTL_BUCKET);
    }

    @Override
    public SoundEvent getBucketedSound() {
        return SoundEvents.ITEM_BUCKET_FILL_AXOLOTL;
    }

    @Override
    public boolean canTakeDamage() {
        return !this.isPlayingDead() && super.canTakeDamage();
    }

    public static void method_35175(AxolotlEntity axolotlEntity) {
        Entity entity;
        DamageSource damageSource;
        Optional<LivingEntity> optional = axolotlEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        if (!optional.isPresent()) {
            return;
        }
        World world = axolotlEntity.world;
        LivingEntity livingEntity = optional.get();
        if (livingEntity.isDead() && (damageSource = livingEntity.getRecentDamageSource()) != null && (entity = damageSource.getAttacker()) != null && entity.getType() == EntityType.PLAYER) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, axolotlEntity.getBoundingBox().expand(20.0));
            if (list.contains(playerEntity)) {
                AxolotlEntity.buffPlayer(playerEntity);
            }
        }
    }

    public static void buffPlayer(PlayerEntity playerEntity) {
        StatusEffectInstance statusEffectInstance = playerEntity.getStatusEffect(StatusEffects.REGENERATION);
        int i = 100 + (statusEffectInstance != null ? statusEffectInstance.getDuration() : 0);
        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, i, 0));
        playerEntity.removeStatusEffect(StatusEffects.MINING_FATIGUE);
    }

    @Override
    public boolean cannotDespawn() {
        return super.cannotDespawn() || this.isFromBucket();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_AXOLOTL_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_AXOLOTL_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isTouchingWater() ? SoundEvents.ENTITY_AXOLOTL_IDLE_WATER : SoundEvents.ENTITY_AXOLOTL_IDLE_AIR;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_AXOLOTL_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_AXOLOTL_SWIM;
    }

    protected Brain.Profile<AxolotlEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return AxolotlBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<AxolotlEntity> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
        if (stack.isOf(Items.TROPICAL_FISH_BUCKET)) {
            player.setStackInHand(hand, BucketItem.getEmptiedStack(stack, player));
        } else {
            super.eat(player, hand, stack);
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    static class AxolotlSwimNavigation
    extends SwimNavigation {
        AxolotlSwimNavigation(AxolotlEntity axolotl, World world) {
            super(axolotl, world);
        }

        @Override
        protected boolean isAtValidPosition() {
            return true;
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new AmphibiousPathNodeMaker(false);
            return new PathNodeNavigator(this.nodeMaker, range);
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            return !this.world.getBlockState(pos.down()).isAir();
        }
    }

    public static class AxolotlData
    extends PassiveEntity.PassiveData {
        public final Variant[] variants;

        public AxolotlData(Variant ... variants) {
            super(false);
            this.variants = variants;
        }

        public Variant getRandomVariant(Random random) {
            if (AxolotlEntity.shouldBabyBeDifferent(random)) {
                return Variant.getRandomNatural(random);
            }
            return this.variants[random.nextInt(this.variants.length)];
        }
    }

    class AxolotlLookControl
    extends AquaticLookControl {
        public AxolotlLookControl(AxolotlEntity axolotl, int maxYawDifference) {
            super(axolotl, maxYawDifference);
        }

        @Override
        public void tick() {
            if (!AxolotlEntity.this.isPlayingDead()) {
                super.tick();
            }
        }
    }

    static class AxolotlMoveControl
    extends AquaticMoveControl {
        private final AxolotlEntity axolotl;

        public AxolotlMoveControl(AxolotlEntity axolotl) {
            super(axolotl, 85, 10, 0.1f, 0.5f, false);
            this.axolotl = axolotl;
        }

        @Override
        public void tick() {
            if (!this.axolotl.isPlayingDead()) {
                super.tick();
            }
        }
    }

    public static enum Variant {
        LUCY(0, "lucy", true),
        WILD(1, "wild", true),
        GOLD(2, "gold", true),
        CYAN(3, "cyan", true),
        BLUE(4, "blue", false);

        public static final Variant[] VARIANTS;
        private final int id;
        private final String name;
        private final boolean natural;

        private Variant(int id, String name, boolean natural) {
            this.id = id;
            this.name = name;
            this.natural = natural;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static Variant getRandomAll(Random random) {
            return Variant.getRandom(random, true);
        }

        public static Variant getRandomNatural(Random random) {
            return Variant.getRandom(random, false);
        }

        private static Variant getRandom(Random random, boolean includeUnnatural) {
            Variant[] variants = (Variant[])Arrays.stream(VARIANTS).filter(variant -> variant.natural == includeUnnatural).toArray(Variant[]::new);
            return Util.getRandom(variants, random);
        }

        static {
            VARIANTS = (Variant[])Arrays.stream(Variant.values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        }
    }
}

