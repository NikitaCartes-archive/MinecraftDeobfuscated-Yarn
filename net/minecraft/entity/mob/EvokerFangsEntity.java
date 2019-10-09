/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EvokerFangsEntity
extends Entity {
    private int warmup;
    private boolean field_7610;
    private int ticksLeft = 22;
    private boolean hasAttacked;
    private LivingEntity owner;
    private UUID ownerUuid;

    public EvokerFangsEntity(EntityType<? extends EvokerFangsEntity> entityType, World world) {
        super(entityType, world);
    }

    public EvokerFangsEntity(World world, double d, double e, double f, float g, int i, LivingEntity livingEntity) {
        this((EntityType<? extends EvokerFangsEntity>)EntityType.EVOKER_FANGS, world);
        this.warmup = i;
        this.setOwner(livingEntity);
        this.yaw = g * 57.295776f;
        this.setPosition(d, e, f);
    }

    @Override
    protected void initDataTracker() {
    }

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUuid = livingEntity == null ? null : livingEntity.getUuid();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld && (entity = ((ServerWorld)this.world).getEntity(this.ownerUuid)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag compoundTag) {
        this.warmup = compoundTag.getInt("Warmup");
        if (compoundTag.containsUuid("OwnerUUID")) {
            this.ownerUuid = compoundTag.getUuid("OwnerUUID");
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putInt("Warmup", this.warmup);
        if (this.ownerUuid != null) {
            compoundTag.putUuid("OwnerUUID", this.ownerUuid);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.hasAttacked) {
                --this.ticksLeft;
                if (this.ticksLeft == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double e = this.getY() + 0.05 + this.random.nextDouble();
                        double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        double h = 0.3 + this.random.nextDouble() * 0.3;
                        double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.world.addParticle(ParticleTypes.CRIT, d, e + 1.0, f, g, h, j);
                    }
                }
            }
        } else if (--this.warmup < 0) {
            if (this.warmup == -8) {
                List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2));
                for (LivingEntity livingEntity : list) {
                    this.damage(livingEntity);
                }
            }
            if (!this.field_7610) {
                this.world.sendEntityStatus(this, (byte)4);
                this.field_7610 = true;
            }
            if (--this.ticksLeft < 0) {
                this.remove();
            }
        }
    }

    private void damage(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return;
        }
        if (livingEntity2 == null) {
            livingEntity.damage(DamageSource.MAGIC, 6.0f);
        } else {
            if (livingEntity2.isTeammate(livingEntity)) {
                return;
            }
            livingEntity.damage(DamageSource.magic(this, livingEntity2), 6.0f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        super.handleStatus(b);
        if (b == 4) {
            this.hasAttacked = true;
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0f, this.random.nextFloat() * 0.2f + 0.85f, false);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getAnimationProgress(float f) {
        if (!this.hasAttacked) {
            return 0.0f;
        }
        int i = this.ticksLeft - 2;
        if (i <= 0) {
            return 1.0f;
        }
        return 1.0f - ((float)i - f) / 20.0f;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

