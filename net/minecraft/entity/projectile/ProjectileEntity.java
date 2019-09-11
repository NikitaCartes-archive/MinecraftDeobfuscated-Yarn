/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectileEntity
extends Entity
implements Projectile {
    private static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Optional<UUID>> field_7580 = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    @Nullable
    private BlockState inBlockState;
    protected boolean inGround;
    protected int inGroundTime;
    public PickupPermission pickupType = PickupPermission.DISALLOWED;
    public int shake;
    public UUID ownerUuid;
    private int life;
    private int field_7577;
    private double damage = 2.0;
    private int field_7575;
    private SoundEvent sound = this.getSound();
    private IntOpenHashSet piercedEntities;
    private List<Entity> piercingKilledEntities;

    protected ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, double d, double e, double f, World world) {
        this(entityType, world);
        this.setPosition(d, e, f);
    }

    protected ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, LivingEntity livingEntity, World world) {
        this(entityType, livingEntity.x, livingEntity.y + (double)livingEntity.getStandingEyeHeight() - (double)0.1f, livingEntity.z, world);
        this.setOwner(livingEntity);
        if (livingEntity instanceof PlayerEntity) {
            this.pickupType = PickupPermission.ALLOWED;
        }
    }

    public void setSound(SoundEvent soundEvent) {
        this.sound = soundEvent;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderAtDistance(double d) {
        double e = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(e)) {
            e = 1.0;
        }
        return d < (e *= 64.0 * ProjectileEntity.getRenderDistanceMultiplier()) * e;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(PROJECTILE_FLAGS, (byte)0);
        this.dataTracker.startTracking(field_7580, Optional.empty());
        this.dataTracker.startTracking(PIERCE_LEVEL, (byte)0);
    }

    public void setProperties(Entity entity, float f, float g, float h, float i, float j) {
        float k = -MathHelper.sin(g * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        float l = -MathHelper.sin(f * ((float)Math.PI / 180));
        float m = MathHelper.cos(g * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
        this.setVelocity(k, l, m, i, j);
        this.setVelocity(this.getVelocity().add(entity.getVelocity().x, entity.onGround ? 0.0 : entity.getVelocity().y, entity.getVelocity().z));
    }

    @Override
    public void setVelocity(double d, double e, double f, float g, float h) {
        Vec3d vec3d = new Vec3d(d, e, f).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h, this.random.nextGaussian() * (double)0.0075f * (double)h).multiply(g);
        this.setVelocity(vec3d);
        float i = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, i) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.life = 0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.setPosition(d, e, f);
        this.setRotation(g, h);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double d, double e, double f) {
        this.setVelocity(d, e, f);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float g = MathHelper.sqrt(d * d + f * f);
            this.pitch = (float)(MathHelper.atan2(e, g) * 57.2957763671875);
            this.yaw = (float)(MathHelper.atan2(d, f) * 57.2957763671875);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
            this.life = 0;
        }
    }

    @Override
    public void tick() {
        VoxelShape voxelShape;
        BlockPos blockPos;
        BlockState blockState;
        super.tick();
        boolean bl = this.isNoClip();
        Vec3d vec3d = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float f = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(vec3d));
            this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(vec3d.y, f) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
        if (!((blockState = this.world.getBlockState(blockPos = new BlockPos(this.x, this.y, this.z))).isAir() || bl || (voxelShape = blockState.getCollisionShape(this.world, blockPos)).isEmpty())) {
            for (Box box : voxelShape.getBoundingBoxes()) {
                if (!box.offset(blockPos).contains(new Vec3d(this.x, this.y, this.z))) continue;
                this.inGround = true;
                break;
            }
        }
        if (this.shake > 0) {
            --this.shake;
        }
        if (this.isInsideWaterOrRain()) {
            this.extinguish();
        }
        if (this.inGround && !bl) {
            if (this.inBlockState != blockState && this.world.doesNotCollide(this.getBoundingBox().expand(0.06))) {
                this.inGround = false;
                this.setVelocity(vec3d.multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
                this.life = 0;
                this.field_7577 = 0;
            } else if (!this.world.isClient) {
                this.age();
            }
            ++this.inGroundTime;
            return;
        }
        this.inGroundTime = 0;
        ++this.field_7577;
        Vec3d vec3d2 = new Vec3d(this.x, this.y, this.z);
        Vec3d vec3d3 = vec3d2.add(vec3d);
        HitResult hitResult = this.world.rayTrace(new RayTraceContext(vec3d2, vec3d3, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d3 = hitResult.getPos();
        }
        while (!this.removed) {
            EntityHitResult entityHitResult = this.getEntityCollision(vec3d2, vec3d3);
            if (entityHitResult != null) {
                hitResult = entityHitResult;
            }
            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                Entity entity2 = this.getOwner();
                if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
                    hitResult = null;
                    entityHitResult = null;
                }
            }
            if (hitResult != null && !bl) {
                this.onHit(hitResult);
                this.velocityDirty = true;
            }
            if (entityHitResult == null || this.getPierceLevel() <= 0) break;
            hitResult = null;
        }
        vec3d = this.getVelocity();
        double d = vec3d.x;
        double e = vec3d.y;
        double g = vec3d.z;
        if (this.isCritical()) {
            for (int i = 0; i < 4; ++i) {
                this.world.addParticle(ParticleTypes.CRIT, this.x + d * (double)i / 4.0, this.y + e * (double)i / 4.0, this.z + g * (double)i / 4.0, -d, -e + 0.2, -g);
            }
        }
        this.x += d;
        this.y += e;
        this.z += g;
        float h = MathHelper.sqrt(ProjectileEntity.squaredHorizontalLength(vec3d));
        this.yaw = bl ? (float)(MathHelper.atan2(-d, -g) * 57.2957763671875) : (float)(MathHelper.atan2(d, g) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(e, h) * 57.2957763671875);
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        this.pitch = MathHelper.lerp(0.2f, this.prevPitch, this.pitch);
        this.yaw = MathHelper.lerp(0.2f, this.prevYaw, this.yaw);
        float j = 0.99f;
        float k = 0.05f;
        if (this.isInsideWater()) {
            for (int l = 0; l < 4; ++l) {
                float m = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, this.x - d * 0.25, this.y - e * 0.25, this.z - g * 0.25, d, e, g);
            }
            j = this.getDragInWater();
        }
        this.setVelocity(vec3d.multiply(j));
        if (!this.hasNoGravity() && !bl) {
            Vec3d vec3d4 = this.getVelocity();
            this.setVelocity(vec3d4.x, vec3d4.y - (double)0.05f, vec3d4.z);
        }
        this.setPosition(this.x, this.y, this.z);
        this.checkBlockCollision();
    }

    protected void age() {
        ++this.life;
        if (this.life >= 1200) {
            this.remove();
        }
    }

    protected void onHit(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
        } else if (type == HitResult.Type.BLOCK) {
            BlockState blockState;
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            this.inBlockState = blockState = this.world.getBlockState(blockHitResult.getBlockPos());
            Vec3d vec3d = blockHitResult.getPos().subtract(this.x, this.y, this.z);
            this.setVelocity(vec3d);
            Vec3d vec3d2 = vec3d.normalize().multiply(0.05f);
            this.x -= vec3d2.x;
            this.y -= vec3d2.y;
            this.z -= vec3d2.z;
            this.playSound(this.method_20011(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            this.inGround = true;
            this.shake = 7;
            this.setCritical(false);
            this.setPierceLevel((byte)0);
            this.setSound(SoundEvents.ENTITY_ARROW_HIT);
            this.setShotFromCrossbow(false);
            this.clearPiercingStatus();
            blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
        }
    }

    private void clearPiercingStatus() {
        if (this.piercingKilledEntities != null) {
            this.piercingKilledEntities.clear();
        }
        if (this.piercedEntities != null) {
            this.piercedEntities.clear();
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        DamageSource damageSource;
        Entity entity2;
        Entity entity = entityHitResult.getEntity();
        float f = (float)this.getVelocity().length();
        int i = MathHelper.ceil(Math.max((double)f * this.damage, 0.0));
        if (this.getPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }
            if (this.piercingKilledEntities == null) {
                this.piercingKilledEntities = Lists.newArrayListWithCapacity(5);
            }
            if (this.piercedEntities.size() < this.getPierceLevel() + 1) {
                this.piercedEntities.add(entity.getEntityId());
            } else {
                this.remove();
                return;
            }
        }
        if (this.isCritical()) {
            i += this.random.nextInt(i / 2 + 2);
        }
        if ((entity2 = this.getOwner()) == null) {
            damageSource = DamageSource.arrow(this, this);
        } else {
            damageSource = DamageSource.arrow(this, entity2);
            if (entity2 instanceof LivingEntity) {
                ((LivingEntity)entity2).onAttacking(entity);
            }
        }
        int j = entity.getFireTime();
        if (this.isOnFire() && !(entity instanceof EndermanEntity)) {
            entity.setOnFireFor(5);
        }
        if (entity.damage(damageSource, i)) {
            if (entity instanceof LivingEntity) {
                Vec3d vec3d;
                LivingEntity livingEntity = (LivingEntity)entity;
                if (!this.world.isClient && this.getPierceLevel() <= 0) {
                    livingEntity.setStuckArrowCount(livingEntity.getStuckArrowCount() + 1);
                }
                if (this.field_7575 > 0 && (vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.field_7575 * 0.6)).lengthSquared() > 0.0) {
                    livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
                }
                if (!this.world.isClient && entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
                }
                this.onHit(livingEntity);
                if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(6, 0.0f));
                }
                if (!entity.isAlive() && this.piercingKilledEntities != null) {
                    this.piercingKilledEntities.add(livingEntity);
                }
                if (!this.world.isClient && entity2 instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
                    if (this.piercingKilledEntities != null && this.isShotFromCrossbow()) {
                        Criterions.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, this.piercingKilledEntities, this.piercingKilledEntities.size());
                    } else if (!entity.isAlive() && this.isShotFromCrossbow()) {
                        Criterions.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, Arrays.asList(entity), 0);
                    }
                }
            }
            this.playSound(this.sound, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if (this.getPierceLevel() <= 0 && !(entity instanceof EndermanEntity)) {
                this.remove();
            }
        } else {
            entity.setFireTime(j);
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.yaw += 180.0f;
            this.prevYaw += 180.0f;
            this.field_7577 = 0;
            if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1f);
                }
                this.remove();
            }
        }
    }

    protected SoundEvent getSound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    protected final SoundEvent method_20011() {
        return this.sound;
    }

    protected void onHit(LivingEntity livingEntity) {
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d vec3d, Vec3d vec3d2) {
        return ProjectileUtil.getEntityCollision(this.world, this, vec3d, vec3d2, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), entity -> !(entity.isSpectator() || !entity.isAlive() || !entity.collides() || entity == this.getOwner() && this.field_7577 < 5 || this.piercedEntities != null && this.piercedEntities.contains(entity.getEntityId())));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putShort("life", (short)this.life);
        if (this.inBlockState != null) {
            compoundTag.put("inBlockState", TagHelper.serializeBlockState(this.inBlockState));
        }
        compoundTag.putByte("shake", (byte)this.shake);
        compoundTag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        compoundTag.putByte("pickup", (byte)this.pickupType.ordinal());
        compoundTag.putDouble("damage", this.damage);
        compoundTag.putBoolean("crit", this.isCritical());
        compoundTag.putByte("PierceLevel", this.getPierceLevel());
        if (this.ownerUuid != null) {
            compoundTag.putUuid("OwnerUUID", this.ownerUuid);
        }
        compoundTag.putString("SoundEvent", Registry.SOUND_EVENT.getId(this.sound).toString());
        compoundTag.putBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.life = compoundTag.getShort("life");
        if (compoundTag.containsKey("inBlockState", 10)) {
            this.inBlockState = TagHelper.deserializeBlockState(compoundTag.getCompound("inBlockState"));
        }
        this.shake = compoundTag.getByte("shake") & 0xFF;
        boolean bl = this.inGround = compoundTag.getByte("inGround") == 1;
        if (compoundTag.containsKey("damage", 99)) {
            this.damage = compoundTag.getDouble("damage");
        }
        if (compoundTag.containsKey("pickup", 99)) {
            this.pickupType = PickupPermission.fromOrdinal(compoundTag.getByte("pickup"));
        } else if (compoundTag.containsKey("player", 99)) {
            this.pickupType = compoundTag.getBoolean("player") ? PickupPermission.ALLOWED : PickupPermission.DISALLOWED;
        }
        this.setCritical(compoundTag.getBoolean("crit"));
        this.setPierceLevel(compoundTag.getByte("PierceLevel"));
        if (compoundTag.containsUuid("OwnerUUID")) {
            this.ownerUuid = compoundTag.getUuid("OwnerUUID");
        }
        if (compoundTag.containsKey("SoundEvent", 8)) {
            this.sound = Registry.SOUND_EVENT.getOrEmpty(new Identifier(compoundTag.getString("SoundEvent"))).orElse(this.getSound());
        }
        this.setShotFromCrossbow(compoundTag.getBoolean("ShotFromCrossbow"));
    }

    public void setOwner(@Nullable Entity entity) {
        UUID uUID = this.ownerUuid = entity == null ? null : entity.getUuid();
        if (entity instanceof PlayerEntity) {
            this.pickupType = ((PlayerEntity)entity).abilities.creativeMode ? PickupPermission.CREATIVE_ONLY : PickupPermission.ALLOWED;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUuid != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntity(this.ownerUuid);
        }
        return null;
    }

    @Override
    public void onPlayerCollision(PlayerEntity playerEntity) {
        boolean bl;
        if (this.world.isClient || !this.inGround && !this.isNoClip() || this.shake > 0) {
            return;
        }
        boolean bl2 = bl = this.pickupType == PickupPermission.ALLOWED || this.pickupType == PickupPermission.CREATIVE_ONLY && playerEntity.abilities.creativeMode || this.isNoClip() && this.getOwner().getUuid() == playerEntity.getUuid();
        if (this.pickupType == PickupPermission.ALLOWED && !playerEntity.inventory.insertStack(this.asItemStack())) {
            bl = false;
        }
        if (bl) {
            playerEntity.sendPickup(this, 1);
            this.remove();
        }
    }

    protected abstract ItemStack asItemStack();

    @Override
    protected boolean canClimb() {
        return false;
    }

    public void setDamage(double d) {
        this.damage = d;
    }

    public double getDamage() {
        return this.damage;
    }

    public void method_7449(int i) {
        this.field_7575 = i;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return 0.0f;
    }

    public void setCritical(boolean bl) {
        this.setProjectileFlag(1, bl);
    }

    public void setPierceLevel(byte b) {
        this.dataTracker.set(PIERCE_LEVEL, b);
    }

    private void setProjectileFlag(int i, boolean bl) {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        if (bl) {
            this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b | i));
        } else {
            this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b & ~i));
        }
    }

    public boolean isCritical() {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        return (b & 1) != 0;
    }

    public boolean isShotFromCrossbow() {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        return (b & 4) != 0;
    }

    public byte getPierceLevel() {
        return this.dataTracker.get(PIERCE_LEVEL);
    }

    public void method_7435(LivingEntity livingEntity, float f) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.POWER, livingEntity);
        int j = EnchantmentHelper.getEquipmentLevel(Enchantments.PUNCH, livingEntity);
        this.setDamage((double)(f * 2.0f) + (this.random.nextGaussian() * 0.25 + (double)((float)this.world.getDifficulty().getId() * 0.11f)));
        if (i > 0) {
            this.setDamage(this.getDamage() + (double)i * 0.5 + 0.5);
        }
        if (j > 0) {
            this.method_7449(j);
        }
        if (EnchantmentHelper.getEquipmentLevel(Enchantments.FLAME, livingEntity) > 0) {
            this.setOnFireFor(100);
        }
    }

    protected float getDragInWater() {
        return 0.6f;
    }

    public void setNoClip(boolean bl) {
        this.noClip = bl;
        this.setProjectileFlag(2, bl);
    }

    public boolean isNoClip() {
        if (!this.world.isClient) {
            return this.noClip;
        }
        return (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
    }

    public void setShotFromCrossbow(boolean bl) {
        this.setProjectileFlag(4, bl);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getEntityId());
    }

    public static enum PickupPermission {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;


        public static PickupPermission fromOrdinal(int i) {
            if (i < 0 || i > PickupPermission.values().length) {
                i = 0;
            }
            return PickupPermission.values()[i];
        }
    }
}

