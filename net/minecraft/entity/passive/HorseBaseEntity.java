/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class HorseBaseEntity
extends AnimalEntity
implements InventoryListener,
JumpingMount {
    private static final Predicate<LivingEntity> IS_BRED_HORSE = livingEntity -> livingEntity instanceof HorseBaseEntity && ((HorseBaseEntity)livingEntity).isBred();
    private static final TargetPredicate PARENT_HORSE_PREDICATE = new TargetPredicate().setBaseMaxDistance(16.0).includeInvulnerable().includeTeammates().includeHidden().setPredicate(IS_BRED_HORSE);
    protected static final EntityAttribute JUMP_STRENGTH = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0).setName("Jump Strength").setTracked(true);
    private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private int eatingGrassTicks;
    private int eatingTicks;
    private int angryTicks;
    public int field_6957;
    public int field_6958;
    protected boolean inAir;
    protected BasicInventory items;
    protected int temper;
    protected float jumpStrength;
    private boolean jumping;
    private float eatingGrassAnimationProgress;
    private float lastEatingGrassAnimationProgress;
    private float angryAnimationProgress;
    private float lastAngryAnimationProgress;
    private float eatingAnimationProgress;
    private float lastEatingAnimationProgress;
    protected boolean field_6964 = true;
    protected int soundTicks;

    protected HorseBaseEntity(EntityType<? extends HorseBaseEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.stepHeight = 1.0f;
        this.method_6721();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, HorseBaseEntity.class));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HORSE_FLAGS, (byte)0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    protected boolean getHorseFlag(int i) {
        return (this.dataTracker.get(HORSE_FLAGS) & i) != 0;
    }

    protected void setHorseFlag(int i, boolean bl) {
        byte b = this.dataTracker.get(HORSE_FLAGS);
        if (bl) {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b | i));
        } else {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b & ~i));
        }
    }

    public boolean isTame() {
        return this.getHorseFlag(2);
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uUID) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uUID));
    }

    public boolean isInAir() {
        return this.inAir;
    }

    public void setTame(boolean bl) {
        this.setHorseFlag(2, bl);
    }

    public void setInAir(boolean bl) {
        this.inAir = bl;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity playerEntity) {
        return super.canBeLeashedBy(playerEntity) && this.getGroup() != EntityGroup.UNDEAD;
    }

    @Override
    protected void updateForLeashLength(float f) {
        if (f > 6.0f && this.isEatingGrass()) {
            this.setEatingGrass(false);
        }
    }

    public boolean isEatingGrass() {
        return this.getHorseFlag(16);
    }

    public boolean isAngry() {
        return this.getHorseFlag(32);
    }

    public boolean isBred() {
        return this.getHorseFlag(8);
    }

    public void setBred(boolean bl) {
        this.setHorseFlag(8, bl);
    }

    public void setSaddled(boolean bl) {
        this.setHorseFlag(4, bl);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int i) {
        this.temper = i;
    }

    public int addTemper(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxTemper());
        this.setTemper(j);
        return j;
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        Entity entity = damageSource.getAttacker();
        if (this.hasPassengers() && entity != null && this.hasPassengerDeep(entity)) {
            return false;
        }
        return super.damage(damageSource, f);
    }

    @Override
    public boolean isPushable() {
        return !this.hasPassengers();
    }

    private void playEatingAnimation() {
        this.setEating();
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        int i;
        if (f > 1.0f) {
            this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4f, 1.0f);
        }
        if ((i = this.method_23329(f, g)) <= 0) {
            return false;
        }
        this.damage(DamageSource.FALL, i);
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengersDeep()) {
                entity.damage(DamageSource.FALL, i);
            }
        }
        this.method_23328();
        return true;
    }

    @Override
    protected int method_23329(float f, float g) {
        return MathHelper.ceil((f * 0.5f - 3.0f) * g);
    }

    protected int getInventorySize() {
        return 2;
    }

    protected void method_6721() {
        BasicInventory basicInventory = this.items;
        this.items = new BasicInventory(this.getInventorySize());
        if (basicInventory != null) {
            basicInventory.removeListener(this);
            int i = Math.min(basicInventory.getInvSize(), this.items.getInvSize());
            for (int j = 0; j < i; ++j) {
                ItemStack itemStack = basicInventory.getInvStack(j);
                if (itemStack.isEmpty()) continue;
                this.items.setInvStack(j, itemStack.copy());
            }
        }
        this.items.addListener(this);
        this.updateSaddle();
    }

    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        this.setSaddled(!this.items.getInvStack(0).isEmpty() && this.canBeSaddled());
    }

    @Override
    public void onInvChange(Inventory inventory) {
        boolean bl = this.isSaddled();
        this.updateSaddle();
        if (this.age > 20 && !bl && this.isSaddled()) {
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5f, 1.0f);
        }
    }

    public double getJumpStrength() {
        return this.getAttributeInstance(JUMP_STRENGTH).getValue();
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.random.nextInt(3) == 0) {
            this.updateAnger();
        }
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
            this.updateAnger();
        }
        return null;
    }

    public boolean canBeSaddled() {
        return true;
    }

    public boolean isSaddled() {
        return this.getHorseFlag(4);
    }

    @Nullable
    protected SoundEvent getAngrySound() {
        this.updateAnger();
        return null;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (blockState.getMaterial().isLiquid()) {
            return;
        }
        BlockState blockState2 = this.world.getBlockState(blockPos.up());
        BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
        if (blockState2.getBlock() == Blocks.SNOW) {
            blockSoundGroup = blockState2.getSoundGroup();
        }
        if (this.hasPassengers() && this.field_6964) {
            ++this.soundTicks;
            if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
                this.playWalkSound(blockSoundGroup);
            } else if (this.soundTicks <= 5) {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
            }
        } else if (blockSoundGroup == BlockSoundGroup.WOOD) {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
        } else {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP, blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
        }
    }

    protected void playWalkSound(BlockSoundGroup blockSoundGroup) {
        this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributes().register(JUMP_STRENGTH);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(53.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.225f);
    }

    @Override
    public int getLimitPerChunk() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 400;
    }

    public void openInventory(PlayerEntity playerEntity) {
        if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(playerEntity)) && this.isTame()) {
            playerEntity.openHorseInventory(this, this.items);
        }
    }

    protected boolean receiveFood(PlayerEntity playerEntity, ItemStack itemStack) {
        boolean bl = false;
        float f = 0.0f;
        int i = 0;
        int j = 0;
        Item item = itemStack.getItem();
        if (item == Items.WHEAT) {
            f = 2.0f;
            i = 20;
            j = 3;
        } else if (item == Items.SUGAR) {
            f = 1.0f;
            i = 30;
            j = 3;
        } else if (item == Blocks.HAY_BLOCK.asItem()) {
            f = 20.0f;
            i = 180;
        } else if (item == Items.APPLE) {
            f = 3.0f;
            i = 60;
            j = 3;
        } else if (item == Items.GOLDEN_CARROT) {
            f = 4.0f;
            i = 60;
            j = 5;
            if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(playerEntity);
            }
        } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
            f = 10.0f;
            i = 240;
            j = 10;
            if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(playerEntity);
            }
        }
        if (this.getHealth() < this.getMaximumHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.method_23322(1.0), this.method_23319() + 0.5, this.method_23325(1.0), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(i);
            }
            bl = true;
        }
        if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            bl = true;
            if (!this.world.isClient) {
                this.addTemper(j);
            }
        }
        if (bl) {
            this.playEatingAnimation();
        }
        return bl;
    }

    protected void putPlayerOnBack(PlayerEntity playerEntity) {
        this.setEatingGrass(false);
        this.setAngry(false);
        if (!this.world.isClient) {
            playerEntity.yaw = this.yaw;
            playerEntity.pitch = this.pitch;
            playerEntity.startRiding(this);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return false;
    }

    private void method_6759() {
        this.field_6957 = 1;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.items == null) {
            return;
        }
        for (int i = 0; i < this.items.getInvSize(); ++i) {
            ItemStack itemStack = this.items.getInvStack(i);
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack)) continue;
            this.dropStack(itemStack);
        }
    }

    @Override
    public void tickMovement() {
        if (this.random.nextInt(200) == 0) {
            this.method_6759();
        }
        super.tickMovement();
        if (this.world.isClient || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.eatsGrass()) {
            if (!this.isEatingGrass() && !this.hasPassengers() && this.random.nextInt(300) == 0 && this.world.getBlockState(new BlockPos(this).method_10074()).getBlock() == Blocks.GRASS_BLOCK) {
                this.setEatingGrass(true);
            }
            if (this.isEatingGrass() && ++this.eatingGrassTicks > 50) {
                this.eatingGrassTicks = 0;
                this.setEatingGrass(false);
            }
        }
        this.walkToParent();
    }

    protected void walkToParent() {
        HorseBaseEntity livingEntity;
        if (this.isBred() && this.isBaby() && !this.isEatingGrass() && (livingEntity = this.world.getClosestEntity(HorseBaseEntity.class, PARENT_HORSE_PREDICATE, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().expand(16.0))) != null && this.squaredDistanceTo(livingEntity) > 4.0) {
            this.navigation.findPathTo(livingEntity, 0);
        }
    }

    public boolean eatsGrass() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eatingTicks > 0 && ++this.eatingTicks > 30) {
            this.eatingTicks = 0;
            this.setHorseFlag(64, false);
        }
        if ((this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) && this.angryTicks > 0 && ++this.angryTicks > 20) {
            this.angryTicks = 0;
            this.setAngry(false);
        }
        if (this.field_6957 > 0 && ++this.field_6957 > 8) {
            this.field_6957 = 0;
        }
        if (this.field_6958 > 0) {
            ++this.field_6958;
            if (this.field_6958 > 300) {
                this.field_6958 = 0;
            }
        }
        this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
        if (this.isEatingGrass()) {
            this.eatingGrassAnimationProgress += (1.0f - this.eatingGrassAnimationProgress) * 0.4f + 0.05f;
            if (this.eatingGrassAnimationProgress > 1.0f) {
                this.eatingGrassAnimationProgress = 1.0f;
            }
        } else {
            this.eatingGrassAnimationProgress += (0.0f - this.eatingGrassAnimationProgress) * 0.4f - 0.05f;
            if (this.eatingGrassAnimationProgress < 0.0f) {
                this.eatingGrassAnimationProgress = 0.0f;
            }
        }
        this.lastAngryAnimationProgress = this.angryAnimationProgress;
        if (this.isAngry()) {
            this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress = 0.0f;
            this.angryAnimationProgress += (1.0f - this.angryAnimationProgress) * 0.4f + 0.05f;
            if (this.angryAnimationProgress > 1.0f) {
                this.angryAnimationProgress = 1.0f;
            }
        } else {
            this.jumping = false;
            this.angryAnimationProgress += (0.8f * this.angryAnimationProgress * this.angryAnimationProgress * this.angryAnimationProgress - this.angryAnimationProgress) * 0.6f - 0.05f;
            if (this.angryAnimationProgress < 0.0f) {
                this.angryAnimationProgress = 0.0f;
            }
        }
        this.lastEatingAnimationProgress = this.eatingAnimationProgress;
        if (this.getHorseFlag(64)) {
            this.eatingAnimationProgress += (1.0f - this.eatingAnimationProgress) * 0.7f + 0.05f;
            if (this.eatingAnimationProgress > 1.0f) {
                this.eatingAnimationProgress = 1.0f;
            }
        } else {
            this.eatingAnimationProgress += (0.0f - this.eatingAnimationProgress) * 0.7f - 0.05f;
            if (this.eatingAnimationProgress < 0.0f) {
                this.eatingAnimationProgress = 0.0f;
            }
        }
    }

    private void setEating() {
        if (!this.world.isClient) {
            this.eatingTicks = 1;
            this.setHorseFlag(64, true);
        }
    }

    public void setEatingGrass(boolean bl) {
        this.setHorseFlag(16, bl);
    }

    public void setAngry(boolean bl) {
        if (bl) {
            this.setEatingGrass(false);
        }
        this.setHorseFlag(32, bl);
    }

    private void updateAnger() {
        if (this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) {
            this.angryTicks = 1;
            this.setAngry(true);
        }
    }

    public void playAngrySound() {
        this.updateAnger();
        SoundEvent soundEvent = this.getAngrySound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    public boolean bondWithPlayer(PlayerEntity playerEntity) {
        this.setOwnerUuid(playerEntity.getUuid());
        this.setTame(true);
        if (playerEntity instanceof ServerPlayerEntity) {
            Criterions.TAME_ANIMAL.trigger((ServerPlayerEntity)playerEntity, this);
        }
        this.world.sendEntityStatus(this, (byte)7);
        return true;
    }

    @Override
    public void travel(Vec3d vec3d) {
        double e;
        double d;
        if (!this.isAlive()) {
            return;
        }
        if (!(this.hasPassengers() && this.canBeControlledByRider() && this.isSaddled())) {
            this.flyingSpeed = 0.02f;
            super.travel(vec3d);
            return;
        }
        LivingEntity livingEntity = (LivingEntity)this.getPrimaryPassenger();
        this.prevYaw = this.yaw = livingEntity.yaw;
        this.pitch = livingEntity.pitch * 0.5f;
        this.setRotation(this.yaw, this.pitch);
        this.headYaw = this.bodyYaw = this.yaw;
        float f = livingEntity.sidewaysSpeed * 0.5f;
        float g = livingEntity.forwardSpeed;
        if (g <= 0.0f) {
            g *= 0.25f;
            this.soundTicks = 0;
        }
        if (this.onGround && this.jumpStrength == 0.0f && this.isAngry() && !this.jumping) {
            f = 0.0f;
            g = 0.0f;
        }
        if (this.jumpStrength > 0.0f && !this.isInAir() && this.onGround) {
            d = this.getJumpStrength() * (double)this.jumpStrength * (double)this.method_23313();
            e = this.hasStatusEffect(StatusEffects.JUMP_BOOST) ? d + (double)((float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f) : d;
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, e, vec3d2.z);
            this.setInAir(true);
            this.velocityDirty = true;
            if (g > 0.0f) {
                float h = MathHelper.sin(this.yaw * ((float)Math.PI / 180));
                float i = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
                this.setVelocity(this.getVelocity().add(-0.4f * h * this.jumpStrength, 0.0, 0.4f * i * this.jumpStrength));
                this.playJumpSound();
            }
            this.jumpStrength = 0.0f;
        }
        this.flyingSpeed = this.getMovementSpeed() * 0.1f;
        if (this.isLogicalSideForUpdatingMovement()) {
            this.setMovementSpeed((float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            super.travel(new Vec3d(f, vec3d.y, g));
        } else if (livingEntity instanceof PlayerEntity) {
            this.setVelocity(Vec3d.ZERO);
        }
        if (this.onGround) {
            this.jumpStrength = 0.0f;
            this.setInAir(false);
        }
        this.lastLimbDistance = this.limbDistance;
        d = this.getX() - this.prevX;
        float j = MathHelper.sqrt(d * d + (e = this.getZ() - this.prevZ) * e) * 4.0f;
        if (j > 1.0f) {
            j = 1.0f;
        }
        this.limbDistance += (j - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putBoolean("EatingHaystack", this.isEatingGrass());
        compoundTag.putBoolean("Bred", this.isBred());
        compoundTag.putInt("Temper", this.getTemper());
        compoundTag.putBoolean("Tame", this.isTame());
        if (this.getOwnerUuid() != null) {
            compoundTag.putString("OwnerUUID", this.getOwnerUuid().toString());
        }
        if (!this.items.getInvStack(0).isEmpty()) {
            compoundTag.put("SaddleItem", this.items.getInvStack(0).toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        ItemStack itemStack;
        EntityAttributeInstance entityAttributeInstance;
        String string;
        super.readCustomDataFromTag(compoundTag);
        this.setEatingGrass(compoundTag.getBoolean("EatingHaystack"));
        this.setBred(compoundTag.getBoolean("Bred"));
        this.setTemper(compoundTag.getInt("Temper"));
        this.setTame(compoundTag.getBoolean("Tame"));
        if (compoundTag.contains("OwnerUUID", 8)) {
            string = compoundTag.getString("OwnerUUID");
        } else {
            String string2 = compoundTag.getString("Owner");
            string = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string2);
        }
        if (!string.isEmpty()) {
            this.setOwnerUuid(UUID.fromString(string));
        }
        if ((entityAttributeInstance = this.getAttributes().get("Speed")) != null) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(entityAttributeInstance.getBaseValue() * 0.25);
        }
        if (compoundTag.contains("SaddleItem", 10) && (itemStack = ItemStack.fromTag(compoundTag.getCompound("SaddleItem"))).getItem() == Items.SADDLE) {
            this.items.setInvStack(0, itemStack);
        }
        this.updateSaddle();
    }

    @Override
    public boolean canBreedWith(AnimalEntity animalEntity) {
        return false;
    }

    protected boolean canBreed() {
        return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getMaximumHealth() && this.isInLove();
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity passiveEntity) {
        return null;
    }

    protected void setChildAttributes(PassiveEntity passiveEntity, HorseBaseEntity horseBaseEntity) {
        double d = this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue() + passiveEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue() + (double)this.getChildHealthBonus();
        horseBaseEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(d / 3.0);
        double e = this.getAttributeInstance(JUMP_STRENGTH).getBaseValue() + passiveEntity.getAttributeInstance(JUMP_STRENGTH).getBaseValue() + this.getChildJumpStrengthBonus();
        horseBaseEntity.getAttributeInstance(JUMP_STRENGTH).setBaseValue(e / 3.0);
        double f = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue() + passiveEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue() + this.getChildMovementSpeedBonus();
        horseBaseEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(f / 3.0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getPrimaryPassenger() instanceof LivingEntity;
    }

    @Environment(value=EnvType.CLIENT)
    public float getEatingGrassAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getAngryAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastAngryAnimationProgress, this.angryAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getEatingAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setJumpStrength(int i) {
        if (!this.isSaddled()) {
            return;
        }
        if (i < 0) {
            i = 0;
        } else {
            this.jumping = true;
            this.updateAnger();
        }
        this.jumpStrength = i >= 90 ? 1.0f : 0.4f + 0.4f * (float)i / 90.0f;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void startJumping(int i) {
        this.jumping = true;
        this.updateAnger();
    }

    @Override
    public void stopJumping() {
    }

    @Environment(value=EnvType.CLIENT)
    protected void spawnPlayerReactionParticles(boolean bl) {
        DefaultParticleType particleEffect = bl ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleEffect, this.method_23322(1.0), this.method_23319() + 0.5, this.method_23325(1.0), d, e, f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 7) {
            this.spawnPlayerReactionParticles(true);
        } else if (b == 6) {
            this.spawnPlayerReactionParticles(false);
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    public void updatePassengerPosition(Entity entity) {
        super.updatePassengerPosition(entity);
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            this.bodyYaw = mobEntity.bodyYaw;
        }
        if (this.lastAngryAnimationProgress > 0.0f) {
            float f = MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180));
            float g = MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180));
            float h = 0.7f * this.lastAngryAnimationProgress;
            float i = 0.15f * this.lastAngryAnimationProgress;
            entity.setPosition(this.getX() + (double)(h * f), this.getY() + this.getMountedHeightOffset() + entity.getHeightOffset() + (double)i, this.getZ() - (double)(h * g));
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).bodyYaw = this.bodyYaw;
            }
        }
    }

    protected float getChildHealthBonus() {
        return 15.0f + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
    }

    protected double getChildJumpStrengthBonus() {
        return (double)0.4f + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
    }

    protected double getChildMovementSpeedBonus() {
        return ((double)0.45f + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.95f;
    }

    public boolean canEquip() {
        return false;
    }

    public boolean canEquip(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean equip(int i, ItemStack itemStack) {
        int j = i - 400;
        if (j >= 0 && j < 2 && j < this.items.getInvSize()) {
            if (j == 0 && itemStack.getItem() != Items.SADDLE) {
                return false;
            }
            if (!(j != 1 || this.canEquip() && this.canEquip(itemStack))) {
                return false;
            }
            this.items.setInvStack(j, itemStack);
            this.updateSaddle();
            return true;
        }
        int k = i - 500 + 2;
        if (k >= 2 && k < this.items.getInvSize()) {
            this.items.setInvStack(k, itemStack);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        if (entityData == null) {
            entityData = new PassiveEntity.class_4697();
            ((PassiveEntity.class_4697)entityData).method_22433(0.2f);
        }
        return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
    }
}

