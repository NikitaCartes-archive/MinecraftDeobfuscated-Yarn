/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Angerable {
    public int getAngerTime();

    public void setAngerTime(int var1);

    @Nullable
    public UUID getAngryAt();

    public void setAngryAt(@Nullable UUID var1);

    public void chooseRandomAngerTime();

    default public void angerToTag(CompoundTag tag) {
        tag.putInt("AngerTime", this.getAngerTime());
        if (this.getAngryAt() != null) {
            tag.putUuid("AngryAt", this.getAngryAt());
        }
    }

    default public void angerFromTag(World world, CompoundTag tag) {
        this.setAngerTime(tag.getInt("AngerTime"));
        if (tag.containsUuid("AngryAt")) {
            PlayerEntity playerEntity;
            this.setAngryAt(tag.getUuid("AngryAt"));
            UUID uUID = this.getAngryAt();
            PlayerEntity playerEntity2 = playerEntity = uUID == null ? null : world.getPlayerByUuid(uUID);
            if (playerEntity != null) {
                this.setAttacker(playerEntity);
                this.method_29505(playerEntity);
            }
        }
    }

    default public void tickAngerLogic() {
        LivingEntity livingEntity = this.getTarget();
        if (livingEntity != null && livingEntity.getType() == EntityType.PLAYER) {
            this.setAngryAt(livingEntity.getUuid());
            if (this.getAngerTime() <= 0) {
                this.chooseRandomAngerTime();
            }
        } else {
            int i = this.getAngerTime();
            if (i > 0) {
                this.setAngerTime(i - 1);
                if (this.getAngerTime() == 0) {
                    this.setAngryAt(null);
                }
            }
        }
    }

    default public boolean shouldAngerAt(LivingEntity entity) {
        if (entity instanceof PlayerEntity && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(entity)) {
            boolean bl = entity.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER);
            return bl ? this.hasAngerTime() : entity.getUuid().equals(this.getAngryAt());
        }
        return false;
    }

    default public boolean hasAngerTime() {
        return this.getAngerTime() > 0;
    }

    default public void forgive(PlayerEntity player) {
        if (!player.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            // empty if block
        }
        if (!player.getUuid().equals(this.getAngryAt())) {
            return;
        }
        this.setAttacker(null);
        this.setAngryAt(null);
        this.setTarget(null);
        this.setAngerTime(0);
    }

    public void setAttacker(@Nullable LivingEntity var1);

    public void method_29505(@Nullable PlayerEntity var1);

    public void setTarget(@Nullable LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}

