/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WolfEntity
extends TameableEntity {
    private static final TrackedData<Float> WOLF_HEALTH = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> BEGGING = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final Predicate<LivingEntity> FOLLOW_TAMED_PREDICATE = livingEntity -> {
        EntityType<?> entityType = livingEntity.getType();
        return entityType == EntityType.SHEEP || entityType == EntityType.RABBIT || entityType == EntityType.FOX;
    };
    private float begAnimationProgress;
    private float lastBegAnimationProgress;
    private boolean wet;
    private boolean canShakeWaterOff;
    private float shakeProgress;
    private float lastShakeProgress;

    public WolfEntity(EntityType<? extends WolfEntity> entityType, World world) {
        super((EntityType<? extends TameableEntity>)entityType, world);
        this.setTamed(false);
    }

    @Override
    protected void initGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, this.sitGoal);
        this.goalSelector.add(3, new AvoidLlamaGoal<LlamaEntity>(this, LlamaEntity.class, 24.0f, 1.5, 1.5));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(9, new WolfBegGoal(this, 8.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(4, new FollowTargetIfTamedGoal<AnimalEntity>(this, AnimalEntity.class, false, FOLLOW_TAMED_PREDICATE));
        this.targetSelector.add(4, new FollowTargetIfTamedGoal<TurtleEntity>(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
        this.targetSelector.add(5, new FollowTargetGoal<AbstractSkeletonEntity>((MobEntity)this, AbstractSkeletonEntity.class, false));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3f);
        if (this.isTamed()) {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
        } else {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        }
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }

    @Override
    public void setTarget(@Nullable LivingEntity livingEntity) {
        super.setTarget(livingEntity);
        if (livingEntity == null) {
            this.setAngry(false);
        } else if (!this.isTamed()) {
            this.setAngry(true);
        }
    }

    @Override
    protected void mobTick() {
        this.dataTracker.set(WOLF_HEALTH, Float.valueOf(this.getHealth()));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(WOLF_HEALTH, Float.valueOf(this.getHealth()));
        this.dataTracker.startTracking(BEGGING, false);
        this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.RED.getId());
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putBoolean("Angry", this.isAngry());
        compoundTag.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        this.setAngry(compoundTag.getBoolean("Angry"));
        if (compoundTag.containsKey("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(compoundTag.getInt("CollarColor")));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isAngry()) {
            return SoundEvents.ENTITY_WOLF_GROWL;
        }
        if (this.random.nextInt(3) == 0) {
            if (this.isTamed() && this.dataTracker.get(WOLF_HEALTH).floatValue() < 10.0f) {
                return SoundEvents.ENTITY_WOLF_WHINE;
            }
            return SoundEvents.ENTITY_WOLF_PANT;
        }
        return SoundEvents.ENTITY_WOLF_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient && this.wet && !this.canShakeWaterOff && !this.isNavigating() && this.onGround) {
            this.canShakeWaterOff = true;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
            this.world.sendEntityStatus(this, (byte)8);
        }
        if (!this.world.isClient && this.getTarget() == null && this.isAngry()) {
            this.setAngry(false);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        this.lastBegAnimationProgress = this.begAnimationProgress;
        this.begAnimationProgress = this.isBegging() ? (this.begAnimationProgress += (1.0f - this.begAnimationProgress) * 0.4f) : (this.begAnimationProgress += (0.0f - this.begAnimationProgress) * 0.4f);
        if (this.isTouchingWater()) {
            this.wet = true;
            this.canShakeWaterOff = false;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
        } else if ((this.wet || this.canShakeWaterOff) && this.canShakeWaterOff) {
            if (this.shakeProgress == 0.0f) {
                this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            }
            this.lastShakeProgress = this.shakeProgress;
            this.shakeProgress += 0.05f;
            if (this.lastShakeProgress >= 2.0f) {
                this.wet = false;
                this.canShakeWaterOff = false;
                this.lastShakeProgress = 0.0f;
                this.shakeProgress = 0.0f;
            }
            if (this.shakeProgress > 0.4f) {
                float f = (float)this.getBoundingBox().minY;
                int i = (int)(MathHelper.sin((this.shakeProgress - 0.4f) * (float)Math.PI) * 7.0f);
                Vec3d vec3d = this.getVelocity();
                for (int j = 0; j < i; ++j) {
                    float g = (this.random.nextFloat() * 2.0f - 1.0f) * this.getWidth() * 0.5f;
                    float h = (this.random.nextFloat() * 2.0f - 1.0f) * this.getWidth() * 0.5f;
                    this.world.addParticle(ParticleTypes.SPLASH, this.x + (double)g, f + 0.8f, this.z + (double)h, vec3d.x, vec3d.y, vec3d.z);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.wet = false;
        this.canShakeWaterOff = false;
        this.lastShakeProgress = 0.0f;
        this.shakeProgress = 0.0f;
        super.onDeath(damageSource);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isWet() {
        return this.wet;
    }

    @Environment(value=EnvType.CLIENT)
    public float getWetBrightnessMultiplier(float f) {
        return 0.75f + MathHelper.lerp(f, this.lastShakeProgress, this.shakeProgress) / 2.0f * 0.25f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getShakeAnimationProgress(float f, float g) {
        float h = (MathHelper.lerp(f, this.lastShakeProgress, this.shakeProgress) + g) / 1.8f;
        if (h < 0.0f) {
            h = 0.0f;
        } else if (h > 1.0f) {
            h = 1.0f;
        }
        return MathHelper.sin(h * (float)Math.PI) * MathHelper.sin(h * (float)Math.PI * 11.0f) * 0.15f * (float)Math.PI;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBegAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastBegAnimationProgress, this.begAnimationProgress) * 0.15f * (float)Math.PI;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
        return entitySize.height * 0.8f;
    }

    @Override
    public int getLookPitchSpeed() {
        if (this.isSitting()) {
            return 20;
        }
        return super.getLookPitchSpeed();
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        Entity entity = damageSource.getAttacker();
        if (this.sitGoal != null) {
            this.sitGoal.setEnabledWithOwner(false);
        }
        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ProjectileEntity)) {
            f = (f + 1.0f) / 2.0f;
        }
        return super.damage(damageSource, f);
    }

    @Override
    public boolean tryAttack(Entity entity) {
        boolean bl = entity.damage(DamageSource.mob(this), (int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
        if (bl) {
            this.dealDamage(this, entity);
        }
        return bl;
    }

    @Override
    public void setTamed(boolean bl) {
        super.setTamed(bl);
        if (bl) {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
        } else {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        }
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }

    @Override
    public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (this.isTamed()) {
            if (!itemStack.isEmpty()) {
                DyeColor dyeColor;
                if (item.isFood()) {
                    if (item.getFoodSetting().isWolfFood() && this.dataTracker.get(WOLF_HEALTH).floatValue() < 20.0f) {
                        if (!playerEntity.abilities.creativeMode) {
                            itemStack.subtractAmount(1);
                        }
                        this.heal(item.getFoodSetting().getHunger());
                        return true;
                    }
                } else if (item instanceof DyeItem && (dyeColor = ((DyeItem)item).getColor()) != this.getCollarColor()) {
                    this.setCollarColor(dyeColor);
                    if (!playerEntity.abilities.creativeMode) {
                        itemStack.subtractAmount(1);
                    }
                    return true;
                }
            }
            if (this.isOwner(playerEntity) && !this.world.isClient && !this.isBreedingItem(itemStack)) {
                this.sitGoal.setEnabledWithOwner(!this.isSitting());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
            }
        } else if (item == Items.BONE && !this.isAngry()) {
            if (!playerEntity.abilities.creativeMode) {
                itemStack.subtractAmount(1);
            }
            if (!this.world.isClient) {
                if (this.random.nextInt(3) == 0) {
                    this.setOwner(playerEntity);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.sitGoal.setEnabledWithOwner(true);
                    this.setHealth(20.0f);
                    this.showEmoteParticle(true);
                    this.world.sendEntityStatus(this, (byte)7);
                } else {
                    this.showEmoteParticle(false);
                    this.world.sendEntityStatus(this, (byte)6);
                }
            }
            return true;
        }
        return super.interactMob(playerEntity, hand);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 8) {
            this.canShakeWaterOff = true;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
        } else {
            super.handleStatus(b);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float method_6714() {
        if (this.isAngry()) {
            return 1.5393804f;
        }
        if (this.isTamed()) {
            return (0.55f - (this.getHealthMaximum() - this.dataTracker.get(WOLF_HEALTH).floatValue()) * 0.02f) * (float)Math.PI;
        }
        return 0.62831855f;
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item.isFood() && item.getFoodSetting().isWolfFood();
    }

    @Override
    public int getLimitPerChunk() {
        return 8;
    }

    public boolean isAngry() {
        return ((Byte)this.dataTracker.get(TAMEABLE_FLAGS) & 2) != 0;
    }

    public void setAngry(boolean bl) {
        byte b = (Byte)this.dataTracker.get(TAMEABLE_FLAGS);
        if (bl) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 2));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & 0xFFFFFFFD));
        }
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor dyeColor) {
        this.dataTracker.set(COLLAR_COLOR, dyeColor.getId());
    }

    public WolfEntity method_6717(PassiveEntity passiveEntity) {
        WolfEntity wolfEntity = EntityType.WOLF.create(this.world);
        UUID uUID = this.getOwnerUuid();
        if (uUID != null) {
            wolfEntity.setOwnerUuid(uUID);
            wolfEntity.setTamed(true);
        }
        return wolfEntity;
    }

    public void setBegging(boolean bl) {
        this.dataTracker.set(BEGGING, bl);
    }

    @Override
    public boolean canBreedWith(AnimalEntity animalEntity) {
        if (animalEntity == this) {
            return false;
        }
        if (!this.isTamed()) {
            return false;
        }
        if (!(animalEntity instanceof WolfEntity)) {
            return false;
        }
        WolfEntity wolfEntity = (WolfEntity)animalEntity;
        if (!wolfEntity.isTamed()) {
            return false;
        }
        if (wolfEntity.isSitting()) {
            return false;
        }
        return this.isInLove() && wolfEntity.isInLove();
    }

    public boolean isBegging() {
        return this.dataTracker.get(BEGGING);
    }

    @Override
    public boolean canAttackWithOwner(LivingEntity livingEntity, LivingEntity livingEntity2) {
        WolfEntity wolfEntity;
        if (livingEntity instanceof CreeperEntity || livingEntity instanceof GhastEntity) {
            return false;
        }
        if (livingEntity instanceof WolfEntity && (wolfEntity = (WolfEntity)livingEntity).isTamed() && wolfEntity.getOwner() == livingEntity2) {
            return false;
        }
        if (livingEntity instanceof PlayerEntity && livingEntity2 instanceof PlayerEntity && !((PlayerEntity)livingEntity2).shouldDamagePlayer((PlayerEntity)livingEntity)) {
            return false;
        }
        if (livingEntity instanceof HorseBaseEntity && ((HorseBaseEntity)livingEntity).isTame()) {
            return false;
        }
        return !(livingEntity instanceof CatEntity) || !((CatEntity)livingEntity).isTamed();
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity playerEntity) {
        return !this.isAngry() && super.canBeLeashedBy(playerEntity);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity passiveEntity) {
        return this.method_6717(passiveEntity);
    }

    class AvoidLlamaGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final WolfEntity wolf;

        public AvoidLlamaGoal(WolfEntity wolfEntity2, Class<T> class_, float f, double d, double e) {
            super(wolfEntity2, class_, f, d, e);
            this.wolf = wolfEntity2;
        }

        @Override
        public boolean canStart() {
            if (super.canStart() && this.targetEntity instanceof LlamaEntity) {
                return !this.wolf.isTamed() && this.isScaredOf((LlamaEntity)this.targetEntity);
            }
            return false;
        }

        private boolean isScaredOf(LlamaEntity llamaEntity) {
            return llamaEntity.getStrength() >= WolfEntity.this.random.nextInt(5);
        }

        @Override
        public void start() {
            WolfEntity.this.setTarget(null);
            super.start();
        }

        @Override
        public void tick() {
            WolfEntity.this.setTarget(null);
            super.tick();
        }
    }
}

