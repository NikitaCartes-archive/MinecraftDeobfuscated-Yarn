/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public abstract class LivingEntity
extends Entity {
    private static final UUID ATTR_SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final EntityAttributeModifier ATTR_SPRINTING_SPEED_BOOST = new EntityAttributeModifier(ATTR_SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", (double)0.3f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL).setSerialize(false);
    protected static final TrackedData<Byte> LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Float> HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> POTION_SWIRLS_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> STUCK_ARROW_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> STINGER_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<BlockPos>> SLEEPING_POSITION = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
    protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2f, 0.2f);
    private AbstractEntityAttributeContainer attributes;
    private final DamageTracker damageTracker = new DamageTracker(this);
    private final Map<StatusEffect, StatusEffectInstance> activeStatusEffects = Maps.newHashMap();
    private final DefaultedList<ItemStack> equippedHand = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> equippedArmor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public boolean isHandSwinging;
    public Hand preferredHand;
    public int handSwingTicks;
    public int stuckArrowTimer;
    public int field_20347;
    public int hurtTime;
    public int maxHurtTime;
    public float knockbackVelocity;
    public int deathTime;
    public float lastHandSwingProgress;
    public float handSwingProgress;
    protected int lastAttackedTicks;
    public float lastLimbDistance;
    public float limbDistance;
    public float limbAngle;
    public final int defaultMaximumHealth = 20;
    public final float randomLargeSeed;
    public final float randomSmallSeed;
    public float bodyYaw;
    public float prevBodyYaw;
    public float headYaw;
    public float prevHeadYaw;
    public float flyingSpeed = 0.02f;
    protected PlayerEntity attackingPlayer;
    protected int playerHitTimer;
    protected boolean dead;
    protected int despawnCounter;
    protected float prevStepBobbingAmount;
    protected float stepBobbingAmount;
    protected float lookDirection;
    protected float prevLookDirection;
    protected float field_6215;
    protected int scoreAmount;
    protected float lastDamageTaken;
    protected boolean jumping;
    public float sidewaysSpeed;
    public float upwardSpeed;
    public float forwardSpeed;
    protected int bodyTrackingIncrements;
    protected double serverX;
    protected double serverY;
    protected double serverZ;
    protected double serverYaw;
    protected double serverPitch;
    protected double serverHeadYaw;
    protected int headTrackingIncrements;
    private boolean effectsChanged = true;
    @Nullable
    private LivingEntity attacker;
    private int lastAttackedTime;
    private LivingEntity attacking;
    private int lastAttackTime;
    private float movementSpeed;
    private int jumpingCooldown;
    private float absorptionAmount;
    protected ItemStack activeItemStack = ItemStack.EMPTY;
    protected int itemUseTimeLeft;
    protected int roll;
    private BlockPos lastBlockPos;
    private DamageSource lastDamageSource;
    private long lastDamageTime;
    protected int pushCooldown;
    private float leaningPitch;
    private float lastLeaningPitch;
    protected Brain<?> brain;

    protected LivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.initAttributes();
        this.setHealth(this.getMaximumHealth());
        this.inanimate = true;
        this.randomSmallSeed = (float)((Math.random() + 1.0) * (double)0.01f);
        this.method_23311();
        this.randomLargeSeed = (float)Math.random() * 12398.0f;
        this.headYaw = this.yaw = (float)(Math.random() * 6.2831854820251465);
        this.stepHeight = 0.6f;
        this.brain = this.deserializeBrain(new Dynamic<CompoundTag>(NbtOps.INSTANCE, new CompoundTag()));
    }

    public Brain<?> getBrain() {
        return this.brain;
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return new Brain(ImmutableList.of(), ImmutableList.of(), dynamic);
    }

    @Override
    public void kill() {
        this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean canTarget(EntityType<?> entityType) {
        return true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(LIVING_FLAGS, (byte)0);
        this.dataTracker.startTracking(POTION_SWIRLS_COLOR, 0);
        this.dataTracker.startTracking(POTION_SWIRLS_AMBIENT, false);
        this.dataTracker.startTracking(STUCK_ARROW_COUNT, 0);
        this.dataTracker.startTracking(STINGER_COUNT, 0);
        this.dataTracker.startTracking(HEALTH, Float.valueOf(1.0f));
        this.dataTracker.startTracking(SLEEPING_POSITION, Optional.empty());
    }

    protected void initAttributes() {
        this.getAttributes().register(EntityAttributes.MAX_HEALTH);
        this.getAttributes().register(EntityAttributes.KNOCKBACK_RESISTANCE);
        this.getAttributes().register(EntityAttributes.MOVEMENT_SPEED);
        this.getAttributes().register(EntityAttributes.ARMOR);
        this.getAttributes().register(EntityAttributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
        if (!this.isInsideWater()) {
            this.checkWaterState();
        }
        if (!this.world.isClient && this.fallDistance > 3.0f && bl) {
            float f = MathHelper.ceil(this.fallDistance - 3.0f);
            if (!blockState.isAir()) {
                double e = Math.min((double)(0.2f + f / 15.0f), 2.5);
                int i = (int)(150.0 * e);
                ((ServerWorld)this.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), this.getX(), this.getY(), this.getZ(), i, 0.0, 0.0, 0.0, 0.15f);
            }
        }
        super.fall(d, bl, blockState, blockPos);
    }

    public boolean canBreatheInWater() {
        return this.getGroup() == EntityGroup.UNDEAD;
    }

    @Environment(value=EnvType.CLIENT)
    public float getLeaningPitch(float f) {
        return MathHelper.lerp(f, this.lastLeaningPitch, this.leaningPitch);
    }

    @Override
    public void baseTick() {
        boolean bl2;
        this.lastHandSwingProgress = this.handSwingProgress;
        if (this.firstUpdate) {
            this.getSleepingPosition().ifPresent(this::setPositionInBed);
        }
        super.baseTick();
        this.world.getProfiler().push("livingEntityBaseTick");
        boolean bl = this instanceof PlayerEntity;
        if (this.isAlive()) {
            double e;
            double d;
            if (this.isInsideWall()) {
                this.damage(DamageSource.IN_WALL, 1.0f);
            } else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox()) && (d = this.world.getWorldBorder().getDistanceInsideBorder(this) + this.world.getWorldBorder().getBuffer()) < 0.0 && (e = this.world.getWorldBorder().getDamagePerBlock()) > 0.0) {
                this.damage(DamageSource.IN_WALL, Math.max(1, MathHelper.floor(-d * e)));
            }
        }
        if (this.isFireImmune() || this.world.isClient) {
            this.extinguish();
        }
        boolean bl3 = bl2 = bl && ((PlayerEntity)this).abilities.invulnerable;
        if (this.isAlive()) {
            BlockPos blockPos;
            if (this.isInFluid(FluidTags.WATER) && this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).getBlock() != Blocks.BUBBLE_COLUMN) {
                if (!(this.canBreatheInWater() || StatusEffectUtil.hasWaterBreathing(this) || bl2)) {
                    this.setAir(this.getNextAirUnderwater(this.getAir()));
                    if (this.getAir() == -20) {
                        this.setAir(0);
                        Vec3d vec3d = this.getVelocity();
                        for (int i = 0; i < 8; ++i) {
                            float f = this.random.nextFloat() - this.random.nextFloat();
                            float g = this.random.nextFloat() - this.random.nextFloat();
                            float h = this.random.nextFloat() - this.random.nextFloat();
                            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + (double)f, this.getY() + (double)g, this.getZ() + (double)h, vec3d.x, vec3d.y, vec3d.z);
                        }
                        this.damage(DamageSource.DROWN, 2.0f);
                    }
                }
                if (!this.world.isClient && this.hasVehicle() && this.getVehicle() != null && !this.getVehicle().canBeRiddenInWater()) {
                    this.stopRiding();
                }
            } else if (this.getAir() < this.getMaxAir()) {
                this.setAir(this.getNextAirOnLand(this.getAir()));
            }
            if (!this.world.isClient && !Objects.equal(this.lastBlockPos, blockPos = new BlockPos(this))) {
                this.lastBlockPos = blockPos;
                this.applyFrostWalker(blockPos);
            }
        }
        if (this.isAlive() && this.isTouchingWater()) {
            this.extinguish();
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
            --this.timeUntilRegen;
        }
        if (this.getHealth() <= 0.0f) {
            this.updatePostDeath();
        }
        if (this.playerHitTimer > 0) {
            --this.playerHitTimer;
        } else {
            this.attackingPlayer = null;
        }
        if (this.attacking != null && !this.attacking.isAlive()) {
            this.attacking = null;
        }
        if (this.attacker != null) {
            if (!this.attacker.isAlive()) {
                this.setAttacker(null);
            } else if (this.age - this.lastAttackedTime > 100) {
                this.setAttacker(null);
            }
        }
        this.tickStatusEffects();
        this.prevLookDirection = this.lookDirection;
        this.prevBodyYaw = this.bodyYaw;
        this.prevHeadYaw = this.headYaw;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.world.getProfiler().pop();
    }

    protected void applyFrostWalker(BlockPos blockPos) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, this);
        if (i > 0) {
            FrostWalkerEnchantment.freezeWater(this, this.world, blockPos, i);
        }
    }

    public boolean isBaby() {
        return false;
    }

    public float getScaleFactor() {
        return this.isBaby() ? 0.5f : 1.0f;
    }

    @Override
    public boolean canBeRiddenInWater() {
        return false;
    }

    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove();
            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.world.addParticle(ParticleTypes.POOF, this.getParticleX(1.0), this.getRandomBodyY(), this.getParticleZ(1.0), d, e, f);
            }
        }
    }

    protected boolean canDropLootAndXp() {
        return !this.isBaby();
    }

    protected int getNextAirUnderwater(int i) {
        int j = EnchantmentHelper.getRespiration(this);
        if (j > 0 && this.random.nextInt(j + 1) > 0) {
            return i;
        }
        return i - 1;
    }

    protected int getNextAirOnLand(int i) {
        return Math.min(i + 4, this.getMaxAir());
    }

    protected int getCurrentExperience(PlayerEntity playerEntity) {
        return 0;
    }

    protected boolean shouldAlwaysDropXp() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return this.attacker;
    }

    public int getLastAttackedTime() {
        return this.lastAttackedTime;
    }

    public void setAttacker(@Nullable LivingEntity livingEntity) {
        this.attacker = livingEntity;
        this.lastAttackedTime = this.age;
    }

    @Nullable
    public LivingEntity getAttacking() {
        return this.attacking;
    }

    public int getLastAttackTime() {
        return this.lastAttackTime;
    }

    public void onAttacking(Entity entity) {
        this.attacking = entity instanceof LivingEntity ? (LivingEntity)entity : null;
        this.lastAttackTime = this.age;
    }

    public int getDespawnCounter() {
        return this.despawnCounter;
    }

    public void setDespawnCounter(int i) {
        this.despawnCounter = i;
    }

    protected void onEquipStack(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        SoundEvent soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        Item item = itemStack.getItem();
        if (item instanceof ArmorItem) {
            soundEvent = ((ArmorItem)item).getMaterial().getEquipSound();
        } else if (item == Items.ELYTRA) {
            soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
        }
        this.playSound(soundEvent, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putFloat("Health", this.getHealth());
        compoundTag.putShort("HurtTime", (short)this.hurtTime);
        compoundTag.putInt("HurtByTimestamp", this.lastAttackedTime);
        compoundTag.putShort("DeathTime", (short)this.deathTime);
        compoundTag.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        compoundTag.put("Attributes", EntityAttributes.toTag(this.getAttributes()));
        if (!this.activeStatusEffects.isEmpty()) {
            ListTag listTag = new ListTag();
            for (StatusEffectInstance statusEffectInstance : this.activeStatusEffects.values()) {
                listTag.add(statusEffectInstance.serialize(new CompoundTag()));
            }
            compoundTag.put("ActiveEffects", listTag);
        }
        compoundTag.putBoolean("FallFlying", this.isFallFlying());
        this.getSleepingPosition().ifPresent(blockPos -> {
            compoundTag.putInt("SleepingX", blockPos.getX());
            compoundTag.putInt("SleepingY", blockPos.getY());
            compoundTag.putInt("SleepingZ", blockPos.getZ());
        });
        compoundTag.put("Brain", this.brain.serialize(NbtOps.INSTANCE));
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.setAbsorptionAmount(compoundTag.getFloat("AbsorptionAmount"));
        if (compoundTag.contains("Attributes", 9) && this.world != null && !this.world.isClient) {
            EntityAttributes.fromTag(this.getAttributes(), compoundTag.getList("Attributes", 10));
        }
        if (compoundTag.contains("ActiveEffects", 9)) {
            ListTag listTag = compoundTag.getList("ActiveEffects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.deserialize(compoundTag2);
                if (statusEffectInstance == null) continue;
                this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
            }
        }
        if (compoundTag.contains("Health", 99)) {
            this.setHealth(compoundTag.getFloat("Health"));
        }
        this.hurtTime = compoundTag.getShort("HurtTime");
        this.deathTime = compoundTag.getShort("DeathTime");
        this.lastAttackedTime = compoundTag.getInt("HurtByTimestamp");
        if (compoundTag.contains("Team", 8)) {
            boolean bl;
            String string = compoundTag.getString("Team");
            Team team = this.world.getScoreboard().getTeam(string);
            boolean bl2 = bl = team != null && this.world.getScoreboard().addPlayerToTeam(this.getUuidAsString(), team);
            if (!bl) {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", (Object)string);
            }
        }
        if (compoundTag.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }
        if (compoundTag.contains("SleepingX", 99) && compoundTag.contains("SleepingY", 99) && compoundTag.contains("SleepingZ", 99)) {
            BlockPos blockPos = new BlockPos(compoundTag.getInt("SleepingX"), compoundTag.getInt("SleepingY"), compoundTag.getInt("SleepingZ"));
            this.setSleepingPosition(blockPos);
            this.dataTracker.set(POSE, EntityPose.SLEEPING);
            if (!this.firstUpdate) {
                this.setPositionInBed(blockPos);
            }
        }
        if (compoundTag.contains("Brain", 10)) {
            this.brain = this.deserializeBrain(new Dynamic<Tag>(NbtOps.INSTANCE, compoundTag.get("Brain")));
        }
    }

    protected void tickStatusEffects() {
        Iterator<StatusEffect> iterator = this.activeStatusEffects.keySet().iterator();
        try {
            while (iterator.hasNext()) {
                StatusEffect statusEffect = iterator.next();
                StatusEffectInstance statusEffectInstance = this.activeStatusEffects.get(statusEffect);
                if (!statusEffectInstance.update(this)) {
                    if (this.world.isClient) continue;
                    iterator.remove();
                    this.onStatusEffectRemoved(statusEffectInstance);
                    continue;
                }
                if (statusEffectInstance.getDuration() % 600 != 0) continue;
                this.onStatusEffectUpgraded(statusEffectInstance, false);
            }
        } catch (ConcurrentModificationException statusEffect) {
            // empty catch block
        }
        if (this.effectsChanged) {
            if (!this.world.isClient) {
                this.updatePotionVisibility();
            }
            this.effectsChanged = false;
        }
        int i = this.dataTracker.get(POTION_SWIRLS_COLOR);
        boolean bl = this.dataTracker.get(POTION_SWIRLS_AMBIENT);
        if (i > 0) {
            boolean bl2 = this.isInvisible() ? this.random.nextInt(15) == 0 : this.random.nextBoolean();
            if (bl) {
                bl2 &= this.random.nextInt(5) == 0;
            }
            if (bl2 && i > 0) {
                double d = (double)(i >> 16 & 0xFF) / 255.0;
                double e = (double)(i >> 8 & 0xFF) / 255.0;
                double f = (double)(i >> 0 & 0xFF) / 255.0;
                this.world.addParticle(bl ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
            }
        }
    }

    protected void updatePotionVisibility() {
        if (this.activeStatusEffects.isEmpty()) {
            this.clearPotionSwirls();
            this.setInvisible(false);
        } else {
            Collection<StatusEffectInstance> collection = this.activeStatusEffects.values();
            this.dataTracker.set(POTION_SWIRLS_AMBIENT, LivingEntity.containsOnlyAmbientEffects(collection));
            this.dataTracker.set(POTION_SWIRLS_COLOR, PotionUtil.getColor(collection));
            this.setInvisible(this.hasStatusEffect(StatusEffects.INVISIBILITY));
        }
    }

    public double getAttackDistanceScalingFactor(@Nullable Entity entity) {
        double d = 1.0;
        if (this.method_21751()) {
            d *= 0.8;
        }
        if (this.isInvisible()) {
            float f = this.getArmorVisibility();
            if (f < 0.1f) {
                f = 0.1f;
            }
            d *= 0.7 * (double)f;
        }
        if (entity != null) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
            Item item = itemStack.getItem();
            EntityType<?> entityType = entity.getType();
            if (entityType == EntityType.SKELETON && item == Items.SKELETON_SKULL || entityType == EntityType.ZOMBIE && item == Items.ZOMBIE_HEAD || entityType == EntityType.CREEPER && item == Items.CREEPER_HEAD) {
                d *= 0.5;
            }
        }
        return d;
    }

    public boolean canTarget(LivingEntity livingEntity) {
        return true;
    }

    public boolean isTarget(LivingEntity livingEntity, TargetPredicate targetPredicate) {
        return targetPredicate.test(this, livingEntity);
    }

    public static boolean containsOnlyAmbientEffects(Collection<StatusEffectInstance> collection) {
        for (StatusEffectInstance statusEffectInstance : collection) {
            if (statusEffectInstance.isAmbient()) continue;
            return false;
        }
        return true;
    }

    protected void clearPotionSwirls() {
        this.dataTracker.set(POTION_SWIRLS_AMBIENT, false);
        this.dataTracker.set(POTION_SWIRLS_COLOR, 0);
    }

    public boolean clearStatusEffects() {
        if (this.world.isClient) {
            return false;
        }
        Iterator<StatusEffectInstance> iterator = this.activeStatusEffects.values().iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            this.onStatusEffectRemoved(iterator.next());
            iterator.remove();
            bl = true;
        }
        return bl;
    }

    public Collection<StatusEffectInstance> getStatusEffects() {
        return this.activeStatusEffects.values();
    }

    public Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects() {
        return this.activeStatusEffects;
    }

    public boolean hasStatusEffect(StatusEffect statusEffect) {
        return this.activeStatusEffects.containsKey(statusEffect);
    }

    @Nullable
    public StatusEffectInstance getStatusEffect(StatusEffect statusEffect) {
        return this.activeStatusEffects.get(statusEffect);
    }

    public boolean addStatusEffect(StatusEffectInstance statusEffectInstance) {
        if (!this.canHaveStatusEffect(statusEffectInstance)) {
            return false;
        }
        StatusEffectInstance statusEffectInstance2 = this.activeStatusEffects.get(statusEffectInstance.getEffectType());
        if (statusEffectInstance2 == null) {
            this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
            this.onStatusEffectApplied(statusEffectInstance);
            return true;
        }
        if (statusEffectInstance2.upgrade(statusEffectInstance)) {
            this.onStatusEffectUpgraded(statusEffectInstance2, true);
            return true;
        }
        return false;
    }

    public boolean canHaveStatusEffect(StatusEffectInstance statusEffectInstance) {
        StatusEffect statusEffect;
        return this.getGroup() != EntityGroup.UNDEAD || (statusEffect = statusEffectInstance.getEffectType()) != StatusEffects.REGENERATION && statusEffect != StatusEffects.POISON;
    }

    public boolean isUndead() {
        return this.getGroup() == EntityGroup.UNDEAD;
    }

    @Nullable
    public StatusEffectInstance removeStatusEffect(@Nullable StatusEffect statusEffect) {
        return this.activeStatusEffects.remove(statusEffect);
    }

    public boolean tryRemoveStatusEffect(StatusEffect statusEffect) {
        StatusEffectInstance statusEffectInstance = this.removeStatusEffect(statusEffect);
        if (statusEffectInstance != null) {
            this.onStatusEffectRemoved(statusEffectInstance);
            return true;
        }
        return false;
    }

    protected void onStatusEffectApplied(StatusEffectInstance statusEffectInstance) {
        this.effectsChanged = true;
        if (!this.world.isClient) {
            statusEffectInstance.getEffectType().onApplied(this, this.getAttributes(), statusEffectInstance.getAmplifier());
        }
    }

    protected void onStatusEffectUpgraded(StatusEffectInstance statusEffectInstance, boolean bl) {
        this.effectsChanged = true;
        if (bl && !this.world.isClient) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            statusEffect.onRemoved(this, this.getAttributes(), statusEffectInstance.getAmplifier());
            statusEffect.onApplied(this, this.getAttributes(), statusEffectInstance.getAmplifier());
        }
    }

    protected void onStatusEffectRemoved(StatusEffectInstance statusEffectInstance) {
        this.effectsChanged = true;
        if (!this.world.isClient) {
            statusEffectInstance.getEffectType().onRemoved(this, this.getAttributes(), statusEffectInstance.getAmplifier());
        }
    }

    public void heal(float f) {
        float g = this.getHealth();
        if (g > 0.0f) {
            this.setHealth(g + f);
        }
    }

    public float getHealth() {
        return this.dataTracker.get(HEALTH).floatValue();
    }

    public void setHealth(float f) {
        this.dataTracker.set(HEALTH, Float.valueOf(MathHelper.clamp(f, 0.0f, this.getMaximumHealth())));
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        boolean bl3;
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (this.world.isClient) {
            return false;
        }
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        if (damageSource.isFire() && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.world.isClient) {
            this.wakeUp();
        }
        this.despawnCounter = 0;
        float g = f;
        if (!(damageSource != DamageSource.ANVIL && damageSource != DamageSource.FALLING_BLOCK || this.getEquippedStack(EquipmentSlot.HEAD).isEmpty())) {
            this.getEquippedStack(EquipmentSlot.HEAD).damage((int)(f * 4.0f + this.random.nextFloat() * f * 2.0f), this, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HEAD));
            f *= 0.75f;
        }
        boolean bl = false;
        float h = 0.0f;
        if (f > 0.0f && this.blockedByShield(damageSource)) {
            Entity entity;
            this.damageShield(f);
            h = f;
            f = 0.0f;
            if (!damageSource.isProjectile() && (entity = damageSource.getSource()) instanceof LivingEntity) {
                this.takeShieldHit((LivingEntity)entity);
            }
            bl = true;
        }
        this.limbDistance = 1.5f;
        boolean bl2 = true;
        if ((float)this.timeUntilRegen > 10.0f) {
            if (f <= this.lastDamageTaken) {
                return false;
            }
            this.applyDamage(damageSource, f - this.lastDamageTaken);
            this.lastDamageTaken = f;
            bl2 = false;
        } else {
            this.lastDamageTaken = f;
            this.timeUntilRegen = 20;
            this.applyDamage(damageSource, f);
            this.hurtTime = this.maxHurtTime = 10;
        }
        this.knockbackVelocity = 0.0f;
        Entity entity2 = damageSource.getAttacker();
        if (entity2 != null) {
            WolfEntity wolfEntity;
            if (entity2 instanceof LivingEntity) {
                this.setAttacker((LivingEntity)entity2);
            }
            if (entity2 instanceof PlayerEntity) {
                this.playerHitTimer = 100;
                this.attackingPlayer = (PlayerEntity)entity2;
            } else if (entity2 instanceof WolfEntity && (wolfEntity = (WolfEntity)entity2).isTamed()) {
                this.playerHitTimer = 100;
                LivingEntity livingEntity2 = wolfEntity.getOwner();
                this.attackingPlayer = livingEntity2 != null && livingEntity2.getType() == EntityType.PLAYER ? (PlayerEntity)livingEntity2 : null;
            }
        }
        if (bl2) {
            if (bl) {
                this.world.sendEntityStatus(this, (byte)29);
            } else if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).method_5549()) {
                this.world.sendEntityStatus(this, (byte)33);
            } else {
                int b = damageSource == DamageSource.DROWN ? 36 : (damageSource.isFire() ? 37 : (damageSource == DamageSource.SWEET_BERRY_BUSH ? 44 : 2));
                this.world.sendEntityStatus(this, (byte)b);
            }
            if (damageSource != DamageSource.DROWN && (!bl || f > 0.0f)) {
                this.scheduleVelocityUpdate();
            }
            if (entity2 != null) {
                double d = entity2.getX() - this.getX();
                double e = entity2.getZ() - this.getZ();
                while (d * d + e * e < 1.0E-4) {
                    d = (Math.random() - Math.random()) * 0.01;
                    e = (Math.random() - Math.random()) * 0.01;
                }
                this.knockbackVelocity = (float)(MathHelper.atan2(e, d) * 57.2957763671875 - (double)this.yaw);
                this.takeKnockback(entity2, 0.4f, d, e);
            } else {
                this.knockbackVelocity = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.getHealth() <= 0.0f) {
            if (!this.tryUseTotem(damageSource)) {
                SoundEvent soundEvent = this.getDeathSound();
                if (bl2 && soundEvent != null) {
                    this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
                }
                this.onDeath(damageSource);
            }
        } else if (bl2) {
            this.playHurtSound(damageSource);
        }
        boolean bl4 = bl3 = !bl || f > 0.0f;
        if (bl3) {
            this.lastDamageSource = damageSource;
            this.lastDamageTime = this.world.getTime();
        }
        if (this instanceof ServerPlayerEntity) {
            Criterions.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)this, damageSource, g, f, bl);
            if (h > 0.0f && h < 3.4028235E37f) {
                ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(h * 10.0f));
            }
        }
        if (entity2 instanceof ServerPlayerEntity) {
            Criterions.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, this, damageSource, g, f, bl);
        }
        return bl3;
    }

    protected void takeShieldHit(LivingEntity livingEntity) {
        livingEntity.knockback(this);
    }

    protected void knockback(LivingEntity livingEntity) {
        livingEntity.takeKnockback(this, 0.5f, livingEntity.getX() - this.getX(), livingEntity.getZ() - this.getZ());
    }

    private boolean tryUseTotem(DamageSource damageSource) {
        if (damageSource.isOutOfWorld()) {
            return false;
        }
        ItemStack itemStack = null;
        for (Hand hand : Hand.values()) {
            ItemStack itemStack2 = this.getStackInHand(hand);
            if (itemStack2.getItem() != Items.TOTEM_OF_UNDYING) continue;
            itemStack = itemStack2.copy();
            itemStack2.decrement(1);
            break;
        }
        if (itemStack != null) {
            if (this instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                Criterions.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
            }
            this.setHealth(1.0f);
            this.clearStatusEffects();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            this.world.sendEntityStatus(this, (byte)35);
        }
        return itemStack != null;
    }

    @Nullable
    public DamageSource getRecentDamageSource() {
        if (this.world.getTime() - this.lastDamageTime > 40L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }

    protected void playHurtSound(DamageSource damageSource) {
        SoundEvent soundEvent = this.getHurtSound(damageSource);
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private boolean blockedByShield(DamageSource damageSource) {
        Vec3d vec3d;
        ProjectileEntity projectileEntity;
        Entity entity = damageSource.getSource();
        boolean bl = false;
        if (entity instanceof ProjectileEntity && (projectileEntity = (ProjectileEntity)entity).getPierceLevel() > 0) {
            bl = true;
        }
        if (!damageSource.bypassesArmor() && this.isBlocking() && !bl && (vec3d = damageSource.getPosition()) != null) {
            Vec3d vec3d2 = this.getRotationVec(1.0f);
            Vec3d vec3d3 = vec3d.reverseSubtract(this.getPos()).normalize();
            vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
            if (vec3d3.dotProduct(vec3d2) < 0.0) {
                return true;
            }
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    private void playEquipmentBreakEffects(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f, false);
            }
            this.spawnItemParticles(itemStack, 5);
        }
    }

    public void onDeath(DamageSource damageSource) {
        if (this.dead) {
            return;
        }
        Entity entity = damageSource.getAttacker();
        LivingEntity livingEntity = this.getPrimeAdversary();
        if (this.scoreAmount >= 0 && livingEntity != null) {
            livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
        }
        if (entity != null) {
            entity.onKilledOther(this);
        }
        if (this.isSleeping()) {
            this.wakeUp();
        }
        this.dead = true;
        this.getDamageTracker().update();
        if (!this.world.isClient) {
            this.drop(damageSource);
            this.method_23733(livingEntity);
        }
        this.world.sendEntityStatus(this, (byte)3);
        this.setPose(EntityPose.DYING);
    }

    protected void method_23733(@Nullable LivingEntity livingEntity) {
        if (this.world.isClient) {
            return;
        }
        boolean bl = false;
        if (livingEntity instanceof WitherEntity) {
            if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                BlockPos blockPos = new BlockPos(this);
                BlockState blockState = Blocks.WITHER_ROSE.getDefaultState();
                if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
                    this.world.setBlockState(blockPos, blockState, 3);
                    bl = true;
                }
            }
            if (!bl) {
                ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
                this.world.spawnEntity(itemEntity);
            }
        }
    }

    protected void drop(DamageSource damageSource) {
        boolean bl;
        Entity entity = damageSource.getAttacker();
        int i = entity instanceof PlayerEntity ? EnchantmentHelper.getLooting((LivingEntity)entity) : 0;
        boolean bl2 = bl = this.playerHitTimer > 0;
        if (this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropLoot(damageSource, bl);
            this.dropEquipment(damageSource, i, bl);
        }
        this.dropInventory();
        this.method_23883();
    }

    protected void dropInventory() {
    }

    protected void method_23883() {
        if (!this.world.isClient && (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
            int j;
            for (int i = this.getCurrentExperience(this.attackingPlayer); i > 0; i -= j) {
                j = ExperienceOrbEntity.roundToOrbSize(i);
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
            }
        }
    }

    protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
    }

    public Identifier getLootTable() {
        return this.getType().getLootTableId();
    }

    protected void dropLoot(DamageSource damageSource, boolean bl) {
        Identifier identifier = this.getLootTable();
        LootTable lootTable = this.world.getServer().getLootManager().getSupplier(identifier);
        LootContext.Builder builder = this.getLootContextBuilder(bl, damageSource);
        lootTable.dropLimited(builder.build(LootContextTypes.ENTITY), this::dropStack);
    }

    protected LootContext.Builder getLootContextBuilder(boolean bl, DamageSource damageSource) {
        LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).setRandom(this.random).put(LootContextParameters.THIS_ENTITY, this).put(LootContextParameters.POSITION, new BlockPos(this)).put(LootContextParameters.DAMAGE_SOURCE, damageSource).putNullable(LootContextParameters.KILLER_ENTITY, damageSource.getAttacker()).putNullable(LootContextParameters.DIRECT_KILLER_ENTITY, damageSource.getSource());
        if (bl && this.attackingPlayer != null) {
            builder = builder.put(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).setLuck(this.attackingPlayer.getLuck());
        }
        return builder;
    }

    public void takeKnockback(Entity entity, float f, double d, double e) {
        if (this.random.nextDouble() < this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue()) {
            return;
        }
        this.velocityDirty = true;
        Vec3d vec3d = this.getVelocity();
        Vec3d vec3d2 = new Vec3d(d, 0.0, e).normalize().multiply(f);
        this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.onGround ? Math.min(0.4, vec3d.y / 2.0 + (double)f) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    }

    protected SoundEvent getFallSound(int i) {
        if (i > 4) {
            return SoundEvents.ENTITY_GENERIC_BIG_FALL;
        }
        return SoundEvents.ENTITY_GENERIC_SMALL_FALL;
    }

    protected SoundEvent getDrinkSound(ItemStack itemStack) {
        return itemStack.getDrinkSound();
    }

    public SoundEvent getEatSound(ItemStack itemStack) {
        return itemStack.getEatSound();
    }

    public boolean isClimbing() {
        if (this.isSpectator()) {
            return false;
        }
        BlockState blockState = this.getBlockState();
        Block block = blockState.getBlock();
        if (block == Blocks.LADDER || block == Blocks.VINE || block == Blocks.SCAFFOLDING) {
            return true;
        }
        return block instanceof TrapdoorBlock && this.canEnterTrapdoor(new BlockPos(this), blockState);
    }

    public BlockState getBlockState() {
        return this.world.getBlockState(new BlockPos(this));
    }

    private boolean canEnterTrapdoor(BlockPos blockPos, BlockState blockState) {
        BlockState blockState2;
        return blockState.get(TrapdoorBlock.OPEN) != false && (blockState2 = this.world.getBlockState(blockPos.down())).getBlock() == Blocks.LADDER && blockState2.get(LadderBlock.FACING) == blockState.get(TrapdoorBlock.FACING);
    }

    @Override
    public boolean isAlive() {
        return !this.removed && this.getHealth() > 0.0f;
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        boolean bl = super.handleFallDamage(f, g);
        int i = this.method_23329(f, g);
        if (i > 0) {
            this.playSound(this.getFallSound(i), 1.0f, 1.0f);
            this.method_23328();
            this.damage(DamageSource.FALL, i);
            return true;
        }
        return bl;
    }

    protected int method_23329(float f, float g) {
        StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.JUMP_BOOST);
        float h = statusEffectInstance == null ? 0.0f : (float)(statusEffectInstance.getAmplifier() + 1);
        return MathHelper.ceil((f - 3.0f - h) * g);
    }

    protected void method_23328() {
        int k;
        int j;
        if (this.isSilent()) {
            return;
        }
        int i = MathHelper.floor(this.getX());
        BlockState blockState = this.world.getBlockState(new BlockPos(i, j = MathHelper.floor(this.getY() - (double)0.2f), k = MathHelper.floor(this.getZ())));
        if (!blockState.isAir()) {
            BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
            this.playSound(blockSoundGroup.getFallSound(), blockSoundGroup.getVolume() * 0.5f, blockSoundGroup.getPitch() * 0.75f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void animateDamage() {
        this.hurtTime = this.maxHurtTime = 10;
        this.knockbackVelocity = 0.0f;
    }

    public int getArmor() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.ARMOR);
        return MathHelper.floor(entityAttributeInstance.getValue());
    }

    protected void damageArmor(float f) {
    }

    protected void damageShield(float f) {
    }

    protected float applyArmorToDamage(DamageSource damageSource, float f) {
        if (!damageSource.bypassesArmor()) {
            this.damageArmor(f);
            f = DamageUtil.getDamageLeft(f, this.getArmor(), (float)this.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());
        }
        return f;
    }

    protected float applyEnchantmentsToDamage(DamageSource damageSource, float f) {
        int i;
        int j;
        float g;
        float h;
        float k;
        if (damageSource.isUnblockable()) {
            return f;
        }
        if (this.hasStatusEffect(StatusEffects.RESISTANCE) && damageSource != DamageSource.OUT_OF_WORLD && (k = (h = f) - (f = Math.max((g = f * (float)(j = 25 - (i = (this.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5))) / 25.0f, 0.0f))) > 0.0f && k < 3.4028235E37f) {
            if (this instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_RESISTED, Math.round(k * 10.0f));
            } else if (damageSource.getAttacker() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)damageSource.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(k * 10.0f));
            }
        }
        if (f <= 0.0f) {
            return 0.0f;
        }
        i = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), damageSource);
        if (i > 0) {
            f = DamageUtil.getInflictedDamage(f, i);
        }
        return f;
    }

    protected void applyDamage(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return;
        }
        f = this.applyArmorToDamage(damageSource, f);
        float g = f = this.applyEnchantmentsToDamage(damageSource, f);
        f = Math.max(f - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (g - f));
        float h = g - f;
        if (h > 0.0f && h < 3.4028235E37f && damageSource.getAttacker() instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)damageSource.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(h * 10.0f));
        }
        if (f == 0.0f) {
            return;
        }
        float i = this.getHealth();
        this.setHealth(i - f);
        this.getDamageTracker().onDamage(damageSource, i, f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - f);
    }

    public DamageTracker getDamageTracker() {
        return this.damageTracker;
    }

    @Nullable
    public LivingEntity getPrimeAdversary() {
        if (this.damageTracker.getBiggestAttacker() != null) {
            return this.damageTracker.getBiggestAttacker();
        }
        if (this.attackingPlayer != null) {
            return this.attackingPlayer;
        }
        if (this.attacker != null) {
            return this.attacker;
        }
        return null;
    }

    public final float getMaximumHealth() {
        return (float)this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getValue();
    }

    public final int getStuckArrowCount() {
        return this.dataTracker.get(STUCK_ARROW_COUNT);
    }

    public final void setStuckArrowCount(int i) {
        this.dataTracker.set(STUCK_ARROW_COUNT, i);
    }

    public final int getStingerCount() {
        return this.dataTracker.get(STINGER_COUNT);
    }

    public final void setStingerCount(int i) {
        this.dataTracker.set(STINGER_COUNT, i);
    }

    private int getHandSwingDuration() {
        if (StatusEffectUtil.hasHaste(this)) {
            return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
        }
        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            return 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2;
        }
        return 6;
    }

    public void swingHand(Hand hand) {
        this.swingHand(hand, false);
    }

    public void swingHand(Hand hand, boolean bl) {
        if (!this.isHandSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
            this.handSwingTicks = -1;
            this.isHandSwinging = true;
            this.preferredHand = hand;
            if (this.world instanceof ServerWorld) {
                EntityAnimationS2CPacket entityAnimationS2CPacket = new EntityAnimationS2CPacket(this, hand == Hand.MAIN_HAND ? 0 : 3);
                ServerChunkManager serverChunkManager = ((ServerWorld)this.world).getChunkManager();
                if (bl) {
                    serverChunkManager.sendToNearbyPlayers(this, entityAnimationS2CPacket);
                } else {
                    serverChunkManager.sendToOtherNearbyPlayers(this, entityAnimationS2CPacket);
                }
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        switch (b) {
            case 2: 
            case 33: 
            case 36: 
            case 37: 
            case 44: {
                DamageSource damageSource;
                SoundEvent soundEvent;
                boolean bl = b == 33;
                boolean bl2 = b == 36;
                boolean bl3 = b == 37;
                boolean bl4 = b == 44;
                this.limbDistance = 1.5f;
                this.timeUntilRegen = 20;
                this.hurtTime = this.maxHurtTime = 10;
                this.knockbackVelocity = 0.0f;
                if (bl) {
                    this.playSound(SoundEvents.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if ((soundEvent = this.getHurtSound(damageSource = bl3 ? DamageSource.ON_FIRE : (bl2 ? DamageSource.DROWN : (bl4 ? DamageSource.SWEET_BERRY_BUSH : DamageSource.GENERIC)))) != null) {
                    this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.damage(DamageSource.GENERIC, 0.0f);
                break;
            }
            case 3: {
                SoundEvent soundEvent2 = this.getDeathSound();
                if (soundEvent2 != null) {
                    this.playSound(soundEvent2, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if (this instanceof PlayerEntity) break;
                this.setHealth(0.0f);
                this.onDeath(DamageSource.GENERIC);
                break;
            }
            case 30: {
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f);
                break;
            }
            case 29: {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 0.8f + this.world.random.nextFloat() * 0.4f);
                break;
            }
            case 46: {
                int i = 128;
                for (int j = 0; j < 128; ++j) {
                    double d = (double)j / 127.0;
                    float f = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float g = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float h = (this.random.nextFloat() - 0.5f) * 0.2f;
                    double e = MathHelper.lerp(d, this.prevX, this.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
                    double k = MathHelper.lerp(d, this.prevY, this.getY()) + this.random.nextDouble() * (double)this.getHeight();
                    double l = MathHelper.lerp(d, this.prevZ, this.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
                    this.world.addParticle(ParticleTypes.PORTAL, e, k, l, f, g, h);
                }
                break;
            }
            case 47: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.MAINHAND));
                break;
            }
            case 48: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.OFFHAND));
                break;
            }
            case 49: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.HEAD));
                break;
            }
            case 50: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.CHEST));
                break;
            }
            case 51: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.LEGS));
                break;
            }
            case 52: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.FEET));
                break;
            }
            case 54: {
                HoneyBlock.method_24178(this);
                break;
            }
            default: {
                super.handleStatus(b);
            }
        }
    }

    @Override
    protected void destroy() {
        this.damage(DamageSource.OUT_OF_WORLD, 4.0f);
    }

    protected void tickHandSwing() {
        int i = this.getHandSwingDuration();
        if (this.isHandSwinging) {
            ++this.handSwingTicks;
            if (this.handSwingTicks >= i) {
                this.handSwingTicks = 0;
                this.isHandSwinging = false;
            }
        } else {
            this.handSwingTicks = 0;
        }
        this.handSwingProgress = (float)this.handSwingTicks / (float)i;
    }

    public EntityAttributeInstance getAttributeInstance(EntityAttribute entityAttribute) {
        return this.getAttributes().get(entityAttribute);
    }

    public AbstractEntityAttributeContainer getAttributes() {
        if (this.attributes == null) {
            this.attributes = new EntityAttributeContainer();
        }
        return this.attributes;
    }

    public EntityGroup getGroup() {
        return EntityGroup.DEFAULT;
    }

    public ItemStack getMainHandStack() {
        return this.getEquippedStack(EquipmentSlot.MAINHAND);
    }

    public ItemStack getOffHandStack() {
        return this.getEquippedStack(EquipmentSlot.OFFHAND);
    }

    public ItemStack getStackInHand(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return this.getEquippedStack(EquipmentSlot.MAINHAND);
        }
        if (hand == Hand.OFF_HAND) {
            return this.getEquippedStack(EquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException("Invalid hand " + (Object)((Object)hand));
    }

    public void setStackInHand(Hand hand, ItemStack itemStack) {
        if (hand == Hand.MAIN_HAND) {
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
        } else if (hand == Hand.OFF_HAND) {
            this.equipStack(EquipmentSlot.OFFHAND, itemStack);
        } else {
            throw new IllegalArgumentException("Invalid hand " + (Object)((Object)hand));
        }
    }

    public boolean hasStackEquipped(EquipmentSlot equipmentSlot) {
        return !this.getEquippedStack(equipmentSlot).isEmpty();
    }

    @Override
    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Override
    public abstract void equipStack(EquipmentSlot var1, ItemStack var2);

    public float getArmorVisibility() {
        Iterable<ItemStack> iterable = this.getArmorItems();
        int i = 0;
        int j = 0;
        for (ItemStack itemStack : iterable) {
            if (!itemStack.isEmpty()) {
                ++j;
            }
            ++i;
        }
        return i > 0 ? (float)j / (float)i : 0.0f;
    }

    @Override
    public void setSprinting(boolean bl) {
        super.setSprinting(bl);
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (entityAttributeInstance.getModifier(ATTR_SPRINTING_SPEED_BOOST_ID) != null) {
            entityAttributeInstance.removeModifier(ATTR_SPRINTING_SPEED_BOOST);
        }
        if (bl) {
            entityAttributeInstance.addModifier(ATTR_SPRINTING_SPEED_BOOST);
        }
    }

    protected float getSoundVolume() {
        return 1.0f;
    }

    protected float getSoundPitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!this.isSleeping()) {
            super.pushAwayFrom(entity);
        }
    }

    private void onDismounted(Entity entity) {
        if (this.world.getBlockState(new BlockPos(entity)).getBlock().matches(BlockTags.PORTALS)) {
            this.setPosition(entity.getX(), entity.getBodyY(1.0) + 0.001, entity.getZ());
            return;
        }
        if (entity instanceof BoatEntity || entity instanceof HorseBaseEntity) {
            float f;
            int i;
            double e;
            double d = (double)(this.getWidth() / 2.0f + entity.getWidth() / 2.0f) + 0.4;
            Box box = entity.getBoundingBox();
            if (entity instanceof BoatEntity) {
                e = box.y2;
                i = 2;
                f = 0.0f;
            } else {
                e = box.y1;
                i = 3;
                f = 1.5707964f * (float)(this.getMainArm() == Arm.RIGHT ? -1 : 1);
            }
            float g = -this.yaw * ((float)Math.PI / 180) - (float)Math.PI + f;
            float h = -MathHelper.sin(g);
            float j = -MathHelper.cos(g);
            double k = Math.abs(h) > Math.abs(j) ? d / (double)Math.abs(h) : d / (double)Math.abs(j);
            Box box2 = this.getBoundingBox().offset(-this.getX(), -this.getY(), -this.getZ());
            ImmutableSet<Entity> immutableSet = ImmutableSet.of(this, entity);
            double l = this.getX() + (double)h * k;
            double m = this.getZ() + (double)j * k;
            double n = 0.001;
            for (int o = 0; o < i; ++o) {
                double p = e + n;
                if (this.world.doesNotCollide(this, box2.offset(l, p, m), immutableSet)) {
                    this.setPosition(l, p, m);
                    return;
                }
                n += 1.0;
            }
            this.setPosition(entity.getX(), entity.getBodyY(1.0) + 0.001, entity.getZ());
            return;
        }
        double q = entity.getX();
        double r = entity.getBodyY(1.0);
        double s = entity.getZ();
        Direction direction = entity.getMovementDirection();
        if (direction != null && direction.getAxis() != Direction.Axis.Y) {
            Direction direction2 = direction.rotateYClockwise();
            int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
            double k = Math.floor(this.getX()) + 0.5;
            double t = Math.floor(this.getZ()) + 0.5;
            double l = this.getBoundingBox().x2 - this.getBoundingBox().x1;
            double m = this.getBoundingBox().z2 - this.getBoundingBox().z1;
            Box box3 = new Box(k - l / 2.0, entity.getBoundingBox().y1, t - m / 2.0, k + l / 2.0, Math.floor(entity.getBoundingBox().y1) + (double)this.getHeight(), t + m / 2.0);
            for (int[] js : is) {
                BlockPos blockPos;
                double u = direction.getOffsetX() * js[0] + direction2.getOffsetX() * js[1];
                double v = direction.getOffsetZ() * js[0] + direction2.getOffsetZ() * js[1];
                double w = k + u;
                double x = t + v;
                Box box4 = box3.offset(u, 0.0, v);
                if (this.world.doesNotCollide(this, box4)) {
                    blockPos = new BlockPos(w, this.getY(), x);
                    if (this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, this)) {
                        this.requestTeleport(w, this.getY() + 1.0, x);
                        return;
                    }
                    BlockPos blockPos2 = new BlockPos(w, this.getY() - 1.0, x);
                    if (!this.world.getBlockState(blockPos2).hasSolidTopSurface(this.world, blockPos2, this) && !this.world.getFluidState(blockPos2).matches(FluidTags.WATER)) continue;
                    q = w;
                    r = this.getY() + 1.0;
                    s = x;
                    continue;
                }
                blockPos = new BlockPos(w, this.getY() + 1.0, x);
                if (!this.world.doesNotCollide(this, box4.offset(0.0, 1.0, 0.0)) || !this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, this)) continue;
                q = w;
                r = this.getY() + 2.0;
                s = x;
            }
        }
        this.requestTeleport(q, r, s);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    protected float getJumpVelocity() {
        return 0.42f * this.method_23313();
    }

    protected void jump() {
        float f = this.getJumpVelocity();
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            f += 0.1f * (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1);
        }
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, f, vec3d.z);
        if (this.isSprinting()) {
            float g = this.yaw * ((float)Math.PI / 180);
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(g) * 0.2f, 0.0, MathHelper.cos(g) * 0.2f));
        }
        this.velocityDirty = true;
    }

    @Environment(value=EnvType.CLIENT)
    protected void knockDownwards() {
        this.setVelocity(this.getVelocity().add(0.0, -0.04f, 0.0));
    }

    protected void swimUpward(net.minecraft.tag.Tag<Fluid> tag) {
        this.setVelocity(this.getVelocity().add(0.0, 0.04f, 0.0));
    }

    protected float getBaseMovementSpeedMultiplier() {
        return 0.8f;
    }

    public void travel(Vec3d vec3d) {
        double r;
        double s;
        double d;
        if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
            boolean bl;
            d = 0.08;
            boolean bl2 = bl = this.getVelocity().y <= 0.0;
            if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                d = 0.01;
                this.fallDistance = 0.0f;
            }
            if (!(!this.isInsideWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying)) {
                Vec3d vec3d3;
                double e = this.getY();
                float f = this.isSprinting() ? 0.9f : this.getBaseMovementSpeedMultiplier();
                float g = 0.02f;
                float h = EnchantmentHelper.getDepthStrider(this);
                if (h > 3.0f) {
                    h = 3.0f;
                }
                if (!this.onGround) {
                    h *= 0.5f;
                }
                if (h > 0.0f) {
                    f += (0.54600006f - f) * h / 3.0f;
                    g += (this.getMovementSpeed() - g) * h / 3.0f;
                }
                if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                    f = 0.96f;
                }
                this.updateVelocity(g, vec3d);
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d2 = this.getVelocity();
                if (this.horizontalCollision && this.isClimbing()) {
                    vec3d2 = new Vec3d(vec3d2.x, 0.2, vec3d2.z);
                }
                this.setVelocity(vec3d2.multiply(f, 0.8f, f));
                if (!this.hasNoGravity() && !this.isSprinting()) {
                    vec3d3 = this.getVelocity();
                    double i = bl && Math.abs(vec3d3.y - 0.005) >= 0.003 && Math.abs(vec3d3.y - d / 16.0) < 0.003 ? -0.003 : vec3d3.y - d / 16.0;
                    this.setVelocity(vec3d3.x, i, vec3d3.z);
                }
                vec3d3 = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(vec3d3.x, vec3d3.y + (double)0.6f - this.getY() + e, vec3d3.z)) {
                    this.setVelocity(vec3d3.x, 0.3f, vec3d3.z);
                }
            } else if (!(!this.isInLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying)) {
                double e = this.getY();
                this.updateVelocity(0.02f, vec3d);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));
                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
                }
                Vec3d vec3d4 = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(vec3d4.x, vec3d4.y + (double)0.6f - this.getY() + e, vec3d4.z)) {
                    this.setVelocity(vec3d4.x, 0.3f, vec3d4.z);
                }
            } else if (this.isFallFlying()) {
                double n;
                float o;
                double m;
                Vec3d vec3d5 = this.getVelocity();
                if (vec3d5.y > -0.5) {
                    this.fallDistance = 1.0f;
                }
                Vec3d vec3d6 = this.getRotationVector();
                float f = this.pitch * ((float)Math.PI / 180);
                double j = Math.sqrt(vec3d6.x * vec3d6.x + vec3d6.z * vec3d6.z);
                double k = Math.sqrt(LivingEntity.squaredHorizontalLength(vec3d5));
                double i = vec3d6.length();
                float l = MathHelper.cos(f);
                l = (float)((double)l * ((double)l * Math.min(1.0, i / 0.4)));
                vec3d5 = this.getVelocity().add(0.0, d * (-1.0 + (double)l * 0.75), 0.0);
                if (vec3d5.y < 0.0 && j > 0.0) {
                    m = vec3d5.y * -0.1 * (double)l;
                    vec3d5 = vec3d5.add(vec3d6.x * m / j, m, vec3d6.z * m / j);
                }
                if (f < 0.0f && j > 0.0) {
                    m = k * (double)(-MathHelper.sin(f)) * 0.04;
                    vec3d5 = vec3d5.add(-vec3d6.x * m / j, m * 3.2, -vec3d6.z * m / j);
                }
                if (j > 0.0) {
                    vec3d5 = vec3d5.add((vec3d6.x / j * k - vec3d5.x) * 0.1, 0.0, (vec3d6.z / j * k - vec3d5.z) * 0.1);
                }
                this.setVelocity(vec3d5.multiply(0.99f, 0.98f, 0.99f));
                this.move(MovementType.SELF, this.getVelocity());
                if (this.horizontalCollision && !this.world.isClient && (o = (float)((n = k - (m = Math.sqrt(LivingEntity.squaredHorizontalLength(this.getVelocity())))) * 10.0 - 3.0)) > 0.0f) {
                    this.playSound(this.getFallSound((int)o), 1.0f, 1.0f);
                    this.damage(DamageSource.FLY_INTO_WALL, o);
                }
                if (this.onGround && !this.world.isClient) {
                    this.setFlag(7, false);
                }
            } else {
                BlockPos blockPos = this.method_23314();
                float p = this.world.getBlockState(blockPos).getBlock().getSlipperiness();
                float f = this.onGround ? p * 0.91f : 0.91f;
                this.updateVelocity(this.getMovementSpeed(p), vec3d);
                this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d7 = this.getVelocity();
                if ((this.horizontalCollision || this.jumping) && this.isClimbing()) {
                    vec3d7 = new Vec3d(vec3d7.x, 0.2, vec3d7.z);
                }
                double q = vec3d7.y;
                if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                    q += (0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - vec3d7.y) * 0.2;
                    this.fallDistance = 0.0f;
                } else if (!this.world.isClient || this.world.isChunkLoaded(blockPos)) {
                    if (!this.hasNoGravity()) {
                        q -= d;
                    }
                } else {
                    q = this.getY() > 0.0 ? -0.1 : 0.0;
                }
                this.setVelocity(vec3d7.x * (double)f, q * (double)0.98f, vec3d7.z * (double)f);
            }
        }
        this.lastLimbDistance = this.limbDistance;
        d = this.getX() - this.prevX;
        float g = MathHelper.sqrt(d * d + (s = this instanceof Flutterer ? this.getY() - this.prevY : 0.0) * s + (r = this.getZ() - this.prevZ) * r) * 4.0f;
        if (g > 1.0f) {
            g = 1.0f;
        }
        this.limbDistance += (g - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    private Vec3d applyClimbingSpeed(Vec3d vec3d) {
        if (this.isClimbing()) {
            this.fallDistance = 0.0f;
            float f = 0.15f;
            double d = MathHelper.clamp(vec3d.x, (double)-0.15f, (double)0.15f);
            double e = MathHelper.clamp(vec3d.z, (double)-0.15f, (double)0.15f);
            double g = Math.max(vec3d.y, (double)-0.15f);
            if (g < 0.0 && this.getBlockState().getBlock() != Blocks.SCAFFOLDING && this.method_21754() && this instanceof PlayerEntity) {
                g = 0.0;
            }
            vec3d = new Vec3d(d, g, e);
        }
        return vec3d;
    }

    private float getMovementSpeed(float f) {
        if (this.onGround) {
            return this.getMovementSpeed() * (0.21600002f / (f * f * f));
        }
        return this.flyingSpeed;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    public void setMovementSpeed(float f) {
        this.movementSpeed = f;
    }

    public boolean tryAttack(Entity entity) {
        this.onAttacking(entity);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickActiveItemStack();
        this.updateLeaningPitch();
        if (!this.world.isClient) {
            int j;
            int i = this.getStuckArrowCount();
            if (i > 0) {
                if (this.stuckArrowTimer <= 0) {
                    this.stuckArrowTimer = 20 * (30 - i);
                }
                --this.stuckArrowTimer;
                if (this.stuckArrowTimer <= 0) {
                    this.setStuckArrowCount(i - 1);
                }
            }
            if ((j = this.getStingerCount()) > 0) {
                if (this.field_20347 <= 0) {
                    this.field_20347 = 20 * (30 - j);
                }
                --this.field_20347;
                if (this.field_20347 <= 0) {
                    this.setStingerCount(j - 1);
                }
            }
            block8: for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack;
                switch (equipmentSlot.getType()) {
                    case HAND: {
                        itemStack = this.equippedHand.get(equipmentSlot.getEntitySlotId());
                        break;
                    }
                    case ARMOR: {
                        itemStack = this.equippedArmor.get(equipmentSlot.getEntitySlotId());
                        break;
                    }
                    default: {
                        continue block8;
                    }
                }
                ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
                if (ItemStack.areEqualIgnoreDamage(itemStack2, itemStack)) continue;
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), equipmentSlot, itemStack2));
                if (!itemStack.isEmpty()) {
                    this.getAttributes().removeAll(itemStack.getAttributeModifiers(equipmentSlot));
                }
                if (!itemStack2.isEmpty()) {
                    this.getAttributes().replaceAll(itemStack2.getAttributeModifiers(equipmentSlot));
                }
                switch (equipmentSlot.getType()) {
                    case HAND: {
                        this.equippedHand.set(equipmentSlot.getEntitySlotId(), itemStack2.copy());
                        continue block8;
                    }
                    case ARMOR: {
                        this.equippedArmor.set(equipmentSlot.getEntitySlotId(), itemStack2.copy());
                    }
                }
            }
            if (this.age % 20 == 0) {
                this.getDamageTracker().update();
            }
            if (!this.glowing) {
                boolean bl = this.hasStatusEffect(StatusEffects.GLOWING);
                if (this.getFlag(6) != bl) {
                    this.setFlag(6, bl);
                }
            }
            if (this.isSleeping() && !this.isSleepingInBed()) {
                this.wakeUp();
            }
        }
        this.tickMovement();
        double d = this.getX() - this.prevX;
        double e = this.getZ() - this.prevZ;
        float f = (float)(d * d + e * e);
        float g = this.bodyYaw;
        float h = 0.0f;
        this.prevStepBobbingAmount = this.stepBobbingAmount;
        float k = 0.0f;
        if (f > 0.0025000002f) {
            k = 1.0f;
            h = (float)Math.sqrt(f) * 3.0f;
            float l = (float)MathHelper.atan2(e, d) * 57.295776f - 90.0f;
            float m = MathHelper.abs(MathHelper.wrapDegrees(this.yaw) - l);
            g = 95.0f < m && m < 265.0f ? l - 180.0f : l;
        }
        if (this.handSwingProgress > 0.0f) {
            g = this.yaw;
        }
        if (!this.onGround) {
            k = 0.0f;
        }
        this.stepBobbingAmount += (k - this.stepBobbingAmount) * 0.3f;
        this.world.getProfiler().push("headTurn");
        h = this.turnHead(g, h);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rangeChecks");
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        while (this.bodyYaw - this.prevBodyYaw < -180.0f) {
            this.prevBodyYaw -= 360.0f;
        }
        while (this.bodyYaw - this.prevBodyYaw >= 180.0f) {
            this.prevBodyYaw += 360.0f;
        }
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.headYaw - this.prevHeadYaw < -180.0f) {
            this.prevHeadYaw -= 360.0f;
        }
        while (this.headYaw - this.prevHeadYaw >= 180.0f) {
            this.prevHeadYaw += 360.0f;
        }
        this.world.getProfiler().pop();
        this.lookDirection += h;
        this.roll = this.isFallFlying() ? ++this.roll : 0;
        if (this.isSleeping()) {
            this.pitch = 0.0f;
        }
    }

    protected float turnHead(float f, float g) {
        boolean bl;
        float h = MathHelper.wrapDegrees(f - this.bodyYaw);
        this.bodyYaw += h * 0.3f;
        float i = MathHelper.wrapDegrees(this.yaw - this.bodyYaw);
        boolean bl2 = bl = i < -90.0f || i >= 90.0f;
        if (i < -75.0f) {
            i = -75.0f;
        }
        if (i >= 75.0f) {
            i = 75.0f;
        }
        this.bodyYaw = this.yaw - i;
        if (i * i > 2500.0f) {
            this.bodyYaw += i * 0.2f;
        }
        if (bl) {
            g *= -1.0f;
        }
        return g;
    }

    public void tickMovement() {
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }
        if (this.isLogicalSideForUpdatingMovement()) {
            this.bodyTrackingIncrements = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.bodyTrackingIncrements > 0) {
            double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
            double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
            double f = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
            double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
            this.yaw = (float)((double)this.yaw + g / (double)this.bodyTrackingIncrements);
            this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
            --this.bodyTrackingIncrements;
            this.setPosition(d, e, f);
            this.setRotation(this.yaw, this.pitch);
        } else if (!this.canMoveVoluntarily()) {
            this.setVelocity(this.getVelocity().multiply(0.98));
        }
        if (this.headTrackingIncrements > 0) {
            this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
            --this.headTrackingIncrements;
        }
        Vec3d vec3d = this.getVelocity();
        double h = vec3d.x;
        double i = vec3d.y;
        double j = vec3d.z;
        if (Math.abs(vec3d.x) < 0.003) {
            h = 0.0;
        }
        if (Math.abs(vec3d.y) < 0.003) {
            i = 0.0;
        }
        if (Math.abs(vec3d.z) < 0.003) {
            j = 0.0;
        }
        this.setVelocity(h, i, j);
        this.world.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0f;
            this.forwardSpeed = 0.0f;
        } else if (this.canMoveVoluntarily()) {
            this.world.getProfiler().push("newAi");
            this.tickNewAi();
            this.world.getProfiler().pop();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("jump");
        if (this.jumping) {
            if (this.waterHeight > 0.0 && (!this.onGround || this.waterHeight > 0.4)) {
                this.swimUpward(FluidTags.WATER);
            } else if (this.isInLava()) {
                this.swimUpward(FluidTags.LAVA);
            } else if ((this.onGround || this.waterHeight > 0.0 && this.waterHeight <= 0.4) && this.jumpingCooldown == 0) {
                this.jump();
                this.jumpingCooldown = 10;
            }
        } else {
            this.jumpingCooldown = 0;
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("travel");
        this.sidewaysSpeed *= 0.98f;
        this.forwardSpeed *= 0.98f;
        this.initAi();
        Box box = this.getBoundingBox();
        this.travel(new Vec3d(this.sidewaysSpeed, this.upwardSpeed, this.forwardSpeed));
        this.world.getProfiler().pop();
        this.world.getProfiler().push("push");
        if (this.pushCooldown > 0) {
            --this.pushCooldown;
            this.push(box, this.getBoundingBox());
        }
        this.tickCramming();
        this.world.getProfiler().pop();
    }

    private void initAi() {
        boolean bl = this.getFlag(7);
        if (bl && !this.onGround && !this.hasVehicle()) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack)) {
                bl = true;
                if (!this.world.isClient && (this.roll + 1) % 20 == 0) {
                    itemStack.damage(1, this, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
                }
            } else {
                bl = false;
            }
        } else {
            bl = false;
        }
        if (!this.world.isClient) {
            this.setFlag(7, bl);
        }
    }

    protected void tickNewAi() {
    }

    protected void tickCramming() {
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            int j;
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                j = 0;
                for (int k = 0; k < list.size(); ++k) {
                    if (list.get(k).hasVehicle()) continue;
                    ++j;
                }
                if (j > i - 1) {
                    this.damage(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);
                this.pushAway(entity);
            }
        }
    }

    protected void push(Box box, Box box2) {
        Box box3 = box.union(box2);
        List<Entity> list = this.world.getEntities(this, box3);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (!(entity instanceof LivingEntity)) continue;
                this.attackLivingEntity((LivingEntity)entity);
                this.pushCooldown = 0;
                this.setVelocity(this.getVelocity().multiply(-0.2));
                break;
            }
        } else if (this.horizontalCollision) {
            this.pushCooldown = 0;
        }
        if (!this.world.isClient && this.pushCooldown <= 0) {
            this.setLivingFlag(4, false);
        }
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }

    protected void attackLivingEntity(LivingEntity livingEntity) {
    }

    public void method_6018(int i) {
        this.pushCooldown = i;
        if (!this.world.isClient) {
            this.setLivingFlag(4, true);
        }
    }

    public boolean isUsingRiptide() {
        return (this.dataTracker.get(LIVING_FLAGS) & 4) != 0;
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.world.isClient) {
            this.onDismounted(entity);
        }
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.prevStepBobbingAmount = this.stepBobbingAmount;
        this.stepBobbingAmount = 0.0f;
        this.fallDistance = 0.0f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.serverX = d;
        this.serverY = e;
        this.serverZ = f;
        this.serverYaw = g;
        this.serverPitch = h;
        this.bodyTrackingIncrements = i;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedHeadRotation(float f, int i) {
        this.serverHeadYaw = f;
        this.headTrackingIncrements = i;
    }

    public void setJumping(boolean bl) {
        this.jumping = bl;
    }

    public void sendPickup(Entity entity, int i) {
        if (!entity.removed && !this.world.isClient && (entity instanceof ItemEntity || entity instanceof ProjectileEntity || entity instanceof ExperienceOrbEntity)) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(entity, new ItemPickupAnimationS2CPacket(entity.getEntityId(), this.getEntityId(), i));
        }
    }

    public boolean canSee(Entity entity) {
        Vec3d vec3d2;
        Vec3d vec3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
        return this.world.rayTrace(new RayTraceContext(vec3d, vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ()), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public float getYaw(float f) {
        if (f == 1.0f) {
            return this.headYaw;
        }
        return MathHelper.lerp(f, this.prevHeadYaw, this.headYaw);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHandSwingProgress(float f) {
        float g = this.handSwingProgress - this.lastHandSwingProgress;
        if (g < 0.0f) {
            g += 1.0f;
        }
        return this.lastHandSwingProgress + g * f;
    }

    public boolean canMoveVoluntarily() {
        return !this.world.isClient;
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.isClimbing();
    }

    @Override
    protected void scheduleVelocityUpdate() {
        this.velocityModified = this.random.nextDouble() >= this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue();
    }

    @Override
    public float getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public void setHeadYaw(float f) {
        this.headYaw = f;
    }

    @Override
    public void setYaw(float f) {
        this.bodyYaw = f;
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.absorptionAmount = f;
    }

    public void enterCombat() {
    }

    public void endCombat() {
    }

    protected void markEffectsDirty() {
        this.effectsChanged = true;
    }

    public abstract Arm getMainArm();

    public boolean isUsingItem() {
        return (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
    }

    public Hand getActiveHand() {
        return (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    private void tickActiveItemStack() {
        if (this.isUsingItem()) {
            if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
                this.activeItemStack.usageTick(this.world, this, this.getItemUseTimeLeft());
                if (this.method_22382()) {
                    this.spawnConsumptionEffects(this.activeItemStack, 5);
                }
                if (--this.itemUseTimeLeft == 0 && !this.world.isClient && !this.activeItemStack.isUsedOnRelease()) {
                    this.consumeItem();
                }
            } else {
                this.clearActiveItem();
            }
        }
    }

    private boolean method_22382() {
        int i = this.getItemUseTimeLeft();
        FoodComponent foodComponent = this.activeItemStack.getItem().getFoodComponent();
        boolean bl = foodComponent != null && foodComponent.isSnack();
        return (bl |= i <= this.activeItemStack.getMaxUseTime() - 7) && i % 4 == 0;
    }

    private void updateLeaningPitch() {
        this.lastLeaningPitch = this.leaningPitch;
        this.leaningPitch = this.isInSwimmingPose() ? Math.min(1.0f, this.leaningPitch + 0.09f) : Math.max(0.0f, this.leaningPitch - 0.09f);
    }

    protected void setLivingFlag(int i, boolean bl) {
        int j = this.dataTracker.get(LIVING_FLAGS).byteValue();
        j = bl ? (j |= i) : (j &= ~i);
        this.dataTracker.set(LIVING_FLAGS, (byte)j);
    }

    public void setCurrentHand(Hand hand) {
        ItemStack itemStack = this.getStackInHand(hand);
        if (itemStack.isEmpty() || this.isUsingItem()) {
            return;
        }
        this.activeItemStack = itemStack;
        this.itemUseTimeLeft = itemStack.getMaxUseTime();
        if (!this.world.isClient) {
            this.setLivingFlag(1, true);
            this.setLivingFlag(2, hand == Hand.OFF_HAND);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> trackedData) {
        super.onTrackedDataSet(trackedData);
        if (SLEEPING_POSITION.equals(trackedData)) {
            if (this.world.isClient) {
                this.getSleepingPosition().ifPresent(this::setPositionInBed);
            }
        } else if (LIVING_FLAGS.equals(trackedData) && this.world.isClient) {
            if (this.isUsingItem() && this.activeItemStack.isEmpty()) {
                this.activeItemStack = this.getStackInHand(this.getActiveHand());
                if (!this.activeItemStack.isEmpty()) {
                    this.itemUseTimeLeft = this.activeItemStack.getMaxUseTime();
                }
            } else if (!this.isUsingItem() && !this.activeItemStack.isEmpty()) {
                this.activeItemStack = ItemStack.EMPTY;
                this.itemUseTimeLeft = 0;
            }
        }
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
        super.lookAt(entityAnchor, vec3d);
        this.prevHeadYaw = this.headYaw;
        this.prevBodyYaw = this.bodyYaw = this.headYaw;
    }

    protected void spawnConsumptionEffects(ItemStack itemStack, int i) {
        if (itemStack.isEmpty() || !this.isUsingItem()) {
            return;
        }
        if (itemStack.getUseAction() == UseAction.DRINK) {
            this.playSound(this.getDrinkSound(itemStack), 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
        }
        if (itemStack.getUseAction() == UseAction.EAT) {
            this.spawnItemParticles(itemStack, i);
            this.playSound(this.getEatSound(itemStack), 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }

    private void spawnItemParticles(ItemStack itemStack, int i) {
        for (int j = 0; j < i; ++j) {
            Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-this.pitch * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(-this.yaw * ((float)Math.PI / 180));
            double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-this.pitch * ((float)Math.PI / 180));
            vec3d2 = vec3d2.rotateY(-this.yaw * ((float)Math.PI / 180));
            vec3d2 = vec3d2.add(this.getX(), this.getEyeY(), this.getZ());
            this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }

    protected void consumeItem() {
        if (!this.activeItemStack.equals(this.getStackInHand(this.getActiveHand()))) {
            this.stopUsingItem();
            return;
        }
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.spawnConsumptionEffects(this.activeItemStack, 16);
            this.setStackInHand(this.getActiveHand(), this.activeItemStack.finishUsing(this.world, this));
            this.clearActiveItem();
        }
    }

    public ItemStack getActiveItem() {
        return this.activeItemStack;
    }

    public int getItemUseTimeLeft() {
        return this.itemUseTimeLeft;
    }

    public int getItemUseTime() {
        if (this.isUsingItem()) {
            return this.activeItemStack.getMaxUseTime() - this.getItemUseTimeLeft();
        }
        return 0;
    }

    public void stopUsingItem() {
        if (!this.activeItemStack.isEmpty()) {
            this.activeItemStack.onStoppedUsing(this.world, this, this.getItemUseTimeLeft());
            if (this.activeItemStack.isUsedOnRelease()) {
                this.tickActiveItemStack();
            }
        }
        this.clearActiveItem();
    }

    public void clearActiveItem() {
        if (!this.world.isClient) {
            this.setLivingFlag(1, false);
        }
        this.activeItemStack = ItemStack.EMPTY;
        this.itemUseTimeLeft = 0;
    }

    public boolean isBlocking() {
        if (!this.isUsingItem() || this.activeItemStack.isEmpty()) {
            return false;
        }
        Item item = this.activeItemStack.getItem();
        if (item.getUseAction(this.activeItemStack) != UseAction.BLOCK) {
            return false;
        }
        return item.getMaxUseTime(this.activeItemStack) - this.itemUseTimeLeft >= 5;
    }

    public boolean method_21754() {
        return this.isSneaking();
    }

    public boolean isFallFlying() {
        return this.getFlag(7);
    }

    @Override
    public boolean isInSwimmingPose() {
        return super.isInSwimmingPose() || !this.isFallFlying() && this.getPose() == EntityPose.FALL_FLYING;
    }

    @Environment(value=EnvType.CLIENT)
    public int getRoll() {
        return this.roll;
    }

    public boolean teleport(double d, double e, double f, boolean bl) {
        double g = this.getX();
        double h = this.getY();
        double i = this.getZ();
        double j = e;
        boolean bl2 = false;
        World world = this.world;
        BlockPos blockPos = new BlockPos(d, j, f);
        if (world.isChunkLoaded(blockPos)) {
            boolean bl3 = false;
            while (!bl3 && blockPos.getY() > 0) {
                BlockPos blockPos2 = blockPos.down();
                BlockState blockState = world.getBlockState(blockPos2);
                if (blockState.getMaterial().blocksMovement()) {
                    bl3 = true;
                    continue;
                }
                j -= 1.0;
                blockPos = blockPos2;
            }
            if (bl3) {
                this.requestTeleport(d, j, f);
                if (world.doesNotCollide(this) && !world.containsFluid(this.getBoundingBox())) {
                    bl2 = true;
                }
            }
        }
        if (!bl2) {
            this.requestTeleport(g, h, i);
            return false;
        }
        if (bl) {
            world.sendEntityStatus(this, (byte)46);
        }
        if (this instanceof MobEntityWithAi) {
            ((MobEntityWithAi)this).getNavigation().stop();
        }
        return true;
    }

    public boolean isAffectedBySplashPotions() {
        return true;
    }

    public boolean method_6102() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public void setNearbySongPlaying(BlockPos blockPos, boolean bl) {
    }

    public boolean canPickUp(ItemStack itemStack) {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new MobSpawnS2CPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose entityPose) {
        return entityPose == EntityPose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(entityPose).scaled(this.getScaleFactor());
    }

    public Optional<BlockPos> getSleepingPosition() {
        return this.dataTracker.get(SLEEPING_POSITION);
    }

    public void setSleepingPosition(BlockPos blockPos) {
        this.dataTracker.set(SLEEPING_POSITION, Optional.of(blockPos));
    }

    public void clearSleepingPosition() {
        this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getSleepingPosition().isPresent();
    }

    public void sleep(BlockPos blockPos) {
        BlockState blockState;
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        if ((blockState = this.world.getBlockState(blockPos)).getBlock() instanceof BedBlock) {
            this.world.setBlockState(blockPos, (BlockState)blockState.with(BedBlock.OCCUPIED, true), 3);
        }
        this.setPose(EntityPose.SLEEPING);
        this.setPositionInBed(blockPos);
        this.setSleepingPosition(blockPos);
        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;
    }

    private void setPositionInBed(BlockPos blockPos) {
        this.setPosition((double)blockPos.getX() + 0.5, (float)blockPos.getY() + 0.6875f, (double)blockPos.getZ() + 0.5);
    }

    private boolean isSleepingInBed() {
        return this.getSleepingPosition().map(blockPos -> this.world.getBlockState((BlockPos)blockPos).getBlock() instanceof BedBlock).orElse(false);
    }

    public void wakeUp() {
        this.getSleepingPosition().filter(this.world::isChunkLoaded).ifPresent(blockPos -> {
            BlockState blockState = this.world.getBlockState((BlockPos)blockPos);
            if (blockState.getBlock() instanceof BedBlock) {
                this.world.setBlockState((BlockPos)blockPos, (BlockState)blockState.with(BedBlock.OCCUPIED, false), 3);
                Vec3d vec3d = BedBlock.findWakeUpPosition(this.getType(), this.world, blockPos, 0).orElseGet(() -> {
                    BlockPos blockPos2 = blockPos.up();
                    return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
                });
                this.setPosition(vec3d.x, vec3d.y, vec3d.z);
            }
        });
        this.setPose(EntityPose.STANDING);
        this.clearSleepingPosition();
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Direction getSleepingDirection() {
        BlockPos blockPos = this.getSleepingPosition().orElse(null);
        return blockPos != null ? BedBlock.getDirection(this.world, blockPos) : null;
    }

    @Override
    public boolean isInsideWall() {
        return !this.isSleeping() && super.isInsideWall();
    }

    @Override
    protected final float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return entityPose == EntityPose.SLEEPING ? 0.2f : this.getActiveEyeHeight(entityPose, entityDimensions);
    }

    protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return super.getEyeHeight(entityPose, entityDimensions);
    }

    public ItemStack getArrowType(ItemStack itemStack) {
        return ItemStack.EMPTY;
    }

    public ItemStack eatFood(World world, ItemStack itemStack) {
        if (itemStack.isFood()) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatSound(itemStack), SoundCategory.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
            this.applyFoodEffects(itemStack, world, this);
            if (!(this instanceof PlayerEntity) || !((PlayerEntity)this).abilities.creativeMode) {
                itemStack.decrement(1);
            }
        }
        return itemStack;
    }

    private void applyFoodEffects(ItemStack itemStack, World world, LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        if (item.isFood()) {
            List<Pair<StatusEffectInstance, Float>> list = item.getFoodComponent().getStatusEffects();
            for (Pair<StatusEffectInstance, Float> pair : list) {
                if (world.isClient || pair.getLeft() == null || !(world.random.nextFloat() < pair.getRight().floatValue())) continue;
                livingEntity.addStatusEffect(new StatusEffectInstance(pair.getLeft()));
            }
        }
    }

    private static byte getEquipmentBreakStatus(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot) {
            case MAINHAND: {
                return 47;
            }
            case OFFHAND: {
                return 48;
            }
            case HEAD: {
                return 49;
            }
            case CHEST: {
                return 50;
            }
            case FEET: {
                return 52;
            }
            case LEGS: {
                return 51;
            }
        }
        return 47;
    }

    public void sendEquipmentBreakStatus(EquipmentSlot equipmentSlot) {
        this.world.sendEntityStatus(this, LivingEntity.getEquipmentBreakStatus(equipmentSlot));
    }

    public void sendToolBreakStatus(Hand hand) {
        this.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }
}

