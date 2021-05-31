/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Angerable {
    public static final String ANGER_TIME_KEY = "AngerTime";
    public static final String ANGRY_AT_KEY = "AngryAt";

    public int getAngerTime();

    public void setAngerTime(int var1);

    @Nullable
    public UUID getAngryAt();

    public void setAngryAt(@Nullable UUID var1);

    public void chooseRandomAngerTime();

    default public void writeAngerToNbt(NbtCompound nbt) {
        nbt.putInt(ANGER_TIME_KEY, this.getAngerTime());
        if (this.getAngryAt() != null) {
            nbt.putUuid(ANGRY_AT_KEY, this.getAngryAt());
        }
    }

    default public void readAngerFromNbt(World world, NbtCompound nbt) {
        this.setAngerTime(nbt.getInt(ANGER_TIME_KEY));
        if (!(world instanceof ServerWorld)) {
            return;
        }
        if (!nbt.containsUuid(ANGRY_AT_KEY)) {
            this.setAngryAt(null);
            return;
        }
        UUID uUID = nbt.getUuid(ANGRY_AT_KEY);
        this.setAngryAt(uUID);
        Entity entity = ((ServerWorld)world).getEntity(uUID);
        if (entity == null) {
            return;
        }
        if (entity instanceof MobEntity) {
            this.setAttacker((MobEntity)entity);
        }
        if (entity.getType() == EntityType.PLAYER) {
            this.setAttacking((PlayerEntity)entity);
        }
    }

    default public void tickAngerLogic(ServerWorld world, boolean bl) {
        LivingEntity livingEntity = this.getTarget();
        UUID uUID = this.getAngryAt();
        if ((livingEntity == null || livingEntity.isDead()) && uUID != null && world.getEntity(uUID) instanceof MobEntity) {
            this.stopAnger();
            return;
        }
        if (livingEntity != null && !Objects.equals(uUID, livingEntity.getUuid())) {
            this.setAngryAt(livingEntity.getUuid());
            this.chooseRandomAngerTime();
        }
        if (!(this.getAngerTime() <= 0 || livingEntity != null && livingEntity.getType() == EntityType.PLAYER && bl)) {
            this.setAngerTime(this.getAngerTime() - 1);
            if (this.getAngerTime() == 0) {
                this.stopAnger();
            }
        }
    }

    default public boolean shouldAngerAt(LivingEntity livingEntity) {
        if (!this.canTarget(livingEntity)) {
            return false;
        }
        if (livingEntity.getType() == EntityType.PLAYER && this.isUniversallyAngry(livingEntity.world)) {
            return true;
        }
        return livingEntity.getUuid().equals(this.getAngryAt());
    }

    default public boolean isUniversallyAngry(World world) {
        return world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.hasAngerTime() && this.getAngryAt() == null;
    }

    default public boolean hasAngerTime() {
        return this.getAngerTime() > 0;
    }

    default public void forgive(PlayerEntity player) {
        if (!player.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            return;
        }
        if (!player.getUuid().equals(this.getAngryAt())) {
            return;
        }
        this.stopAnger();
    }

    default public void universallyAnger() {
        this.stopAnger();
        this.chooseRandomAngerTime();
    }

    default public void stopAnger() {
        this.setAttacker(null);
        this.setAngryAt(null);
        this.setTarget(null);
        this.setAngerTime(0);
    }

    @Nullable
    public LivingEntity getAttacker();

    public void setAttacker(@Nullable LivingEntity var1);

    public void setAttacking(@Nullable PlayerEntity var1);

    public void setTarget(@Nullable LivingEntity var1);

    public boolean canTarget(LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}

